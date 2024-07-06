package com.huwdunnit.snookeruprest.security;

/**
 * Provides constants for the names of the different roles in the application, used to control access.
 *
 * @author Huwdunnit
 */
public interface Roles {

    /** The user role, i.e. for normal end users. */
    String USER = "ROLE_USER";

    /** The admin role, i.e. for system administrators. */
    String ADMIN = "ROLE_ADMIN";
}
