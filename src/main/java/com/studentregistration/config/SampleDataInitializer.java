package com.studentregistration.config;

import com.studentregistration.model.Course;
import com.studentregistration.model.Student;
import com.studentregistration.service.CourseService;
import com.studentregistration.service.RegistrationService;
import com.studentregistration.service.StudentService;

/**
 * Inserts a small demo data set the first time the application runs.
 *
 * <p>Demo data lives here — not in the controllers — so the GUI code never
 * contains hard-coded records. Delete the CSV files in {@code data/} to seed
 * again on the next start.</p>
 */
public final class SampleDataInitializer {

    private SampleDataInitializer() {
        // Utility class: no instances.
    }

    /**
     * Seeds students, courses and a few enrollments when the catalogue and the
     * student list are both empty.
     *
     * @param students      student service
     * @param courses       course service
     * @param registrations registration service
     */
    public static void seedIfEmpty(StudentService students, CourseService courses,
                                   RegistrationService registrations) {
        if (!students.getAllStudents().isEmpty() || !courses.getAllCourses().isEmpty()) {
            return;
        }

        Student sara = students.addStudent("STU-1001", "Sara Al-Qahtani",
                "sara.alqahtani@taibah.edu.sa", "0501000001", "Computer Science", 2);
        Student omar = students.addStudent("STU-1002", "Omar Al-Zahrani",
                "omar.alzahrani@taibah.edu.sa", "0501000002", "Computer Science", 3);
        students.addStudent("STU-1003", "Layla Al-Mutairi",
                "layla.almutairi@taibah.edu.sa", "0501000003", "Information Systems", 1);

        Course programming = courses.addCourse("CS113", "Programming II", 3, 30,
                "Dr. Mona Al-Harbi", "Sun/Tue 10:00-11:30");
        Course oop = courses.addCourse("CS210", "Object-Oriented Programming", 3, 25,
                "Dr. Faisal Al-Otaibi", "Mon/Wed 13:00-14:30");
        Course dataStructures = courses.addCourse("CS201", "Data Structures", 3, 20,
                "Dr. Huda Al-Amri", "Sun/Tue 08:00-09:30");
        courses.addCourse("IS220", "Database Fundamentals", 3, 35,
                "Dr. Khalid Al-Saadi", "Mon/Wed 10:00-11:30");

        registrations.enroll(sara.getId(), programming.getCourseId());
        registrations.enroll(sara.getId(), oop.getCourseId());
        registrations.enroll(omar.getId(), programming.getCourseId());
        registrations.enroll(omar.getId(), dataStructures.getCourseId());
    }
}
