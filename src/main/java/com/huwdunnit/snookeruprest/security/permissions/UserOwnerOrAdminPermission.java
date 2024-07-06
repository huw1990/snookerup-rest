package com.huwdunnit.snookeruprest.security.permissions;

import com.huwdunnit.snookeruprest.security.Roles;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation used as part of Spring Security's method level security.
 *
 * When set on a method in a RestController, only authenticated users either with user access and a user ID matching
 * the provided user ID, or users with admin privileges, are allowed access.
 *
 * @author Huwdunnit
 */
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('" + Roles.USER + "') && #userId == principal.getId() || hasRole('" + Roles.ADMIN + "')")
public @interface UserOwnerOrAdminPermission {
}
