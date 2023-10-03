package com.elearning.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEmailRequest {
    @NotNull(message = "Email chưa được nhập")
    @NotBlank(message = "Email chưa được nhập")
    private String email;
}
