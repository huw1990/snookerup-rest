package com.huwdunnit.snookeruprest.security;

import com.huwdunnit.snookeruprest.db.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * An implementation of a Spring Security UserDetailsService, where users are stored in the backing MongoDB database
 * used by this application.
 *
 * @author Huwdunnit
 */
@Service
@RequiredArgsConstructor
public class MongoUserDetailsService implements UserDetailsService {

    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        Optional<com.huwdunnit.snookeruprest.model.User> userLookup = userRepository.findByEmail(userName);

        if (userLookup.isPresent()) {
            return userLookup.get();
        }
        throw new UsernameNotFoundException("Username " + userName + " not found");

    }

}
