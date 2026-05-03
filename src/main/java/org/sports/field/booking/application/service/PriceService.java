package org.sports.field.booking.application.service;

import java.math.BigDecimal;
import java.text.ParseException;

public interface PriceService {
    String formatPrice(BigDecimal price);

    String formatPriceWithCurrency(BigDecimal price);

    BigDecimal parsePrice(String priceStr) throws ParseException;

    boolean isValidPrice(String priceStr);

    BigDecimal calculateTotal(BigDecimal price, int quantity);
}
