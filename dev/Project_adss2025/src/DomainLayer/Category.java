package DomainLayer;

/**
 *
 */
public class Category {

    private String name;
    private String category_id;
    private Category[] subCategory;
    private int discount_pre;


    /**
     *
     * @param name
     * @param category_id
     * @param subCategory
     * @param discount_pre
     */
    public Category(String name, String category_id, Category[] subCategory, int discount_pre) {
        this.name = name;
        this.category_id = category_id;
        this.subCategory = subCategory;
        this.discount_pre = discount_pre;
    }
}
