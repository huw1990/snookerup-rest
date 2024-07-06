package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.BaseIT;
import com.huwdunnit.snookeruprest.db.IdGenerator;
import com.huwdunnit.snookeruprest.model.User;
import com.huwdunnit.snookeruprest.model.errors.ErrorResponse;
import com.huwdunnit.snookeruprest.security.Roles;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the UserController class.
 *
 * @author Huwdunnit
 */
@SpringBootTest
@ActiveProfiles(profiles = "test")
public class UserControllerTestsIT extends BaseIT {

    @Test
    void addUser_Should_Return201ResponseWithAddedUser() throws Exception {
        User userToAdd = getRonnieUser();
        String requestBody = objectMapper.writeValueAsString(userToAdd);

        MvcResult result = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("$.id").exists(),
                        jsonPath("$.firstName").value(userToAdd.getFirstName()),
                        jsonPath("$.lastName").value(userToAdd.getLastName()),
                        jsonPath("$.email").value(userToAdd.getEmail()))
                .andReturn();

        // Get the user's ID so we can check it exists in the DB
        User userInResponse = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
        String addedUserId = userInResponse.getId();

        // Get the user by ID from the DB.
        Optional<User> opt = userRepository.findById(addedUserId);

        opt.ifPresentOrElse(
                (userInDb) -> assertEquals(userInResponse, userInDb, "User returned in response is different to user in DB"),
                () -> fail("User with ID from response not found in the DB")
        );
    }

    @Test
    void addUser_Should_Return400Response_When_EmailAlreadyExists() throws Exception {
        User userToAdd = getRonnieUser();

        // Add user to DB before running test
        User existingUser = getRonnieUser();
        existingUser.setId(IdGenerator.createNewId());
        userRepository.insert(existingUser);

        // Now add same user via REST (error expected)
        String requestBody = objectMapper.writeValueAsString(userToAdd);

        MvcResult result = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.errorMessage").value(ErrorResponse.DUPLICATE_FIELD))
                .andReturn();
    }

    @WithMockUser(authorities = Roles.ADMIN)
    @Test
    void getAllUsers_Should_EmptyUsersPage_When_NoUsersInDb() throws Exception {
        int pageSize = 50;
        int pageToGet = 0;
        int expectedNumberOfPages = 0;
        int expectedTotalItems = 0;

        // Get the first page of users
        mockMvc.perform(get("/api/v1/users?pageSize={page-size}&pageNumber={page-number}",
                        pageSize, pageToGet))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.users").isEmpty())
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @WithMockUser(authorities = Roles.ADMIN)
    @Test
    void getAllUsers_Should_UsersInOnePage_When_OnlyTwoUsersInDb() throws Exception {
        // Add users to DB before running test
        User ronnieInDb = getRonnieUser();
        ronnieInDb.setId(IdGenerator.createNewId());
        userRepository.insert(ronnieInDb);
        User hendryInDb = getHendryUser();
        hendryInDb.setId(IdGenerator.createNewId());
        userRepository.insert(hendryInDb);

        int pageSize = 50;
        int pageToGet = 0;
        int expectedNumberOfPages = 1;
        int expectedTotalItems = 2;

        // Get the first page of users
        mockMvc.perform(get("/api/v1/users?pageSize={page-size}&pageNumber={page-number}",
                        pageSize, pageToGet))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.users[0].id").value(ronnieInDb.getId()),
                        jsonPath("$.users[0].firstName").value(ronnieInDb.getFirstName()),
                        jsonPath("$.users[0].lastName").value(ronnieInDb.getLastName()),
                        jsonPath("$.users[0].email").value(ronnieInDb.getEmail()))
                .andExpectAll(
                        jsonPath("$.users[1].id").value(hendryInDb.getId()),
                        jsonPath("$.users[1].firstName").value(hendryInDb.getFirstName()),
                        jsonPath("$.users[1].lastName").value(hendryInDb.getLastName()),
                        jsonPath("$.users[1].email").value(hendryInDb.getEmail()))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @WithMockUser(authorities = Roles.ADMIN)
    @Test
    void getAllUsers_Should_UsersInTwoPages_When_RequestedPagesOfTwoButThreeUsersInDb() throws Exception {
        // Add users to DB before running test
        User ronnieInDb = getRonnieUser();
        ronnieInDb.setId(IdGenerator.createNewId());
        userRepository.insert(ronnieInDb);
        User hendryInDb = getHendryUser();
        hendryInDb.setId(IdGenerator.createNewId());
        userRepository.insert(hendryInDb);
        User willoInDb = getWilloUser();
        willoInDb.setId(IdGenerator.createNewId());
        userRepository.insert(willoInDb);

        int pageSize = 2;
        int pageToGet = 0;
        int expectedNumberOfPages = 2;
        int expectedTotalItems = 3;

        // Get the first page of users
        mockMvc.perform(get("/api/v1/users?pageSize={page-size}&pageNumber={page-number}",
                        pageSize, pageToGet))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.users[0].id").value(ronnieInDb.getId()),
                        jsonPath("$.users[0].firstName").value(ronnieInDb.getFirstName()),
                        jsonPath("$.users[0].lastName").value(ronnieInDb.getLastName()),
                        jsonPath("$.users[0].email").value(ronnieInDb.getEmail()))
                .andExpectAll(
                        jsonPath("$.users[1].id").value(hendryInDb.getId()),
                        jsonPath("$.users[1].firstName").value(hendryInDb.getFirstName()),
                        jsonPath("$.users[1].lastName").value(hendryInDb.getLastName()),
                        jsonPath("$.users[1].email").value(hendryInDb.getEmail()))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));


        pageToGet = 1;
        // Get the second page of users
        mockMvc.perform(get("/api/v1/users?pageSize={page-size}&pageNumber={page-number}",
                        pageSize, pageToGet))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.users[0].id").value(willoInDb.getId()),
                        jsonPath("$.users[0].firstName").value(willoInDb.getFirstName()),
                        jsonPath("$.users[0].lastName").value(willoInDb.getLastName()),
                        jsonPath("$.users[0].email").value(willoInDb.getEmail()))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @Test
    void getUserById_Should_Return200ResponseWithUser_When_UserExists() throws Exception {
        String userId = "1234";
        User ronnieUser = getRonnieUser();
        ronnieUser.setId(userId);
        userRepository.insert(ronnieUser);

        mockMvc.perform(get("/api/v1/users/{user-id}", userId)
                        .with(user(getPrincipalForUser(ronnieUser))))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.id").value(ronnieUser.getId()),
                        jsonPath("$.firstName").value(ronnieUser.getFirstName()),
                        jsonPath("$.lastName").value(ronnieUser.getLastName()),
                        jsonPath("$.email").value(ronnieUser.getEmail()));
    }

    @WithMockUser(authorities = Roles.ADMIN)
    @Test
    void getUserById_Should_Return404Response_When_UserNotFound() throws Exception {
        String invalidUserId = "1234";

        mockMvc.perform(get("/api/v1/users/{user-id}", invalidUserId))
                .andExpect(status().isNotFound())
                .andExpectAll(
                        jsonPath("$.errorMessage").value("User not found"));
    }
}
