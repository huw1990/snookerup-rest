package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.db.IdGenerator;
import com.huwdunnit.snookeruprest.db.UserRepository;
import com.huwdunnit.snookeruprest.exceptions.UserNotFoundException;
import com.huwdunnit.snookeruprest.model.User;
import com.huwdunnit.snookeruprest.model.UserListResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.huwdunnit.snookeruprest.controllers.UserController.USERS_URL;

/**
 * REST Controller for User endpoints.
 *
 * @author Huwdunnit
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(USERS_URL)
public class UserController {

    public static final String USERS_URL = "/api/v1/users";

    private final UserRepository userRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@RequestBody User userToAdd) {
        log.debug("addUser user={}", userToAdd);

        String generatedUserId = IdGenerator.createNewId();
        userToAdd.setId(generatedUserId);

        User addedUser = userRepository.insert(userToAdd);

        log.debug("Returning new user {}", addedUser);
        return addedUser;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public UserListResponse getAllUsers(@RequestParam(defaultValue = "0", name = "pageNumber") int pageNumber,
                                        @RequestParam(defaultValue = "50", name = "pageSize") int pageSize) {
        log.debug("getAllUsers pageNumber={}, pageSize={}", pageNumber, pageSize);

        Pageable pageConstraints = PageRequest.of(pageNumber, pageSize);
        Page<User> usersPage = userRepository.findAll(pageConstraints);
        UserListResponse userListResponse = new UserListResponse(usersPage);

        log.debug("Returning user list={}", userListResponse);
        return userListResponse;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@PathVariable(name = "id") @NotBlank String userId) {
        log.debug("getUserById userId={}", userId);

        User userResponse = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User not found, ID=" + userId, userId));

        log.debug("Returning user={}", userResponse);
        return userResponse;
    }
}
