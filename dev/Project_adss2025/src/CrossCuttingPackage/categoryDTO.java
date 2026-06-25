package CrossCuttingPackage;

import java.util.List;

public class categoryDTO {
    private final String name;
    private final String categoryId;
    private final String type;

    private final List<categoryDTO> subCategoryIds;

    private List<promotionDTO> discountIds;

    public categoryDTO(String name, String categoryId, String type,
                       List<categoryDTO> subCategoryIds, List<promotionDTO> discountIds) {
        this.name = name;
        this.categoryId = categoryId;
        this.type = type;
        this.subCategoryIds=subCategoryIds;
        this.discountIds = discountIds;
    }

    public String getName() { return name; }
    public String getCategoryId() { return categoryId; }
    public String getType() { return type; }
    public List<categoryDTO> getSubCategoryIds() { return subCategoryIds; }
    public List<promotionDTO> getDiscountIds() { return discountIds; }

    public void setsub(List<promotionDTO> promotions) {
        discountIds=promotions;
    }
}
