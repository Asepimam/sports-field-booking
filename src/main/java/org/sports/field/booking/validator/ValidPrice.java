package org.sports.field.booking.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PriceValidator.class)
@Documented
public @interface ValidPrice {

    String message() default "Invalid price format. Expected format: 10.000,00 or 10000.00";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // Optional: minimum value
    long min() default 0;

    // Optional: maximum value
    long max() default Long.MAX_VALUE;

    // Allow decimal places
    boolean allowDecimal() default true;
}