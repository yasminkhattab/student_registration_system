package com.studentregistration.config;

import com.studentregistration.model.Administrator;

import java.nio.file.Path;

/**
 * Central application constants.
 *
 * <p>The administrator account is intentionally hard-coded for this course
 * project: the assignment asks for simple authentication, not a security
 * subsystem. The password can be overridden with the {@code SRS_ADMIN_PASSWORD}
 * environment variable.</p>
 */
public final class AppConfig {

    public static final String APP_NAME = "University Student Course Registration System";
    public static final String APP_SHORT_NAME = "Taibah Registration";
    public static final String COURSE_NAME = "CS113 Programming II";

    /** Directory (relative to the working directory) that stores the CSV files. */
    public static final Path DATA_DIRECTORY = Path.of("data");

    public static final String ADMIN_ID = "ADM-001";
    public static final String ADMIN_NAME = "System Administrator";
    public static final String ADMIN_EMAIL = "registration.admin@taibah.edu.sa";
    public static final String ADMIN_PHONE = "0500000000";
    public static final String ADMIN_DEPARTMENT = "Deanship of Admission and Registration";

    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    private AppConfig() {
        // Constants only.
    }

    /**
     * @return the password expected on the administrator login form
     */
    public static String getAdminPassword() {
        String override = System.getenv("SRS_ADMIN_PASSWORD");
        return (override == null || override.isBlank()) ? DEFAULT_ADMIN_PASSWORD : override;
    }

    /**
     * Validates the administrator credentials entered on the login screen.
     *
     * @param id       entered identifier
     * @param password entered password
     * @return {@code true} when both values match
     */
    public static boolean isValidAdminCredentials(String id, String password) {
        return id != null && password != null
                && ADMIN_ID.equalsIgnoreCase(id.trim())
                && getAdminPassword().equals(password);
    }

    /**
     * Builds the built-in administrator used by the session.
     *
     * @return a new {@link Administrator} instance
     */
    public static Administrator createAdministrator() {
        return new Administrator(ADMIN_ID, ADMIN_NAME, ADMIN_EMAIL, ADMIN_PHONE, ADMIN_DEPARTMENT);
    }
}
