package InventoryModule.DomainLayer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;



public class CategoryDL {

    public enum CategoryType {
        Main, Sub, Subsub
    }

    private String name;
    private String category_id;
    private List<CategoryDL> subCategory;

    private List<Promotion> discount_pre;
    private CategoryType type;


    public CategoryDL(String name, String category_id, List<CategoryDL> subCategory, Promotion initialPromotion, CategoryType t) {

        for (CategoryDL c : subCategory) { // checks for a null subcategory
            if (c == null)
                throw new IllegalArgumentException("A bad subcategory was sent.");
        }

        this.name = name;
        this.category_id = category_id;
        this.subCategory = subCategory;
        this.type = t;

        this.discount_pre = new ArrayList<>();

        if (initialPromotion != null) {
            this.discount_pre.add(initialPromotion);
        }
    }

    /*
    Method that receives a new subcategory to add to the category.
     */
    public void AddSubcategory(CategoryDL toAdd) {
        if (toAdd == null)
            throw new IllegalArgumentException("CategoryDL - Add Category:null category was sent.");
        this.subCategory.add(toAdd);
    }


    public void addPromotion(Promotion promotion) {
        if (promotion == null) {
            throw new IllegalArgumentException("Promotion cannot be null");
        }
        this.discount_pre.add(promotion);
    }

    public double getTotalCategoryDiscount() {
        double priceMultiplier = 1.0;

        for (Promotion p : discount_pre) {
            if (p.isActiveNow()) {
                priceMultiplier *= (1 - p.getDiscountPercentage());
            }
        }

        return 1 - priceMultiplier;
    }
    private static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            .appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            .toFormatter();    public void removeExpiredPromotions() {
        if (this.discount_pre == null || this.discount_pre.isEmpty()) {
            return;
        }

        LocalDate today = LocalDate.now();

        this.discount_pre.removeIf(promo -> {
            String endDateStr = String.valueOf(promo.getEndDate());

            if (endDateStr == null || endDateStr.isEmpty()) {
                return false;
            }
            LocalDate endDate = LocalDate.parse(endDateStr, DATE_FORMATTER);
            return endDate.isBefore(today);
        });
    }

    /*
    ==============================
    Getters and setters
    ==============================
     */
    public CategoryType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getCategory_id() {
        return category_id;
    }


    public List<Promotion> getDiscount_pre() {
        return discount_pre;
    }

    public List<CategoryDL> getSubCategories() {
        return this.subCategory;
    }


    public void setDiscount_pre(List<Promotion> discount_pre) {
        if (discount_pre == null) {
            throw new IllegalArgumentException("Promotions list cannot be null");
        }
        this.discount_pre = discount_pre;
    }
}