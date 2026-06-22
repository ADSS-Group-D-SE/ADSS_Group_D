package InventoryModule.DataLayer;

import CrossCuttingPackage.categoryDTO;
import InventoryModule.DomainLayer.CategoryDL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryHierarchyDAO {

    private static final String url = "jdbc:sqlite:database.db";

    public void InsertLinks(String parentId, List<categoryDTO> subCategories) {
        if (subCategories == null || subCategories.isEmpty()) return;

        String query = "INSERT INTO CategoryHierarchy(parent_id, sub_category_id) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(query)) {

            for (categoryDTO sub : subCategories) {
                ps.setString(1, parentId);
                ps.setString(2, sub.getCategoryId());
                ps.addBatch();
            }
            ps.executeBatch();

        } catch (SQLException e) {
            throw new RuntimeException("CategoryHierarchyDAO:InsertLinks - " + e.getMessage());
        }
    }

    public void DeleteLinksByParent(String parentId) {
        String query = "DELETE FROM CategoryHierarchy WHERE parent_id = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, parentId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("CategoryHierarchyDAO:DeleteLinksByParent - " + e.getMessage());
        }
    }

    public List<String> SelectSubCategories(String parentId) {
        String query = "SELECT c.category_id FROM Categories c " +
                "JOIN CategoryHierarchy ch ON c.category_id = ch.sub_category_id " +
                "WHERE ch.parent_id = ?";

        List<String> subIds = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, parentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    subIds.add(rs.getString("category_id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("CategoryHierarchyDAO:SelectSubCategories - " + e.getMessage());
        }
        return subIds;
    }

    public void Clean() {
        String query = "DELETE FROM CategoryHierarchy";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("CategoryHierarchyDAO:Clean - " + e.getMessage());
        }
    }

    public void InsertSingleLink(String rootId, String categoryId) {
        if (rootId == null || categoryId == null) {
            throw new IllegalArgumentException("CategoryHierarchyDAO: Parent ID and Category ID cannot be null.");
        }

        String query = "INSERT INTO CategoryHierarchy (parent_id, sub_category_id) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, rootId);
            ps.setString(2, categoryId);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("CategoryHierarchyDAO:InsertSingleLink - " + e.getMessage());
        }
    }
}
