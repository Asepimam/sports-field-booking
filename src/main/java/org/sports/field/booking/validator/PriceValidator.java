package org.sports.field.booking.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class PriceValidator implements ConstraintValidator<ValidPrice, String> {

    private long min;
    private long max;
    private boolean allowDecimal;

    @Override
    public void initialize(ValidPrice constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.allowDecimal = constraintAnnotation.allowDecimal();
    }

    @Override
    public boolean isValid(String price, ConstraintValidatorContext context) {
        if (price == null || price.trim().isEmpty()) {
            return true; // Use @NotNull for required fields
        }

        try {
            // Try to parse Indonesian format (10.000,00)
            BigDecimal parsedPrice = parseIndonesianPrice(price);

            // Check if decimal is allowed
            if (!allowDecimal && parsedPrice.scale() > 0) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Decimal values are not allowed")
                        .addConstraintViolation();
                return false;
            }

            // Check min and max
            BigDecimal minValue = BigDecimal.valueOf(min);
            BigDecimal maxValue = BigDecimal.valueOf(max);

            if (parsedPrice.compareTo(minValue) < 0) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Price must be at least " + formatPrice(minValue))
                        .addConstraintViolation();
                return false;
            }

            if (parsedPrice.compareTo(maxValue) > 0) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Price must not exceed " + formatPrice(maxValue))
                        .addConstraintViolation();
                return false;
            }

            return true;

        } catch (ParseException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid price format. Use format: 10.000,00 or 10000.00")
                    .addConstraintViolation();
            return false;
        }
    }

    private BigDecimal parseIndonesianPrice(String priceStr) throws ParseException {
        // Remove all whitespace
        priceStr = priceStr.trim();

        // Try parsing as Indonesian format (using NumberFormat)
        try {
            NumberFormat indonesianFormat = NumberFormat.getNumberInstance(new Locale("id", "ID"));
            Number number = indonesianFormat.parse(priceStr);
            return BigDecimal.valueOf(number.doubleValue());
        } catch (ParseException e) {
            // Try parsing as standard decimal format
            try {
                NumberFormat standardFormat = NumberFormat.getNumberInstance(Locale.US);
                Number number = standardFormat.parse(priceStr);
                return BigDecimal.valueOf(number.doubleValue());
            } catch (ParseException ex) {
                throw new ParseException("Unable to parse price: " + priceStr, 0);
            }
        }
    }

    private String formatPrice(BigDecimal price) {
        NumberFormat indonesianFormat = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        return indonesianFormat.format(price);
    }
}