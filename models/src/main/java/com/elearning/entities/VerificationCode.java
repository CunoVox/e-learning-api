package com.elearning.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class VerificationCode extends IBaseEntity{

    private String code;

    private String type;

    private String parentId;

    private Date expiredAt;
}
