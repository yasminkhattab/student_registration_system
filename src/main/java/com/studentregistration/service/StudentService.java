package com.studentregistration.service;

import com.studentregistration.exception.EntityNotFoundException;
import com.studentregistration.exception.ValidationException;
import com.studentregistration.model.Student;
import com.studentregistration.repository.StudentRepository;
import com.studentregistration.util.Validator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Business operations for student records.
 *
 * <p>The service keeps an in-memory list (the working set used by the GUI) and
 * writes the complete list back to the CSV repository after every change. It
 * contains no JavaFX code, which is what makes it unit-testable.</p>
 */
public class StudentService {

    private final StudentRepository repository;
    private final List<Student> students = new ArrayList<>();

    /**
     * Loads the persisted students once.
     *
     * @param repository student repository
     */
    public StudentService(StudentRepository repository) {
        this.repository = repository;
        this.students.addAll(repository.findAll());
    }

    /**
     * @return copy of all students ordered by identifier
     */
    public List<Student> getAllStudents() {
        List<Student> copy = new ArrayList<>(students);
        copy.sort(Comparator.comparing(Student::getId));
        return List.copyOf(copy);
    }

    /**
     * @return number of stored students
     */
    public int count() {
        return students.size();
    }

    /**
     * Finds a student by identifier, ignoring case.
     *
     * @param id student identifier
     * @return matching student when present
     */
    public Optional<Student> findOptional(String id) {
        if (id == null) {
            return Optional.empty();
        }
        String key = id.trim().toUpperCase(Locale.ROOT);
        return students.stream()
                .filter(student -> student.getId().equalsIgnoreCase(key))
                .findFirst();
    }

    /**
     * Finds a student or fails with a user-friendly message.
     *
     * @param id student identifier
     * @return the student
     * @throws EntityNotFoundException when no student has this identifier
     */
    public Student findById(String id) {
        return findOptional(id).orElseThrow(() ->
                new EntityNotFoundException("No student found with ID " + id + "."));
    }

    /**
     * Adds a student after validating every field.
     *
     * @return the created student
     * @throws ValidationException when a field is invalid or the ID is taken
     */
    public Student addStudent(String id, String name, String email, String phone,
                              String major, int level) {
        String studentId = Validator.requireId(id, "Student ID");
        String studentName = Validator.requireText(name, "Name");
        String studentEmail = Validator.requireEmail(email);
        String studentPhone = Validator.requirePhone(phone);
        String studentMajor = Validator.requireText(major, "Major");
        if (level < 1 || level > 8) {
            throw new ValidationException("Level must be between 1 and 8.");
        }
        if (findOptional(studentId).isPresent()) {
            throw new ValidationException("Student ID " + studentId + " already exists.");
        }

        Student student = new Student(studentId, studentName, studentEmail, studentPhone,
                studentMajor, level);
        students.add(student);
        persist();
        return student;
    }

    /**
     * Overloaded convenience method accepting an already built {@link Student}.
     *
     * @param student student to add
     * @return the created student
     * @throws ValidationException when a field is invalid or the ID is taken
     */
    public Student addStudent(Student student) {
        if (student == null) {
            throw new ValidationException("Student data is required.");
        }
        return addStudent(student.getId(), student.getName(), student.getEmail(),
                student.getPhone(), student.getMajor(), student.getLevel());
    }

    /**
     * Updates the mutable fields of an existing student.
     *
     * @throws EntityNotFoundException when the student does not exist
     * @throws ValidationException     when a field is invalid
     */
    public void updateStudent(String id, String name, String email, String phone,
                              String major, int level) {
        Student student = findById(id);
        String studentName = Validator.requireText(name, "Name");
        String studentEmail = Validator.requireEmail(email);
        String studentPhone = Validator.requirePhone(phone);
        String studentMajor = Validator.requireText(major, "Major");
        if (level < 1 || level > 8) {
            throw new ValidationException("Level must be between 1 and 8.");
        }

        student.setName(studentName);
        student.setEmail(studentEmail);
        student.setPhone(studentPhone);
        student.setMajor(studentMajor);
        student.setLevel(level);
        persist();
    }

    /**
     * Deletes a student. Callers must first make sure the student has no active
     * registrations; that cross-entity rule lives in {@code RegistrationService}.
     *
     * @throws EntityNotFoundException when the student does not exist
     */
    public void deleteStudent(String id) {
        Student student = findById(id);
        students.remove(student);
        persist();
    }

    /**
     * Searches students by ID, name, e-mail or major.
     *
     * @param keyword free text; blank returns every student
     * @return matching students ordered by identifier
     */
    public List<Student> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllStudents();
        }
        String needle = keyword.trim().toLowerCase(Locale.ROOT);
        return getAllStudents().stream()
                .filter(student ->
                        student.getId().toLowerCase(Locale.ROOT).contains(needle)
                                || student.getName().toLowerCase(Locale.ROOT).contains(needle)
                                || student.getEmail().toLowerCase(Locale.ROOT).contains(needle)
                                || student.getMajor().toLowerCase(Locale.ROOT).contains(needle))
                .toList();
    }

    private void persist() {
        repository.saveAll(students);
    }
}
