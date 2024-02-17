package com.huwdunnit.snookeruprest.db;

import com.huwdunnit.snookeruprest.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the User collection.
 *
 * @author Huwdunnit
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

}
