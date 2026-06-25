package InventoryModule.DataAccessLayer;

import CrossCuttingPackage.categoryDTO;
import CrossCuttingPackage.promotionDTO;
import InventoryModule.DomainLayer.CategoryDL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    private static final String url = "jdbc:sqlite:database.db";
    private static final CategoryPromotionDAO promotionDAO = new CategoryPromotionDAO();
    private static final CategoryHierarchyDAO hierarchyDAO = new CategoryHierarchyDAO();

    public void Insert(categoryDTO category) {
        String sql = "INSERT INTO Categories(category_id, name, type) VALUES(?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement ps = conn.prepareStatement(sql)) {
            promotionDAO.insertPromotions(category.getCategoryId(), category.getDiscountIds());

            ps.setString(1, category.getCategoryId());
            ps.setString(2, category.getName());
            ps.setString(3, category.getType());

            ps.executeUpdate();

            hierarchyDAO.InsertLinks(category.getCategoryId(), category.getSubCategoryIds());

        } catch (SQLException e) {
            throw new RuntimeException("CategoryDAO:Insert - " + e.getMessage());
        }
    }


    public List<categoryDTO> SelectAll() {
        String q = "SELECT category_id, name, type FROM Categories";
        List<categoryDTO> res = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement qu = conn.prepareStatement(q);
             ResultSet rs = qu.executeQuery()) {

            while (rs.next()) {
                String categoryId = rs.getString("category_id");

                List<String> subCategoryIds = hierarchyDAO.SelectSubCategories(categoryId);

                List<categoryDTO> subCategories = new ArrayList<>();
                for (String subId : subCategoryIds) {
                    categoryDTO fullSub = this.Select(subId);
                    if (fullSub != null) {
                        subCategories.add(fullSub);
                    }
                }

                List<promotionDTO> promotions = promotionDAO.selectByCategory(categoryId);

                categoryDTO category = new categoryDTO(
                        rs.getString("name"),
                        categoryId,
                        CategoryDL.CategoryType.valueOf(rs.getString("type")).toString(),
                        subCategories,
                        promotions
                );

                res.add(category);
            }

        } catch (SQLException e) {
            throw new RuntimeException("CategoryDAO:SelectAll - " + e.getMessage(), e);
        }

        return res;
    }

    public categoryDTO Select(String categoryId) {
        String query = "SELECT category_id, name, type FROM Categories WHERE category_id = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    List<String> subIds = hierarchyDAO.SelectSubCategories(categoryId);
                    List<categoryDTO> subCategories = new ArrayList<>();
                    for (String subId : subIds) {
                        categoryDTO child = this.Select(subId);
                        if (child != null) subCategories.add(child);
                    }

                    List<promotionDTO> promotions = promotionDAO.selectByCategory(categoryId);

                    return new categoryDTO(
                            rs.getString("name"),
                            categoryId,
                            CategoryDL.CategoryType.valueOf(rs.getString("type")).toString(),
                            subCategories,
                            promotions
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("CategoryDAO:Select failed for ID: " + categoryId, e);
        }
        return null;
    }

    public void Clean() {
        String q = "DELETE FROM Categories";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {
            promotionDAO.Clean();
            hierarchyDAO.Clean();
            qu.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("CategoryDAO:Clean - " + e.getMessage());
        }
    }

    public void Delete(String category_id) {
        String q = "DELETE FROM Categories WHERE category_id = ?";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {
            qu.setString(1, category_id);
            promotionDAO.DeletePromotionsByCategory(category_id);

            int rowsDeleted = qu.executeUpdate();
            if (rowsDeleted <= 0) {
                throw new RuntimeException("CategoryDAO:Delete - no such category was found in db: " + category_id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("CategoryDAO:Delete - " + e.getMessage());
        }
    }
}