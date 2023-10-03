package com.elearning.annotation.validator;

import com.elearning.annotation.validator.impls.ValuesAllowedValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ValuesAllowedValidator.class})
public @interface ValuesAllowed {

    String message() default "Tham số không phù hợp";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] values();

}
