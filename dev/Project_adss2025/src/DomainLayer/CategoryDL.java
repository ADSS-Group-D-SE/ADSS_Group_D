package DomainLayer;

import java.util.List;


public class CategoryDL {

    public CategoryType getType() {
        return type;
    }

    public enum CategoryType{
        Main,Sub,Subsub
    }
    private String name;
    private String category_id;
    private List<CategoryDL> subCategory;
    private double discount_pre;
    private CategoryType type;

    /**
     *
     * @param name
     * @param category_id
     * @param subCategory - a list of its subcategories, can be empty.
     * @param discount_pre - a double between 0 to 1.
     *
     */
    public CategoryDL(String name, String category_id, List<CategoryDL> subCategory, double discount_pre,CategoryType t) {

        for(CategoryDL c:subCategory) // checks for a null subcategory
            if(c == null)
                throw new IllegalArgumentException("A bad subcategory was sent.");

        if(discount_pre < 0 || discount_pre > 1)
            throw new IllegalArgumentException("Bad discount modifier was sent.");

        this.name = name;
        this.category_id = category_id;
        this.subCategory = subCategory;
        this.discount_pre = discount_pre;
        this.type = t;
    }

    /*
    Method that receives a new subcategory to add to the category.
     */
    public void AddSubcategory(CategoryDL toAdd)
    {
        if(toAdd == null)
            throw new IllegalArgumentException("CategoryDL - Add Category:null category was sent.");
        this.subCategory.add(toAdd);
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

    public double getDiscount_pre() {
        return discount_pre;
    }

    public List<CategoryDL> getSubCategories(){
        return this.subCategory;
    }

    public void setDiscount_pre(double discount_pre) {
        this.discount_pre = discount_pre;
    }
}