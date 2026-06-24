package InventoryModule.DomainLayer;

import CrossCuttingPackage.categoryDTO;
import CrossCuttingPackage.productDTO;
import InventoryModule.DataLayer.CategoryDAO;
import InventoryModule.DataLayer.CategoryHierarchyDAO;
import InventoryModule.DataLayer.ProductDAO;

import java.util.*;

public class CategoryFacade {

    private static HashMap<String,CategoryDL> categories;
    private final List<CategoryDL> mainCategories;


    private static final CategoryDAO categoryDAO = new CategoryDAO();
    private static final CategoryHierarchyDAO categoryHierarchyDAO = new CategoryHierarchyDAO();




    public CategoryFacade()
    {
        this.mainCategories = new ArrayList<>();
        categories = new HashMap<>();
    }

    /**
     * Method that searches a category object by its id.
     * throws if wasnt found.
     * @param category_id
     * @return Corresponding category object.
     */
    public CategoryDL FindCategoryById(String category_id)
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


    public String AddCategory(String name, double discountPre,String date) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("CategoryFacade - Create Category:Name is invalid.");
        if (categories.get(name) != null)
            throw new IllegalArgumentException("CategoryFacade - Create Category:Name " + name + " already taken.");

        Promotion initialPromo = null;
        if (discountPre > 0) {
            if (discountPre > 1) throw new IllegalArgumentException("CategoryFacade - Create Category:Discount is invalid.");
            String promoId = String.valueOf(System.currentTimeMillis());
            initialPromo = new Promotion(promoId, discountPre, date, PromotionScope.CATEGORY);
        }

        CategoryDL toAdd = new CategoryDL(name, name, new ArrayList<>(), initialPromo, CategoryDL.CategoryType.Main);
        categoryDAO.Insert(toAdd.toDTO());
        categories.put(name, toAdd);
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
    public String AddSubcategory(String name,double discountPre,String date,String rootId)
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

        Promotion initialPromo = null;
        if (discountPre > 0) {
            if (discountPre > 1) throw new IllegalArgumentException("CategoryFacade - Create Sub-Category:Discount is invalid.");
            String promoId = String.valueOf(System.currentTimeMillis());
            initialPromo = new Promotion(promoId, discountPre, date, PromotionScope.CATEGORY);
        }

        CategoryDL toAdd = new CategoryDL(name,newId,new ArrayList<>(),initialPromo,newType);
        categoryDAO.Insert(toAdd.toDTO());
        categoryHierarchyDAO.InsertSingleLink(rootId, toAdd.getCategory_id());
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
    public void addCatDiscount(String cat_id, double discount, String date)
    {
        if(discount < 0 || discount > 1)
            throw new RuntimeException("ProductFacade - SetCatDiscounts: Invalid discount was sent:" + discount);

        CategoryDL originalCat = FindCategoryById(cat_id);

        CategoryDL tempCat = new CategoryDL(originalCat);

        Promotion initialPromo = null;
        if (discount > 0) {
            String promoId = String.valueOf((int) (System.currentTimeMillis() / 1000));
            initialPromo = new Promotion(promoId, discount, date, PromotionScope.CATEGORY);
        }

        tempCat.addPromotion(initialPromo);

        categoryDAO.UpdateCategory(tempCat.toDTO());

        originalCat.addPromotion(initialPromo);
    }

    /**
    STATIC METHOD: given a string, returns a category discount modifier, if category is found is facade.
    else: return null.
     **/
    public static Double GetCategoryDiscount(String id)
    {
        CategoryDL cat = categories.get(id);
        if(cat !=null) {
            cat.removeExpiredPromotions();
            return cat.getTotalCategoryDiscount();
        }
        return null;
    }

    /**
    Method that returns a list of all categoryDL in facade.
     CAN ADD A CONDITION to filter all subsub categories in the future
     **/
    public List<CategoryDL> GetAllCategories() {
        List<CategoryDL> cats = new ArrayList<>();
        for(Map.Entry<String,CategoryDL> en:categories.entrySet())
        {
            cats.add(en.getValue());
        }
        return cats;
    }

    public void CleanData()
    {
        categoryDAO.Clean();
    }

    public void LoadData() {
        List<categoryDTO> categoryDTOS = categoryDAO.SelectAll();
        if (this.mainCategories != null) {
            this.mainCategories.clear();
        }

        for (categoryDTO dto : categoryDTOS) {
            CategoryDL category = new CategoryDL(dto);

            this.categories.put(category.getCategory_id(), category);

            if (category.getType() == CategoryDL.CategoryType.Main) {
                this.mainCategories.add(category);
            }
        }

        System.out.println("[V] Database data loaded successfully. Main categories initialized!");
    }

}
