package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.db.UserRepository;
import com.huwdunnit.snookeruprest.model.User;
import com.huwdunnit.snookeruprest.model.UserListResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

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

    private static final String HENDRY_EMAIL = "hendry@example.com";
    private static final String HENDRY_FIRST_NAME = "Stephen";

    private static final String HENDRY_LAST_NAME = "Hendry";

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

    @Test
    public void getAllUsers_Should_RespondWithTwoUsersAndNoFurtherPages_When_OnlyTwoUsersInDb() {
        // Define variables
        User ronnieUser = new User();
        ronnieUser.setEmail(RONNIE_EMAIL);
        ronnieUser.setFirstName(RONNIE_FIRST_NAME);
        ronnieUser.setLastName(RONNIE_LAST_NAME);
        ronnieUser.setId("1000");
        User hendryUser = new User();
        hendryUser.setEmail(HENDRY_EMAIL);
        hendryUser.setFirstName(HENDRY_FIRST_NAME);
        hendryUser.setLastName(HENDRY_LAST_NAME);
        hendryUser.setId("1001");
        Page<User> mockUsersPage = mock(Page.class);

        // Set mock expectations
        when(mockUserRepository.findAll(any(Pageable.class))).thenReturn(mockUsersPage);
        when(mockUsersPage.getContent()).thenReturn(List.of(ronnieUser, hendryUser));
        when(mockUsersPage.getNumber()).thenReturn(0);
        when(mockUsersPage.getSize()).thenReturn(2);
        when(mockUsersPage.getTotalPages()).thenReturn(1);
        when(mockUsersPage.getTotalElements()).thenReturn(2L);

        // Execute method under test
        UserListResponse usersResponse = userController.getAllUsers(0, 50);

        // Verify
        assertEquals(2, usersResponse.getUsers().size());
        assertEquals(ronnieUser, usersResponse.getUsers().get(0));
        assertEquals(hendryUser, usersResponse.getUsers().get(1));
        assertEquals(0, usersResponse.getPageNumber());
        assertEquals(2, usersResponse.getPageSize());
        assertEquals(1, usersResponse.getTotalPages());
        assertEquals(2L, usersResponse.getTotalItems());
    }

    @Test
    public void getAllUsers_Should_RespondWithEmptyList_When_NoUsersInDb() {
        // Define variables
        Page<User> mockUsersPage = mock(Page.class);

        // Set mock expectations
        when(mockUserRepository.findAll(any(Pageable.class))).thenReturn(mockUsersPage);
        when(mockUsersPage.getContent()).thenReturn(List.of());
        when(mockUsersPage.getNumber()).thenReturn(0);
        when(mockUsersPage.getSize()).thenReturn(0);
        when(mockUsersPage.getTotalPages()).thenReturn(1);
        when(mockUsersPage.getTotalElements()).thenReturn(0L);

        // Execute method under test
        UserListResponse usersResponse = userController.getAllUsers(0, 50);

        // Verify
        assertEquals(0, usersResponse.getUsers().size());
        assertEquals(0, usersResponse.getPageNumber());
        assertEquals(0, usersResponse.getPageSize());
        assertEquals(1, usersResponse.getTotalPages());
        assertEquals(0L, usersResponse.getTotalItems());
    }

    @Test
    public void getAllUsers_Should_RespondWithTwoUsersAndOneFurtherPage_When_ThreeUsersInDb() {
        // Define variables
        User ronnieUser = new User();
        ronnieUser.setEmail(RONNIE_EMAIL);
        ronnieUser.setFirstName(RONNIE_FIRST_NAME);
        ronnieUser.setLastName(RONNIE_LAST_NAME);
        ronnieUser.setId("1000");
        User hendryUser = new User();
        hendryUser.setEmail(HENDRY_EMAIL);
        hendryUser.setFirstName(HENDRY_FIRST_NAME);
        hendryUser.setLastName(HENDRY_LAST_NAME);
        hendryUser.setId("1001");
        Page<User> mockUsersPage = mock(Page.class);

        // Set mock expectations
        when(mockUserRepository.findAll(any(Pageable.class))).thenReturn(mockUsersPage);
        when(mockUsersPage.getContent()).thenReturn(List.of(ronnieUser, hendryUser));
        when(mockUsersPage.getNumber()).thenReturn(0);
        when(mockUsersPage.getSize()).thenReturn(2);
        when(mockUsersPage.getTotalPages()).thenReturn(2);
        when(mockUsersPage.getTotalElements()).thenReturn(3L);

        // Execute method under test
        UserListResponse usersResponse = userController.getAllUsers(0, 2);

        // Verify
        assertEquals(2, usersResponse.getUsers().size());
        assertEquals(ronnieUser, usersResponse.getUsers().get(0));
        assertEquals(hendryUser, usersResponse.getUsers().get(1));
        assertEquals(0, usersResponse.getPageNumber());
        assertEquals(2, usersResponse.getPageSize());
        assertEquals(2, usersResponse.getTotalPages());
        assertEquals(3L, usersResponse.getTotalItems());
    }
}
