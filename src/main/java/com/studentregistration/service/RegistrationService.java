package com.studentregistration.service;

import com.studentregistration.exception.DataAccessException;
import com.studentregistration.exception.DuplicateEnrollmentException;
import com.studentregistration.exception.EntityNotFoundException;
import com.studentregistration.exception.ValidationException;
import com.studentregistration.model.Course;
import com.studentregistration.model.Registration;
import com.studentregistration.model.Student;
import com.studentregistration.repository.RegistrationRepository;
import com.studentregistration.util.Validator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Coordinates enrollments between students, courses and the registration file.
 *
 * <p>This service is the only place that knows both students and courses, so it
 * also owns the cross-entity rules:</p>
 * <ul>
 *   <li>a student cannot enroll twice in the same course;</li>
 *   <li>a full course cannot accept more students;</li>
 *   <li>students/courses with active enrollments cannot be deleted.</li>
 * </ul>
 */
public class RegistrationService {

    private final StudentService studentService;
    private final CourseService courseService;
    private final RegistrationRepository repository;
    private final List<Registration> registrations = new ArrayList<>();

    /**
     * Loads persisted registrations and re-attaches students to their courses.
     *
     * @param studentService student business service
     * @param courseService  course business service
     * @param repository     registration repository
     */
    public RegistrationService(StudentService studentService,
                               CourseService courseService,
                               RegistrationRepository repository) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.repository = repository;
        this.registrations.addAll(repository.findAll());
        rehydrateEnrollments();
    }

    private void rehydrateEnrollments() {
        for (Registration registration : registrations) {
            if (!registration.isActive()) {
                continue;
            }
            studentService.findOptional(registration.getStudentId()).ifPresent(student ->
                    courseService.findOptional(registration.getCourseId()).ifPresent(course ->
                            course.restoreEnrollment(student)));
        }
    }

    /**
     * Enrolls a student in a course and persists a registration record.
     *
     * @param studentId student identifier
     * @param courseId  course identifier
     * @return the new active registration
     * @throws com.studentregistration.exception.EntityNotFoundException    when the student or course does not exist
     * @throws DuplicateEnrollmentException                                when already enrolled
     * @throws com.studentregistration.exception.CourseFullException       when the course is full
     */
    public Registration enroll(String studentId, String courseId) {
        Student student = studentService.findById(Validator.requireText(studentId, "Student ID"));
        Course course = courseService.findById(Validator.requireText(courseId, "Course ID"));

        if (findActive(student.getId(), course.getCourseId()).isPresent()) {
            throw new DuplicateEnrollmentException(
                    "Student " + student.getId() + " is already enrolled in " + course.getCourseId() + ".");
        }

        // The course enforces capacity and duplicate rules itself.
        course.enroll(student);

        Registration registration = Registration.create(student.getId(), course.getCourseId());
        registrations.add(registration);
        try {
            repository.saveAll(registrations);
        } catch (DataAccessException ex) {
            // Keep memory and disk consistent when the write fails.
            registrations.remove(registration);
            course.drop(student);
            throw ex;
        }
        return registration;
    }

    /**
     * Drops an active enrollment. The registration row is kept with status
     * {@code DROPPED}.
     *
     * @param studentId student identifier
     * @param courseId  course identifier
     * @throws EntityNotFoundException when no active enrollment exists
     */
    public void drop(String studentId, String courseId) {
        Student student = studentService.findById(Validator.requireText(studentId, "Student ID"));
        Course course = courseService.findById(Validator.requireText(courseId, "Course ID"));
        Registration registration = findActive(student.getId(), course.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Student " + student.getId() + " is not enrolled in " + course.getCourseId() + "."));

        course.drop(student);
        registration.markDropped();
        try {
            repository.saveAll(registrations);
        } catch (DataAccessException ex) {
            registration.markActive();
            course.restoreEnrollment(student);
            throw ex;
        }
    }

    /**
     * Deletes a student only when no active enrollment exists, and removes the
     * student's historical registration rows.
     *
     * @param studentId student identifier
     * @throws EntityNotFoundException when the student does not exist
     * @throws ValidationException     when the student is still enrolled somewhere
     */
    public void deleteStudent(String studentId) {
        Student student = studentService.findById(studentId);
        List<Registration> active = getActiveRegistrationsForStudent(student.getId());
        if (!active.isEmpty()) {
            throw new ValidationException("Cannot delete student " + student.getId()
                    + ": " + active.size() + " active enrollment(s). Drop them first.");
        }
        registrations.removeIf(registration ->
                registration.getStudentId().equalsIgnoreCase(student.getId()));
        repository.saveAll(registrations);
        studentService.deleteStudent(student.getId());
    }

    /**
     * Deletes a course only when nobody is actively enrolled, and removes the
     * course's historical registration rows.
     *
     * @param courseId course identifier
     * @throws EntityNotFoundException when the course does not exist
     * @throws ValidationException     when the course still has active enrollments
     */
    public void deleteCourse(String courseId) {
        Course course = courseService.findById(courseId);
        if (course.getEnrollmentCount() > 0) {
            throw new ValidationException("Cannot delete course " + course.getCourseId()
                    + ": " + course.getEnrollmentCount()
                    + " student(s) are enrolled. Drop them first.");
        }
        registrations.removeIf(registration ->
                registration.getCourseId().equalsIgnoreCase(course.getCourseId()));
        repository.saveAll(registrations);
        courseService.deleteCourse(course.getCourseId());
    }

    /**
     * @param studentId student identifier
     * @return active registrations of one student, newest first
     */
    public List<Registration> getActiveRegistrationsForStudent(String studentId) {
        if (studentId == null) {
            return List.of();
        }
        return registrations.stream()
                .filter(Registration::isActive)
                .filter(registration -> registration.getStudentId().equalsIgnoreCase(studentId.trim()))
                .sorted(Comparator.comparing(Registration::getRegisteredAt).reversed())
                .toList();
    }

    /**
     * @param studentId student identifier
     * @return courses the student is currently enrolled in, ordered by course ID
     */
    public List<Course> getActiveCoursesForStudent(String studentId) {
        return getActiveRegistrationsForStudent(studentId).stream()
                .map(registration -> courseService.findOptional(registration.getCourseId()))
                .flatMap(Optional::stream)
                .sorted(Comparator.comparing(Course::getCourseId))
                .toList();
    }

    /**
     * @param courseId course identifier
     * @return students currently enrolled in the course
     */
    public List<Student> getActiveStudentsForCourse(String courseId) {
        if (courseId == null) {
            return List.of();
        }
        return courseService.findOptional(courseId)
                .map(course -> course.getEnrolledStudents().stream()
                        .sorted(Comparator.comparing(Student::getId))
                        .toList())
                .orElse(List.of());
    }

    /**
     * @return number of active registrations across all courses
     */
    public int countActiveRegistrations() {
        return (int) registrations.stream().filter(Registration::isActive).count();
    }

    private Optional<Registration> findActive(String studentId, String courseId) {
        return registrations.stream()
                .filter(Registration::isActive)
                .filter(registration -> registration.getStudentId().equalsIgnoreCase(studentId))
                .filter(registration -> registration.getCourseId().equalsIgnoreCase(courseId))
                .findFirst();
    }
}
