package com.huwdunnit.snookeruprest.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class User {

    private String id;

    private String firstName;

    private String lastName;

    private String email;
}
