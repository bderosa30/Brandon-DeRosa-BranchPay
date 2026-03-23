package org.example.model.api;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class Repo {
    private String name;
    private String url;
}
