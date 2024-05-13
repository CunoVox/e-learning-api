package com.elearning.models.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDTO {
    private String id;
    private String name;
    private String mimeType;
    private String webViewLink;
    private String webContentLink;
    private String videoMediaMetadata;
    private String size;
}
