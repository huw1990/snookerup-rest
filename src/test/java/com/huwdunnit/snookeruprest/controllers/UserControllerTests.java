package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.db.UserRepository;
import com.huwdunnit.snookeruprest.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

/**
 * Tests for the UserController class.
 *
 * @author Huwdunnit
 */
public class UserControllerTests {

    private static final String RONNIE_EMAIL = "ronnieo@example.com";
    private static final String RONNIE_FIRST_NAME = "Ronnie";

    private static final String RONNIE_LAST_NAME = "O'Sullivan";

    private UserRepository mockUserRepository;

    private UserController userController;

    @BeforeEach
    public void beforeEach() {
        mockUserRepository = mock(UserRepository.class);

        userController = new UserController(mockUserRepository);
    }

    @Test
    public void addUser_Should_AddUserAndReturnWithId() {
        // Define variables
        User userToAdd = new User();
        userToAdd.setEmail(RONNIE_EMAIL);
        userToAdd.setFirstName(RONNIE_FIRST_NAME);
        userToAdd.setLastName(RONNIE_LAST_NAME);
        User expectedUser = new User();
        expectedUser.setEmail(RONNIE_EMAIL);
        expectedUser.setFirstName(RONNIE_FIRST_NAME);
        expectedUser.setLastName(RONNIE_LAST_NAME);
        expectedUser.setId("1234");

        // Set mock expectations
        when(mockUserRepository.insert(any(User.class))).thenReturn(expectedUser);

        // Execute method under test
        User addedUser = userController.addUser(userToAdd);

        // Verify
        assertNotNull(addedUser);
        assertEquals(expectedUser, addedUser);

        verify(mockUserRepository).insert(any(User.class));
    }

    @Test
    public void addUser_Should_ThrowException_When_UserWithDuplicateEmailAdded() {
        // Define variables
        User userToAdd = new User();
        userToAdd.setEmail(RONNIE_EMAIL);
        userToAdd.setFirstName(RONNIE_FIRST_NAME);
        userToAdd.setLastName(RONNIE_LAST_NAME);

        // Set mock expectations
        when(mockUserRepository.insert(any(User.class))).thenThrow(new DuplicateKeyException("Duplicate email"));

        // Execute method under test
        try {
            userController.addUser(userToAdd);
            fail("Expected DuplicateKeyException to be thrown");
        } catch (DuplicateKeyException ex) {
            // Exception expected, test passed
        }

        // Verify
    }
}
