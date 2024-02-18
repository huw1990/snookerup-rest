package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.BaseIT;
import com.huwdunnit.snookeruprest.db.IdGenerator;
import com.huwdunnit.snookeruprest.model.User;
import com.huwdunnit.snookeruprest.model.errors.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
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
        User userToAdd = getNewUserForTest();
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
        User userToAdd = getNewUserForTest();

        // Add user to DB before running test
        User existingUser = getNewUserForTest();
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

    private User getNewUserForTest() {
        User user = new User();
        user.setFirstName("Ronnie");
        user.setLastName("O'Sullivan");
        user.setEmail("ronnieo@example.com");
        return user;
    }
}
