package InventoryModule.ServiceLayer;

import InventoryModule.DomainLayer.CategoryDL;

import java.util.ArrayList;
import java.util.List;

public class CategorySL {

    public String name;
    public String Id;

    public CategorySL(CategoryDL cat)
    {
        this.name = cat.getName();
        this.Id = cat.getCategory_id();
    }

    /*
    Public static method that takes a list of categoriesDL and converts them to a list of CategorySL.
     */
    public static List<CategorySL> convert (List<CategoryDL> toConvert)
    {
        List<CategorySL> res = new ArrayList<>();
        for(CategoryDL cat:toConvert)
        {
            res.add(new CategorySL(cat));
        }
        return res;
    }
}
