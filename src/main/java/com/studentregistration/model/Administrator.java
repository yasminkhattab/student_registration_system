package com.studentregistration.model;

/**
 * A staff member allowed to manage students and courses.
 *
 * <p>The application uses a single built-in administrator account; administrator
 * records are therefore not persisted. See {@code docs/decisions}.</p>
 */
public class Administrator extends Person {

    private String department;

    /**
     * Creates an uninitialised administrator (no-argument constructor required
     * by the project brief). Fill the fields with setters before using it.
     */
    public Administrator() {
        this.department = "";
    }

    /**
     * @param id         administrator identifier, for example {@code ADM-001}
     * @param name       full name
     * @param email      e-mail address
     * @param phone      phone number
     * @param department administrative department
     */
    public Administrator(String id, String name, String email, String phone, String department) {
        super(id, name, email, phone);
        setDepartment(department);
    }

    @Override
    public String getRole() {
        return "Administrator";
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = requireNonBlank(department, "department");
    }

    @Override
    public String getContactSummary() {
        return super.getContactSummary() + " | " + department;
    }
}
