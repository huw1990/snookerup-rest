package com.huwdunnit.snookeruprest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Routine {

    @Id
    private String id;

    private String title;

    private List<String> description;

    private List<String> tags;

    private List<Integer> cushionLimits;

    private List<String> colours;

    private Balls balls;

    private List<String> images;

    private boolean canLoop;
}
