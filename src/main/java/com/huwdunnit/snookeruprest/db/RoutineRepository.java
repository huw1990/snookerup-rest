package com.huwdunnit.snookeruprest.db;

import com.huwdunnit.snookeruprest.model.Routine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data MongoDB repository for the Routine collection.
 *
 * @author Huwdunnit
 */
@Repository
public interface RoutineRepository extends MongoRepository<Routine, String> {

    Page<Routine> findByTagsIn(Pageable pageConstraints, List<String> tags);
}
