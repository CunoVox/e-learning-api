package com.elearning.models.dtos;

import com.elearning.utils.enumAttribute.EnumVerificationCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationCodeDTO {

    private String id;

    @JsonIgnore
    private String code;

    private EnumVerificationCode type;

    @JsonProperty(value = "parent_id")
    private String parentId;

    @JsonProperty(value = "expired_at")
    private Date expiredAt;
    @JsonProperty(value = "confirmed_at")
    private Date confirmedAt;
}