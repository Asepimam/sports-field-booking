package org.sports.field.booking.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BigDecimalPriceValidator.class)
@Documented
public @interface ValidBigDecimalPrice {

    String message() default "Invalid price";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    long min() default 0;

    long max() default Long.MAX_VALUE;

    int maxDecimalPlaces() default 2;
}