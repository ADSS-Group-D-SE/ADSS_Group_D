package DomainLayerTests;
import InventoryModule.DomainLayer.CategoryDL;
import InventoryModule.DomainLayer.CategoryFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryFacadeTests
{
    private CategoryFacade c;
    private final String defaultDate = "31/12/2030";

    @BeforeEach
    public void SetUp()
    {
        c = new CategoryFacade();
        c.CleanData();
    }

    @Test
    public void AddCategoryTest()
    {
        String testId = c.AddCategory("TestCat", 0, defaultDate);
        String testId2 = c.AddCategory("Test2", 0.5, defaultDate);
        CategoryDL testCat = c.FindCategoryById("TestCat");
        CategoryDL testCat2 = c.FindCategoryById("Test2");

        assertEquals(testCat.getCategory_id(), testId);
        assertEquals(testCat2.getCategory_id(), testId2);

        try
        {
            c.AddCategory(null, 0.5, defaultDate);
            fail();
        } catch (Exception e) {
        }
        try
        {
            c.AddCategory("Test 3", 15, defaultDate);
            fail();
        } catch (Exception e) {
        }
        try
        {
            c.AddCategory("TestCat", 0.1, defaultDate);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void AddSubCategoryTest()
    {
        String testId = c.AddCategory("TestCat", 0, defaultDate);

        String subTestId = c.AddSubcategory("TestCatSub", 0, defaultDate, testId);
        CategoryDL sub = c.FindCategoryById("TestCat-TestCatSub");
        assertEquals(subTestId, sub.getCategory_id());

        String subsubTestId = c.AddSubcategory("TestCatSubsub", 0, defaultDate, subTestId);
        CategoryDL subsub = c.FindCategoryById("TestCat-TestCatSub-TestCatSubsub");
        assertEquals(subsubTestId, subsub.getCategory_id());

        try
        {
            c.AddSubcategory(null, 0.5, defaultDate, testId);
            fail();
        } catch (Exception e) {
        }
        try
        {
            c.AddSubcategory("FailTest", 999, defaultDate, testId);
            fail();
        } catch (Exception e) {
        }
        try
        {
            c.AddSubcategory("FailTest", 999, defaultDate, "None");
            fail();
        } catch (Exception e) {
        }
        try
        {
            c.AddSubcategory("FailTest", 0.5, defaultDate, subsubTestId);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void GetSubCategoriesTest()
    {
        String main = c.AddCategory("Main", 0.5, defaultDate);
        String sub1 = c.AddSubcategory("Sub1", 0, defaultDate, main);
        String sub2 = c.AddSubcategory("Sub2", 0, defaultDate, main);
        String subsub1 = c.AddSubcategory("Subsub1", 0, defaultDate, sub1);
        String subsub2 = c.AddSubcategory("Subsub2", 0, defaultDate, sub2);

        List<CategoryDL> mList = c.GetSubcategories(main);
        if(mList.size() != 2)
            fail();
        for(CategoryDL cat : mList)
            if(!cat.getCategory_id().equals(sub1) && !cat.getCategory_id().equals(sub2))
                fail();

        mList = c.GetSubcategories(sub1);
        if(mList.size() != 1)
            fail();
        if(!mList.get(0).getCategory_id().equals(subsub1))
            fail();

        mList = c.GetSubcategories(sub2);
        if(mList.size() != 1)
            fail();
        if(!mList.get(0).getCategory_id().equals(subsub2))
            fail();
    }

    @Test
    public void SetCatDiscountTest()
    {
        String main = c.AddCategory("Main", 0.5, defaultDate);

        c.addCatDiscount(main, 0.6, defaultDate);

        assertEquals(0.8, c.GetCategoryDiscount(main));

        try {
            c.addCatDiscount(main, 1111, defaultDate);
            fail();
        } catch (Exception e) {
        }
        try {
            c.addCatDiscount(main, -1, defaultDate);
            fail();
        } catch (Exception e) {
        }
        try {
            c.addCatDiscount("None", 0.5, defaultDate);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void GetCatDiscountTest()
    {
        String main = c.AddCategory("Main", 0.5, defaultDate);

        assertEquals(0.5, c.GetCategoryDiscount(main));
        assertNull( c.GetCategoryDiscount("None"));
    }
}