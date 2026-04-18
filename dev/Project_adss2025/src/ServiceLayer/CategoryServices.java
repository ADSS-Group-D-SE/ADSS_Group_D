package ServiceLayer;

import DomainLayer.CategoryFacade;
import DomainLayer.ProductFacade;

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

    /*
    The service for category creation, returns: Response with new category Id if created successfully.
    Else: return a response with Error string.
     */
    public Response<String> CreateCategory(String name,double discountPre)
    {
        Response<String> res;
        try {
            res = new Response<>(null,this.cFacade.AddCategory(name, discountPre));
        } catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    /*
   The service for Sub-category creation, returns: Response with new category Id if created successfully.
   Else: return a response with Error string.
    */
    public Response<String> CreateSubCategory(String name,double discountPre,String rootId)
    {
        Response<String> res;
        try {
            res = new Response<>(null,this.cFacade.AddSubcategory(name, discountPre,rootId));
        } catch (Exception e) {
            res = new Response<>(e.getMessage());
        }
        return res;
    }

    /*
    The service to get the list of currently existing main categories.
    Returns:Response with list of categoriesSL if op was a success.
    Else:Response with an error.
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
    Returns:Response with Subcategory list of categoriesSL if op was a success.
    Else:Response with an error.
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
     else:Response with an error msg.
     **/
    public Response<String> SetCategoryDiscount(String category_id,double discount)
    {
        Response<String> res = null;
        try
        {
            this.cFacade.SetCatDiscount(category_id,discount);
            res = new Response<>(null,null);
        }
        catch (Exception e)
        {
            res = new Response<>(e.getMessage());
        }
        return res;
    }
}
