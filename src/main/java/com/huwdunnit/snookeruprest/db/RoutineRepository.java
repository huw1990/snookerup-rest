package com.huwdunnit.snookeruprest.db;

import com.huwdunnit.snookeruprest.model.Routine;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Routine collection.
 *
 * @author Huwdunnit
 */
@Repository
public interface RoutineRepository extends MongoRepository<Routine, String> {

}
