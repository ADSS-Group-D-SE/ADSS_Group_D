package InventoryModule.ServiceLayer;

import CrossCuttingPackage.Response;
import InventoryModule.DomainLayer.CategoryFacade;

import java.util.List;

public class CategoryServices {
    private static CategoryServices INSTANCE;
    private final CategoryFacade cFacade;

    /*
    Private constructor, to imp the singleton pattern
     */
    private CategoryServices()
    {
        this.cFacade= new CategoryFacade();
    }

    /*
    Static instance method, return the instance, or creates it in non exist.
    Ensures only one is created.
     */
    public static CategoryServices GetInstance()
    {
        if(INSTANCE==null){
            INSTANCE= new CategoryServices();
        }
        return INSTANCE;
    }

    public Response<String> Clean()
    {
        Response<String> res;
        try {
            cFacade.CleanData();
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    /*
    The service for category creation, returns: CrossCuttingPackage.Response with new category Id if created successfully.
    Else: return a response with Error string.
     */
    public Response<String> CreateCategory(String name, double discountPre, String date)
    {
        Response<String> res;
        try {
            res = new Response<>(null,this.cFacade.AddCategory(name, discountPre,date));
        } catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    /*
   The service for Sub-category creation, returns: CrossCuttingPackage.Response with new category Id if created successfully.
   Else: return a response with Error string.
    */
    public Response<String> CreateSubCategory(String name, double discountPre, String date, String rootId)
    {
        Response<String> res;
        try {
            res = new Response<>(null,this.cFacade.AddSubcategory(name, discountPre,date, rootId));
        } catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    /*
    The service to get the list of currently existing main categories.
    Returns:CrossCuttingPackage.Response with list of categoriesSL if op was a success.
    Else:CrossCuttingPackage.Response with an error.
     */
    public Response<List<CategorySL>> GetMainCategories()
    {
        Response<List<CategorySL>> res;
        try {
            res = new Response<>(null,CategorySL.convert(this.cFacade.GetMainCategories()));
        } catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }
    /*
    The service to get the list of subcategories of the input category ID.
    Returns:CrossCuttingPackage.Response with Subcategory list of categoriesSL if op was a success.
    Else:CrossCuttingPackage.Response with an error.
    */
    public Response<List<CategorySL>> GetSubCategories(String category_id)
    {
        Response<List<CategorySL>> res;
        try {
            res = new Response<>(null,CategorySL.convert(this.cFacade.GetSubcategories(category_id)));
        } catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }
    /**
     The category discount setup service.
     Returns response: with null value when op was a success.
     else:CrossCuttingPackage.Response with an error msg.
     **/
    public Response<String> SetCategoryDiscount(String category_id, double discount, String date)
    {
        Response<String> res = null;
        try
        {
            this.cFacade.addCatDiscount(category_id,discount,date);
            res = new Response<>(null,null);
        }
        catch (Exception e)
        {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    /**
     * Service that handles the get all categories request.
     * Returns: a list of categorySL objects if op was a success, else
     * A CrossCuttingPackage.Response with error msg.
     * @return
     */
    public Response<List<CategorySL>> GetAllCategories()
    {
        Response<List<CategorySL>> res;
        try {
            res = new Response<>(null,CategorySL.convert(this.cFacade.GetAllCategories()));
        } catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    public Response<String> Load() {
        Response<String> res;
        try {
            cFacade.LoadData();
            res = new Response<>(null,null);
        }
        catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }
}
