package InventoryModule.DomainLayer;

import CrossCuttingPackage.promotionDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;


public class Promotion {
    private String id;
    private double discountPercentage;
    private LocalDate endDate;
    private PromotionScope scope;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Promotion(String baseId,double discountPercentage, String endDateStr, PromotionScope scope) {
        if (discountPercentage < 0 || discountPercentage > 1) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 1");
        }
        this.id = generateOrderId(baseId);
        this.discountPercentage = discountPercentage;
        this.scope = scope;

        try {
            this.endDate = LocalDate.parse(endDateStr, FORMATTER);

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Please use DD/MM/YYYY");
        }
    }
    private static String generateOrderId(String s) {
        return "PROMO-"+ s+"-"+ UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16)
                .toUpperCase();
    }

    public Promotion(Promotion other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot copy a null Promotion object.");
        }
        this.id = other.id;
        this.discountPercentage = other.discountPercentage;
        this.endDate = other.endDate;
        this.scope = other.scope;
    }

    public Promotion(promotionDTO p) {
        this.id = p.getId();
        this.discountPercentage = p.getDiscountPercentage();

        try {
            this.endDate = LocalDate.parse(p.getEndDate(), FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format in DTO. Use DD/MM/YYYY");
        }

        try {
            this.scope = PromotionScope.valueOf(p.getScope());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid scope value: " + p.getScope());
        }

        if (discountPercentage < 0 || discountPercentage > 1) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 1");
        }
    }

    public promotionDTO toDTO() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = (this.endDate != null) ? this.endDate.format(formatter) : null;
        String scopeStr = (this.scope != null) ? this.scope.name() : null;

        return new promotionDTO(
                this.id,
                this.discountPercentage,
                formattedDate,
                scopeStr
        );
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

    public String Summary()
    {
        return "Promotion:" + this.id+"\nDiscount%:" + this.discountPercentage*100 +"%\nEnd date:" + this.endDate.toString();
    }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setScope(PromotionScope scope) { this.scope = scope; }
}