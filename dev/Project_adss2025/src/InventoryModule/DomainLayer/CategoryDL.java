package InventoryModule.DomainLayer;

import CrossCuttingPackage.SupplierDTO;
import CrossCuttingPackage.categoryDTO;
import CrossCuttingPackage.promotionDTO;
import SupplierModule.DomainLayer.ContactInfo;
import SupplierModule.DomainLayer.DeliveryDaySchedule;
import SupplierModule.DomainLayer.SupplierAgreement;

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

    public CategoryDL(CategoryDL c) {
        if (c == null) {
            throw new IllegalArgumentException("Cannot copy a null CategoryDL object.");
        }

        this.name = c.name;
        this.category_id = c.category_id;
        this.type = c.type;

        this.subCategory = new ArrayList<>();
        if (c.subCategory != null) {
            for (CategoryDL sub : c.subCategory) {
                this.subCategory.add(new CategoryDL(sub));
            }
        }

        this.discount_pre = new ArrayList<>();
        if (c.discount_pre != null) {
            for (Promotion promo : c.discount_pre) {
                this.discount_pre.add(new Promotion(promo));
            }
        }
    }

    public CategoryDL(categoryDTO dto) {
        this.name = dto.getName();
        this.category_id = dto.getCategoryId();

        this.type = CategoryType.valueOf(dto.getType());

        this.subCategory = new ArrayList<>();
        if (dto.getSubCategoryIds() != null) {
            for (categoryDTO subDto : dto.getSubCategoryIds()) {
                this.subCategory.add(new CategoryDL(subDto));
            }
        }

        this.discount_pre = new ArrayList<>();
        if (dto.getDiscountIds() != null) {
            for (promotionDTO pDto : dto.getDiscountIds()) {
                this.discount_pre.add(new Promotion(pDto));
            }
        }
    }

    public categoryDTO toDTO() {
        return new categoryDTO(
                this.name,
                this.category_id,
                (this.type != null) ? this.type.name() : null,
                convertCategoryList(subCategory),
                convertPromotionsToDTO(discount_pre)
        );
    }

    private static List<promotionDTO> convertPromotionsToDTO(List<Promotion> promotions) {
        List<promotionDTO> dtos = new ArrayList<>();
        if (promotions != null) {
            for (Promotion p : promotions) {
                dtos.add(p.toDTO());
            }
        }
        return dtos;
    }

    public List<categoryDTO> convertCategoryList(List<CategoryDL> domainList) {
        List<categoryDTO> dtoList = new ArrayList<>();

        if (domainList != null) {
            for (CategoryDL category : domainList) {
                dtoList.add(category.toDTO());
            }
        }

        return dtoList;
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