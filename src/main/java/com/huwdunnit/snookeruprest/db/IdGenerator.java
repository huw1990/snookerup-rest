package com.huwdunnit.snookeruprest.db;

import java.util.UUID;

/**
 * Generates new object IDs for objects inserted into MongoDB.
 *
 * @author Huwdunnit
 */
public class IdGenerator {

    public static String createNewId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
