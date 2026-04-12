package DomainLayer;

import java.util.List;

public class Category {

    private String name;
    private String category_id;
    private List<Category> subCategory;
    private double discount_pre;


    /**
     *
     * @param name
     * @param category_id
     * @param subCategory - a list of its subcategories, can be empty.
     * @param discount_pre - a double between 0 to 1.
     *
     */
    public Category(String name, String category_id, List<Category> subCategory, double discount_pre) {

        for(Category c:subCategory) // checks for a null subcategory
            if(c == null)
                throw new IllegalArgumentException("A bad subcategory was sent.");

        if(discount_pre < 0 || discount_pre > 1)
            throw new IllegalArgumentException("Bad discount modifier was sent.");

        this.name = name;
        this.category_id = category_id;
        this.subCategory = subCategory;
        this.discount_pre = discount_pre;
    }

    /*
    ==============================
    Getters and setters
    ==============================
     */
    public String getName() {
        return name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public List<Category> getSubCategory() {
        return subCategory;
    }

    public double getDiscount_pre() {
        return discount_pre;
    }

    public void setDiscount_pre(double discount_pre) {
        this.discount_pre = discount_pre;
    }
}