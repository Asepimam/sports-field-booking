package org.sports.field.booking.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BigDecimalPriceValidator implements ConstraintValidator<ValidBigDecimalPrice, Long> {

    private long min;
    private long max;
    private int maxDecimalPlaces;

    @Override
    public void initialize(ValidBigDecimalPrice constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.maxDecimalPlaces = constraintAnnotation.maxDecimalPlaces();
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        // Null check - use @NotNull for required validation
        if (value == null) {
            return true;
        }

        // Check min/max range
        if (value < min || value > max) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Price must be between " + min + " and " + max).addConstraintViolation();
            return false;
        }

        // For Long/decimal validation: Since Long doesn't have decimal places,
        // we need to interpret what maxDecimalPlaces means for your business logic
        // Example: if maxDecimalPlaces = 2, maybe price is in cents?
        // Or you can ignore decimal places check for Long

        return true;
    }
}