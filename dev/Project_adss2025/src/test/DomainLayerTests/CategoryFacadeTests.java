package DomainLayerTests;
import DomainLayer.CategoryDL;
import DomainLayer.CategoryFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CategoryFacadeTests
{
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
        CategoryDL testCat = c.FindCategoryById("TestCat");

        assertEquals(testCat.getCategory_id(),testId);
    }


}