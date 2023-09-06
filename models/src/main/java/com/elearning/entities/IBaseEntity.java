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
public abstract class IBaseEntity {
    @Id
    private String id;
    private Date createdAt = new Date();
    private Date updatedAt = new Date();
    private Boolean isDeleted = false;
}
