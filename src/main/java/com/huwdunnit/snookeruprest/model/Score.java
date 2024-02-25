package com.huwdunnit.snookeruprest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
public class Score {

    @Id
    private String id;

    private int value;

    private String routineId;

    private String userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/M/yyyy HH:mm")
    private LocalDateTime dateAndTime;
}
