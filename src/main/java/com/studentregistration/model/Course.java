package com.studentregistration.model;

import com.studentregistration.exception.CourseFullException;
import com.studentregistration.exception.DuplicateEnrollmentException;
import com.studentregistration.exception.EntityNotFoundException;
import com.studentregistration.exception.ValidationException;
import com.studentregistration.interfaces.Registrable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A course offered in the current semester.
 *
 * <p>The class <strong>implements</strong> {@link Registrable} and keeps an
 * in-memory list of enrolled {@link Student} objects (an <strong>aggregation</strong>:
 * students exist independently of the course). Capacity rules are enforced here
 * so that every client goes through the same validation.</p>
 */
public class Course implements Registrable {

    private String courseId;
    private String title;
    private int creditHours;
    private int capacity;
    private String instructor;
    private String schedule;

    /** Aggregated students; the course does not own their lifecycle. */
    private final List<Student> enrolledStudents = new ArrayList<>();

    /**
     * Creates an empty course (no-argument constructor required by the project
     * brief). Populate the fields through setters before using the object.
     */
    public Course() {
        this.courseId = "";
        this.title = "";
        this.creditHours = 3;
        this.capacity = 30;
        this.instructor = "";
        this.schedule = "Not scheduled";
    }

    /**
     * Creates a course with the default "not scheduled" timetable.
     *
     * @param courseId    course identifier, for example {@code CS113}
     * @param title       course title
     * @param creditHours number of credit hours
     * @param capacity    maximum number of students
     * @param instructor  instructor name
     */
    public Course(String courseId, String title, int creditHours, int capacity, String instructor) {
        this(courseId, title, creditHours, capacity, instructor, "Not scheduled");
    }

    /**
     * Creates a fully described course.
     *
     * @param courseId    course identifier, for example {@code CS113}
     * @param title       course title
     * @param creditHours number of credit hours
     * @param capacity    maximum number of students
     * @param instructor  instructor name
     * @param schedule    weekly timetable, for example {@code Sun/Tue 10:00-11:30}
     */
    public Course(String courseId, String title, int creditHours, int capacity,
                  String instructor, String schedule) {
        setCourseId(courseId);
        setTitle(title);
        setCreditHours(creditHours);
        setCapacity(capacity);
        setInstructor(instructor);
        setSchedule(schedule);
    }

    /**
     * {@inheritDoc}
     *
     * <p>This is an <strong>overriding</strong> method: {@link Registrable} declares
     * the contract, {@code Course} provides the rule implementation.</p>
     */
    @Override
    public void enroll(Student student) {
        if (student == null) {
            throw new ValidationException("A student must be selected before enrolling.");
        }
        if (isEnrolled(student)) {
            throw new DuplicateEnrollmentException(
                    "Student " + student.getId() + " is already enrolled in " + courseId + ".");
        }
        if (isFull()) {
            throw new CourseFullException(
                    "Course " + courseId + " is full (" + capacity + " seats).");
        }
        enrolledStudents.add(student);
    }

    @Override
    public void drop(Student student) {
        if (student == null) {
            throw new ValidationException("A student must be selected before dropping a course.");
        }
        boolean removed = enrolledStudents.removeIf(enrolled -> enrolled.getId().equals(student.getId()));
        if (!removed) {
            throw new EntityNotFoundException(
                    "Student " + student.getId() + " is not enrolled in " + courseId + ".");
        }
    }

    @Override
    public boolean isEnrolled(Student student) {
        if (student == null) {
            return false;
        }
        return enrolledStudents.stream().anyMatch(enrolled -> enrolled.getId().equals(student.getId()));
    }

    @Override
    public boolean isFull() {
        return enrolledStudents.size() >= capacity;
    }

    @Override
    public int getAvailableSeats() {
        return Math.max(0, capacity - enrolledStudents.size());
    }

    @Override
    public int getEnrollmentCount() {
        return enrolledStudents.size();
    }

    @Override
    public List<Student> getEnrolledStudents() {
        return Collections.unmodifiableList(enrolledStudents);
    }

    /**
     * Re-attaches a student while loading persisted registrations at start-up.
     *
     * <p>Normal business flows must call {@link #enroll(Student)} instead; this
     * method deliberately skips the capacity check because the stored data has
     * already been accepted previously.</p>
     *
     * @param student student to re-attach
     */
    public void restoreEnrollment(Student student) {
        Objects.requireNonNull(student, "student");
        if (!isEnrolled(student)) {
            enrolledStudents.add(student);
        }
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        if (courseId == null || courseId.isBlank()) {
            throw new ValidationException("Course ID must not be blank.");
        }
        this.courseId = courseId.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new ValidationException("Course title must not be blank.");
        }
        this.title = title.trim();
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int creditHours) {
        if (creditHours < 1 || creditHours > 6) {
            throw new ValidationException("Credit hours must be between 1 and 6.");
        }
        this.creditHours = creditHours;
    }

    public int getCapacity() {
        return capacity;
    }

    /**
     * Changes the capacity. The value can never be lower than the number of
     * students already enrolled.
     *
     * @param capacity new maximum number of students
     */
    public void setCapacity(int capacity) {
        if (capacity < enrolledStudents.size()) {
            throw new ValidationException(
                    "Capacity cannot be lower than the current enrollment ("
                            + enrolledStudents.size() + " students).");
        }
        if (capacity < 1) {
            throw new ValidationException("Capacity must be at least 1.");
        }
        this.capacity = capacity;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        if (instructor == null || instructor.isBlank()) {
            throw new ValidationException("Instructor name must not be blank.");
        }
        this.instructor = instructor.trim();
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = (schedule == null || schedule.isBlank()) ? "Not scheduled" : schedule.trim();
    }

    @Override
    public String toString() {
        return courseId + " - " + title;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Course course)) {
            return false;
        }
        return courseId.equals(course.courseId);
    }

    @Override
    public int hashCode() {
        return courseId.hashCode();
    }
}
