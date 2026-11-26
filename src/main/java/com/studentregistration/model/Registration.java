package com.studentregistration.model;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Records that a student registered in (or dropped) a course.
 *
 * <p>Registrations are the persistent source of truth for enrollments: the
 * in-memory {@link Course} list is rebuilt from this data at start-up.</p>
 */
public class Registration {

    private final String registrationId;
    private final String studentId;
    private final String courseId;
    private final LocalDateTime registeredAt;
    private RegistrationStatus status;

    /**
     * Creates an empty registration (no-argument constructor required by the
     * project brief). Normal flows use {@link #create(String, String)} or the
     * parameterized constructor.
     */
    public Registration() {
        this("", "", "", LocalDateTime.now(), RegistrationStatus.ACTIVE);
    }

    /**
     * Creates a registration from persisted data.
     *
     * @param registrationId unique registration identifier
     * @param studentId      identifier of the enrolled student
     * @param courseId       identifier of the course
     * @param registeredAt   date and time of registration
     * @param status         current status
     */
    public Registration(String registrationId, String studentId, String courseId,
                        LocalDateTime registeredAt, RegistrationStatus status) {
        this.registrationId = Objects.requireNonNull(registrationId, "registrationId");
        this.studentId = Objects.requireNonNull(studentId, "studentId");
        this.courseId = Objects.requireNonNull(courseId, "courseId");
        this.registeredAt = Objects.requireNonNull(registeredAt, "registeredAt");
        this.status = Objects.requireNonNull(status, "status");
    }

    /**
     * Factory method used when a student enrolls in a course.
     *
     * @param studentId identifier of the student
     * @param courseId  identifier of the course
     * @return a new active registration stamped with the current time
     */
    public static Registration create(String studentId, String courseId) {
        return new Registration(newRegistrationId(), studentId, courseId, LocalDateTime.now(),
                RegistrationStatus.ACTIVE);
    }

    private static String newRegistrationId() {
        return "REG-" + UUID.randomUUID().toString()
                .substring(0, 8)
                .toUpperCase(Locale.ROOT);
    }

    public void markDropped() {
        this.status = RegistrationStatus.DROPPED;
    }

    /**
     * Restores the active status after a failed persistence attempt.
     * Normal business flows do not call this method directly.
     */
    public void markActive() {
        this.status = RegistrationStatus.ACTIVE;
    }

    public boolean isActive() {
        return status == RegistrationStatus.ACTIVE;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Registration{" + registrationId + ", student=" + studentId
                + ", course=" + courseId + ", status=" + status + "}";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Registration registration)) {
            return false;
        }
        return registrationId.equals(registration.registrationId);
    }

    @Override
    public int hashCode() {
        return registrationId.hashCode();
    }
}
