package com.huwdunnit.snookeruprest.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
public class Routine {

    @Id
    private String id;

    private String title;

    private String description;

    private List<String> tags;
}
