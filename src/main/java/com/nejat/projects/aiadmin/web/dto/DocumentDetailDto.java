package com.nejat.projects.aiadmin.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDetailDto {

    private UUID id;
    private String title;
    private String sender;
    private String amount;
    private String due;
    private String status;
    private String category;
    private List<String> tags;
    private String summary;
}
