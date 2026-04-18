package DomainLayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

public class CategoryFacade {

    private static HashMap<String,CategoryDL> categories;
    private List<CategoryDL> mainCategories;

    /*
    Id method for categories
    NAME-NAME SUB- NAME SUBSUB
     */


    public CategoryFacade()
    {
        this.mainCategories = new ArrayList<>();
        this.categories = new HashMap<>();
    }

    /**
     * Method that searches a category object by its id.
     * throws if wasnt found.
     * @param category_id
     * @return Corresponding category object.
     */
    public  CategoryDL FindCategoryById(String category_id)
    {
        CategoryDL res = categories.get(category_id);
        if (res == null)
            throw new NoSuchElementException("CategoryFacade - FindCategoryById:ID " + category_id +" could not be located in facade.");

        return res;
    }

    /**
     *
     * @param name category name, cannot pe empty.
     * @param discountPre = a double between 0 to 1.
     * @return new categroy ID
     */
    public String AddCategory(String name,double discountPre)
    {
        if(name == null || name.isEmpty())
            throw new IllegalArgumentException("CategoryFacade - Create Category:Name is invalid.");
        if(categories.get(name) != null)
            throw new IllegalArgumentException ("CategoryFacade - Create Category:Name " + name +" already taken.");
        if(discountPre <0 || discountPre>1)
            throw new IllegalArgumentException ("CategoryFacade - Create Category:Discount is invalid.");

        CategoryDL toAdd = new CategoryDL(name,name,new ArrayList<>(),discountPre, CategoryDL.CategoryType.Main); // send as null as we create a main category, also an empty list to start.
        categories.put(name,toAdd);
        this.mainCategories.add(toAdd); // add to main categories list.
        return name;
    }

    /**
     * Method that adds a sub category to and existing one.
     * Locates the roots, create a new sub category and adds its to the root category,and to the map as well.
     * ensures the only types that are created are sub and subsub.
     * @param name
     * @param discountPre
     * @param rootId
     * @return
     */
    public String AddSubcategory(String name,double discountPre,String rootId)
    {
        if(name == null || name.isEmpty())
            throw new IllegalArgumentException("CategoryFacade - Create Sub-Category:Name is invalid.");
        if(discountPre <0 || discountPre>1)
            throw new IllegalArgumentException ("CategoryFacade - Create Sub-Category:Discount is invalid.");

        CategoryDL root = FindCategoryById(rootId);
        String newId = rootId +"-" +name;
        if(categories.get(newId)!= null)
            throw new IllegalArgumentException ("CategoryFacade - Create Sub-Category:Name " + name +" already taken.");

        CategoryDL.CategoryType newType = CategoryDL.CategoryType.Sub; // finds out the new subcategory type.
        switch (root.getType())
        {
            case Sub -> newType = CategoryDL.CategoryType.Subsub;
            case Subsub -> throw new IllegalArgumentException("CategoryFacade - Create Sub-Category:Cannot create a sub category to a subsub category.");
        }

        CategoryDL toAdd = new CategoryDL(name,newId,new ArrayList<>(),discountPre,newType);

        categories.put(toAdd.getCategory_id(),toAdd);
        root.AddSubcategory(toAdd);

        return toAdd.getCategory_id();
    }

    /*
    Returns the list of main categories.
     */
    public List<CategoryDL> GetMainCategories()
    {
        return this.mainCategories;
    }

    /*
    Method that finds a category and returns its sub categories.
     */
    public List<CategoryDL> GetSubcategories(String category_id)
    {
        CategoryDL cat = FindCategoryById(category_id);
        return cat.getSubCategories();
    }

    /**
     Method that allows setting category discount, Looks for the category in the facade and updates its discount modifier.
     **/
    public void SetCatDiscount(String cat_id,double discount)
    {
        if(discount < 0 || discount > 1)
            throw new RuntimeException("ProductFacade - SetCatDiscounts: Invalid discount was sent:"+discount);

        CategoryDL cat = FindCategoryById(cat_id);
        cat.setDiscount_pre(discount);
    }

    /**
    STATIC METHOD: given a string, returns a category discount modifier, if category is found is facade.
    else: return null.
     **/
    public static Double GetCategoryDiscount(String id)
    {
        CategoryDL cat = categories.get(id);
        if(cat!=null)
            return cat.getDiscount_pre();

        return null;
    }
}
