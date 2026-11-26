package com.studentregistration.model;

/**
 * A student who can log in with a student identifier and enroll in courses.
 */
public class Student extends Person {

    private String major;
    private int level;

    /**
     * Creates an uninitialised student (no-argument constructor required by the
     * project brief). Fill the fields with setters before using the object.
     */
    public Student() {
        this.major = "Undeclared";
        this.level = 1;
    }

    /**
     * Convenience constructor used when only the personal details are known.
     * Demonstrates <strong>constructor overloading</strong>.
     *
     * @param id    student identifier
     * @param name  full name
     * @param email e-mail address
     * @param phone phone number
     */
    public Student(String id, String name, String email, String phone) {
        this(id, name, email, phone, "Undeclared", 1);
    }

    /**
     * Creates a fully described student.
     *
     * @param id    student identifier, for example {@code STU-1001}
     * @param name  full name
     * @param email e-mail address
     * @param phone phone number
     * @param major academic major
     * @param level academic level (1-8)
     */
    public Student(String id, String name, String email, String phone, String major, int level) {
        super(id, name, email, phone);
        setMajor(major);
        setLevel(level);
    }

    @Override
    public String getRole() {
        return "Student";
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = requireNonBlank(major, "major");
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if (level < 1 || level > 8) {
            throw new IllegalArgumentException("level must be between 1 and 8");
        }
        this.level = level;
    }

    /**
     * Adds the academic major and level to the base contact summary.
     * Demonstrates <strong>method overriding</strong>.
     */
    @Override
    public String getContactSummary() {
        return super.getContactSummary() + " | " + major + ", level " + level;
    }
}
