package com.studentregistration.model;

import java.util.Objects;

/**
 * Common base type for every person that uses the registration system.
 *
 * <p>Demonstrates <strong>abstraction</strong> (the class cannot be instantiated),
 * <strong>encapsulation</strong> (all attributes are private and exposed through
 * accessors/mutators) and <strong>inheritance</strong> (see {@link Student} and
 * {@link Administrator}).</p>
 */
public abstract class Person {

    private String id;
    private String name;
    private String email;
    private String phone;

    /**
     * Creates an uninitialised person.
     *
     * <p>This no-argument constructor is required by the project brief's
     * "no-argument and parameterized constructors" rule. Normal application
     * flows use the parameterized constructor; when this one is used for a
     * demonstration, populate the object through the setters before persisting
     * it.</p>
     */
    protected Person() {
        this.id = "";
        this.name = "";
        this.email = "";
        this.phone = "";
    }

    /**
     * Creates a person. The constructor is {@code protected} because only
     * subclasses may create instances.
     *
     * @param id    unique identifier (student number or administrator number)
     * @param name  full name
     * @param email e-mail address
     * @param phone contact phone number
     * @throws IllegalArgumentException if a required value is missing
     */
    protected Person(String id, String name, String email, String phone) {
        this.id = requireNonBlank(id, "id");
        this.name = requireNonBlank(name, "name");
        this.email = requireNonBlank(email, "email");
        this.phone = requireNonBlank(phone, "phone");
    }

    /**
     * Returns the role of this person as shown in the user interface.
     *
     * @return {@code "Student"} or {@code "Administrator"}
     */
    public abstract String getRole();

    /**
     * Builds a short, human-readable summary of the contact details.
     * Subclasses override this method to add role-specific information.
     *
     * @return contact summary text
     */
    public String getContactSummary() {
        return name + " <" + email + "> | " + phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = requireNonBlank(id, "id");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = requireNonBlank(name, "name");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = requireNonBlank(email, "email");
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = requireNonBlank(phone, "phone");
    }

    @Override
    public String toString() {
        return getRole() + "{id='" + id + "', name='" + name + "'}";
    }

    /**
     * Two people are equal when they have the same concrete class and identifier.
     * Identity (database-independent) is used instead of the mutable attributes.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        return id.equals(((Person) other).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), id);
    }

    /**
     * Small helper shared by subclasses so validation messages stay consistent.
     *
     * @param value value to check
     * @param field field name used in the error message
     * @return the trimmed value
     * @throws IllegalArgumentException if the value is {@code null} or blank
     */
    protected static String requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }
}
