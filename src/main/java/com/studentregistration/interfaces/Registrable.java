package com.studentregistration.interfaces;

import com.studentregistration.model.Student;

import java.util.List;

/**
 * Contract for anything students can register in.
 *
 * <p>Only {@link com.studentregistration.model.Course} implements this interface
 * today, but the abstraction keeps enrollment logic independent from the GUI and
 * would allow other registrable activities (labs, workshops) later.</p>
 */
public interface Registrable {

    /**
     * Enrolls a student.
     *
     * @param student student to enroll
     * @throws com.studentregistration.exception.ValidationException   if the student is {@code null}
     * @throws com.studentregistration.exception.DuplicateEnrollmentException
     *         if the student is already enrolled
     * @throws com.studentregistration.exception.CourseFullException   if no seat is available
     */
    void enroll(Student student);

    /**
     * Removes a student from the registration list.
     *
     * @param student student to remove
     * @throws com.studentregistration.exception.EntityNotFoundException if the student is not enrolled
     */
    void drop(Student student);

    /**
     * @param student student to look up
     * @return {@code true} when the student is currently enrolled
     */
    boolean isEnrolled(Student student);

    /**
     * @return {@code true} when every seat is taken
     */
    boolean isFull();

    /**
     * @return number of seats that are still free
     */
    int getAvailableSeats();

    /**
     * @return number of currently enrolled students
     */
    int getEnrollmentCount();

    /**
     * @return an unmodifiable view of the enrolled students
     */
    List<Student> getEnrolledStudents();
}
