package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.db.IdGenerator;
import com.huwdunnit.snookeruprest.db.UserRepository;
import com.huwdunnit.snookeruprest.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for User endpoints.
 *
 * @author Huwdunnit
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

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
}
