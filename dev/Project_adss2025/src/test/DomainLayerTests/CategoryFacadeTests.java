package DomainLayerTests;
import DomainLayer.CategoryDL;
import DomainLayer.CategoryFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryFacadeTests
{
    /*
    Test class for category facade. Assumes FindCategoryById works, to test other methods.
     */
    private CategoryFacade c;
    @BeforeEach
    public void SetUp()
    {
        c = new CategoryFacade();
    }
    @Test
    public void AddCategoryTest()
    {
        String testId = c.AddCategory("TestCat",0);
        String testId2 = c.AddCategory("Test2",0.5);
        CategoryDL testCat = c.FindCategoryById("TestCat");
        CategoryDL testCat2 = c.FindCategoryById("Test2");

        assertEquals(testCat.getCategory_id(),testId);
        assertEquals(testCat2.getCategory_id(),testId2);

        try
        {
            c.AddCategory(null,0.5);
            fail();

        } catch (Exception e) {
        }
        try
        {
            c.AddCategory("Test 3",15);
            fail();
        } catch (Exception e) {
        }
        try
        {
            c.AddCategory("TestCat",0.1);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void AddSubCategoryTest()
    {
        String testId = c.AddCategory("TestCat",0); //assumes correctness

        String subTestId = c.AddSubcategory("TestCatSub",0,testId);
        CategoryDL sub = c.FindCategoryById("TestCat-TestCatSub");
        assertEquals(subTestId,sub.getCategory_id());

        String subsubTestId = c.AddSubcategory("TestCatSubsub",0,subTestId);
        CategoryDL subsub = c.FindCategoryById("TestCat-TestCatSub-TestCatSubsub");
        assertEquals(subsubTestId,subsub.getCategory_id());

        try
        {
            c.AddSubcategory(null,0.5,testId);
            fail();

        } catch (Exception e) {
        }
        try
        {
            c.AddSubcategory("FailTest",999,testId);
            fail();

        } catch (Exception e) {
        }
        try
        {
            c.AddSubcategory("FailTest",999,"None");
            fail();

        } catch (Exception e) {
        }
        try
        {
            c.AddSubcategory("FailTest",0.5,subsubTestId);
            fail();

        } catch (Exception e) {
        }
    }

    @Test
    public void GetSubCategoriesTest()
    {
        String main = c.AddCategory("Main",0.5);
        String sub1 = c.AddSubcategory("Sub1",0,main);
        String sub2 = c.AddSubcategory("Sub2",0,main);
        String subsub1 = c.AddSubcategory("Subsub1",0,sub1);
        String subsub2 = c.AddSubcategory("Subsub2",0,sub2);

        List<CategoryDL> mList = c.GetSubcategories(main);
        if(mList.size()!=2)
            fail();
        for(CategoryDL cat:mList)
            if(!cat.getCategory_id().equals(sub1)&&!cat.getCategory_id().equals(sub2))
                fail();

        mList = c.GetSubcategories(sub1);
        if(mList.size()!=1)
            fail();
        if(!mList.get(0).getCategory_id().equals(subsub1))
            fail();
        mList = c.GetSubcategories(sub2);
        if(mList.size()!=1)
            fail();
        if(!mList.get(0).getCategory_id().equals(subsub2))
            fail();
    }

    @Test
    public void SetCatDiscountTest()
    {
        String main = c.AddCategory("Main",0.5);

        c.SetCatDiscount(main,0.6);
        assertEquals(0.6,c.FindCategoryById(main).getDiscount_pre());

        try {
            c.SetCatDiscount(main,1111);
            fail();
        } catch (Exception e) {
        }
        try {
            c.SetCatDiscount(main,-1);
            fail();
        } catch (Exception e) {
        }
        try {
            c.SetCatDiscount("None",0.5);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void GetCatDiscountTest()
    {
        String main = c.AddCategory("Main",0.5);
        assertEquals(0.5,CategoryFacade.GetCategoryDiscount(main));
        assertEquals(null,CategoryFacade.GetCategoryDiscount("None"));
    }
}