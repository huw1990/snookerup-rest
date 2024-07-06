package com.huwdunnit.snookeruprest.db;

import com.huwdunnit.snookeruprest.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data MongoDB repository for the User collection.
 *
 * @author Huwdunnit
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Find a user by their email address.
     * @param email
     * @return
     */
    Optional<User> findByEmail(String email);

}
