package InventoryModule.DomainLayer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Promotion {
    private String id;
    private double discountPercentage;
    private LocalDate endDate;
    private PromotionScope scope;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Promotion(String id, double discountPercentage, String endDateStr, PromotionScope scope) {
        if (discountPercentage < 0 || discountPercentage > 1) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 1");
        }
        this.id = id;
        this.discountPercentage = discountPercentage;
        this.scope = scope;

        try {
            this.endDate = LocalDate.parse(endDateStr, FORMATTER);

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Please use DD/MM/YYYY");
        }
    }

    public boolean isActiveNow() {
        return !LocalDate.now().isAfter(endDate);
    }

    public String getId() { return id; }
    public double getDiscountPercentage() { return discountPercentage; }
    public LocalDate getEndDate() { return endDate; }
    public PromotionScope getScope() { return scope; }

    public String getEndDateFormatted() {
        return endDate.format(FORMATTER);
    }

    public void setDiscountPercentage(double discountPercentage) {
        if (discountPercentage < 0 || discountPercentage > 1) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 1");
        }
        this.discountPercentage = discountPercentage;
    }

    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setScope(PromotionScope scope) { this.scope = scope; }
}