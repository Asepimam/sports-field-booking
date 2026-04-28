package org.sports.field.booking.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class BigDecimalPriceValidator implements ConstraintValidator<ValidBigDecimalPrice, BigDecimal> {

    private BigDecimal min;
    private BigDecimal max;
    private int maxDecimalPlaces;

    @Override
    public void initialize(ValidBigDecimalPrice constraintAnnotation) {
        this.min = BigDecimal.valueOf(constraintAnnotation.min());
        this.max = BigDecimal.valueOf(constraintAnnotation.max());
        this.maxDecimalPlaces = constraintAnnotation.maxDecimalPlaces();
    }

    @Override
    public boolean isValid(BigDecimal price, ConstraintValidatorContext context) {
        if (price == null) {
            return true;
        }

        // Check min
        if (price.compareTo(min) < 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Price must be at least " + min)
                    .addConstraintViolation();
            return false;
        }

        // Check max
        if (price.compareTo(max) > 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Price must not exceed " + max)
                    .addConstraintViolation();
            return false;
        }

        // Check decimal places
        if (price.scale() > maxDecimalPlaces) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Price cannot have more than " + maxDecimalPlaces + " decimal places")
                    .addConstraintViolation();
            return false;
        }

        // Check if negative
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Price cannot be negative")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}