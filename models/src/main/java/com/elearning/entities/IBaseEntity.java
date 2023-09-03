package com.elearning.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class IBaseEntity {
    @Id
    private String id;
    private Date createdAt = new Date(new Date().getTime());
    private Date updatedAt = new Date(new Date().getTime());
    private Boolean isDeleted = false;
}
