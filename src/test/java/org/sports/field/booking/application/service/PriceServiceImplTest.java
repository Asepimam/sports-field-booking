package org.sports.field.booking.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class PriceServiceImplTest {

    private final PriceService priceService = new PriceServiceImpl();

    @Test
    void formatPriceUsesIndonesianNumberFormat() {
        assertEquals("Rp 1.500,00", priceService.formatPriceWithCurrency(BigDecimal.valueOf(1500)));
    }

    @Test
    void calculateTotalReturnsZeroForInvalidQuantity() {
        assertEquals(BigDecimal.ZERO, priceService.calculateTotal(BigDecimal.valueOf(1000), 0));
    }

    @Test
    void validatesParsablePrice() {
        assertTrue(priceService.isValidPrice("Rp 10.000,00"));
        assertFalse(priceService.isValidPrice("abc"));
    }
}
