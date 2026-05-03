package org.sports.field.booking.application.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PriceServiceImpl implements PriceService {

    private static final NumberFormat INDONESIAN_FORMAT = NumberFormat.getNumberInstance(new Locale("id", "ID"));

    static {
        INDONESIAN_FORMAT.setMinimumFractionDigits(2);
        INDONESIAN_FORMAT.setMaximumFractionDigits(2);
    }

    @Override
    public String formatPrice(BigDecimal price) {
        if (price == null) {
            return "0,00";
        }
        return INDONESIAN_FORMAT.format(price);
    }

    @Override
    public String formatPriceWithCurrency(BigDecimal price) {
        return "Rp " + formatPrice(price);
    }

    @Override
    public BigDecimal parsePrice(String priceStr) throws ParseException {
        if (priceStr == null || priceStr.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        priceStr = priceStr.replace("Rp", "").trim();

        try {
            Number number = INDONESIAN_FORMAT.parse(priceStr);
            return BigDecimal.valueOf(number.doubleValue());
        } catch (ParseException e) {
            try {
                NumberFormat standardFormat = NumberFormat.getNumberInstance(Locale.US);
                Number number = standardFormat.parse(priceStr);
                return BigDecimal.valueOf(number.doubleValue());
            } catch (ParseException ex) {
                throw new ParseException("Unable to parse price: " + priceStr, 0);
            }
        }
    }

    @Override
    public boolean isValidPrice(String priceStr) {
        try {
            parsePrice(priceStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    @Override
    public BigDecimal calculateTotal(BigDecimal price, int quantity) {
        if (price == null || quantity <= 0) {
            return BigDecimal.ZERO;
        }
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
