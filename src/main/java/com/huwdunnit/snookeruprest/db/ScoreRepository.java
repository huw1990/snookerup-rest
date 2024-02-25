package com.huwdunnit.snookeruprest.db;

import com.huwdunnit.snookeruprest.model.Score;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Score collection.
 *
 * @author Huwdunnit
 */
@Repository
public interface ScoreRepository extends MongoRepository<Score, String> {

}
