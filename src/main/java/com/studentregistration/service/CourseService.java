package com.studentregistration.service;

import com.studentregistration.exception.EntityNotFoundException;
import com.studentregistration.exception.ValidationException;
import com.studentregistration.model.Course;
import com.studentregistration.repository.CourseRepository;
import com.studentregistration.util.Validator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Business operations for the course catalogue.
 *
 * <p>The rule "a course with active enrollments cannot be removed" also involves
 * registrations, so it is enforced by {@code RegistrationService} before calling
 * {@link #deleteCourse(String)}.</p>
 */
public class CourseService {

    private final CourseRepository repository;
    private final List<Course> courses = new ArrayList<>();

    /**
     * Loads the persisted courses once.
     *
     * @param repository course repository
     */
    public CourseService(CourseRepository repository) {
        this.repository = repository;
        this.courses.addAll(repository.findAll());
    }

    /**
     * @return copy of all courses ordered by identifier
     */
    public List<Course> getAllCourses() {
        List<Course> copy = new ArrayList<>(courses);
        copy.sort(Comparator.comparing(Course::getCourseId));
        return List.copyOf(copy);
    }

    /**
     * @return number of offered courses
     */
    public int count() {
        return courses.size();
    }

    /**
     * @return total number of seats across all courses
     */
    public int getTotalCapacity() {
        return courses.stream().mapToInt(Course::getCapacity).sum();
    }

    /**
     * @return total number of enrolled students across all courses
     */
    public int getTotalEnrollment() {
        return courses.stream().mapToInt(Course::getEnrollmentCount).sum();
    }

    /**
     * Finds a course by identifier, ignoring case.
     *
     * @param courseId course identifier
     * @return matching course when present
     */
    public Optional<Course> findOptional(String courseId) {
        if (courseId == null) {
            return Optional.empty();
        }
        String key = courseId.trim().toUpperCase(Locale.ROOT);
        return courses.stream()
                .filter(course -> course.getCourseId().equalsIgnoreCase(key))
                .findFirst();
    }

    /**
     * Finds a course or fails with a user-friendly message.
     *
     * @param courseId course identifier
     * @return the course
     * @throws EntityNotFoundException when no course has this identifier
     */
    public Course findById(String courseId) {
        return findOptional(courseId).orElseThrow(() ->
                new EntityNotFoundException("No course found with ID " + courseId + "."));
    }

    /**
     * Adds a course after validating every field.
     *
     * @return the created course
     * @throws ValidationException when a field is invalid or the ID is taken
     */
    public Course addCourse(String courseId, String title, int creditHours, int capacity,
                            String instructor, String schedule) {
        String id = Validator.requireId(courseId, "Course ID");
        String courseTitle = Validator.requireText(title, "Course title");
        if (creditHours < 1 || creditHours > 6) {
            throw new ValidationException("Credit hours must be between 1 and 6.");
        }
        if (capacity < 1 || capacity > 500) {
            throw new ValidationException("Capacity must be between 1 and 500.");
        }
        String courseInstructor = Validator.requireText(instructor, "Instructor");
        if (findOptional(id).isPresent()) {
            throw new ValidationException("Course ID " + id + " already exists.");
        }

        Course course = new Course(id, courseTitle, creditHours, capacity, courseInstructor, schedule);
        courses.add(course);
        persist();
        return course;
    }

    /**
     * Updates the mutable fields of a course. The identifier never changes.
     *
     * @throws EntityNotFoundException when the course does not exist
     * @throws ValidationException     when a field is invalid
     */
    public void updateCourse(String courseId, String title, int creditHours, int capacity,
                             String instructor, String schedule) {
        Course course = findById(courseId);
        String courseTitle = Validator.requireText(title, "Course title");
        if (creditHours < 1 || creditHours > 6) {
            throw new ValidationException("Credit hours must be between 1 and 6.");
        }
        if (capacity < 1 || capacity > 500) {
            throw new ValidationException("Capacity must be between 1 and 500.");
        }
        if (capacity < course.getEnrollmentCount()) {
            throw new ValidationException("Capacity cannot be lower than the "
                    + course.getEnrollmentCount() + " students already enrolled.");
        }
        String courseInstructor = Validator.requireText(instructor, "Instructor");

        course.setTitle(courseTitle);
        course.setCreditHours(creditHours);
        course.setCapacity(capacity);
        course.setInstructor(courseInstructor);
        course.setSchedule(schedule);
        persist();
    }

    /**
     * Deletes a course that has no active registrations.
     *
     * @throws EntityNotFoundException when the course does not exist
     */
    public void deleteCourse(String courseId) {
        Course course = findById(courseId);
        courses.remove(course);
        persist();
    }

    /**
     * Searches courses by ID, title or instructor.
     *
     * @param keyword free text; blank returns every course
     * @return matching courses ordered by identifier
     */
    public List<Course> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllCourses();
        }
        String needle = keyword.trim().toLowerCase(Locale.ROOT);
        return getAllCourses().stream()
                .filter(course ->
                        course.getCourseId().toLowerCase(Locale.ROOT).contains(needle)
                                || course.getTitle().toLowerCase(Locale.ROOT).contains(needle)
                                || course.getInstructor().toLowerCase(Locale.ROOT).contains(needle))
                .toList();
    }

    private void persist() {
        repository.saveAll(courses);
    }
}
