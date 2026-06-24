package InventoryModule.DataAccessLayer;

import CrossCuttingPackage.promotionDTO;
import InventoryModule.DomainLayer.PromotionScope;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CategoryPromotionDAO {
    private static final String url = "jdbc:sqlite:database.db";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void insertPromotion(String categoryId, promotionDTO promotion) {
        String q = "INSERT INTO PromotionCategories (id, discountPre, category_id, endDate, scope) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, promotion.getId());
            qu.setDouble(2, promotion.getDiscountPercentage());
            qu.setString(3, categoryId);
            qu.setString(4, promotion.getEndDate());
            qu.setString(5, promotion.getScope());

            qu.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("CategoryPromotionDAO:insertPromotion - " + e.getMessage());
        }
    }

    public void insertPromotions(String categoryId, List<promotionDTO> promotions) {
        for (promotionDTO promo : promotions) {
            this.insertPromotion(categoryId, promo);
        }
    }

    public void Update(String categoryId,promotionDTO p) {
        String q = "UPDATE PromotionCategories SET discountPre = ?, category_id = ?, endDate = ?, scope = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setDouble(1, p.getDiscountPercentage());
            qu.setString(2, categoryId);
            qu.setString(3, p.getEndDate());
            qu.setString(4, p.getScope());
            qu.setString(5, p.getId());

            int rowsUpdated = qu.executeUpdate();

            if (rowsUpdated <= 0)
                throw new RuntimeException("PromotionCategoriesDAO:Update - no such promotion was found in db:" + p.getId());

        } catch (SQLException e) {
            throw new RuntimeException("PromotionCategoriesDAO:Update - " + e.getMessage());
        }
    }

    public void updatePromotions(String categoryId, List<promotionDTO> promotions) {
        for (promotionDTO promo : promotions) {
            this.Update(categoryId,promo);
        }
    }

    public void RemoveItem(String id) {
        String q = "DELETE FROM PromotionCategories WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, id);
            int rowsDeleted = qu.executeUpdate();

            if (rowsDeleted <= 0) {
                throw new RuntimeException("CategoryPromotionDAO:RemoveItem - failed to find a promotion with ID: " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException("CategoryPromotionDAO:RemoveItem - " + e.getMessage());
        }
    }

    public void DeletePromotionsByCategory(String categoryId) {
        String q = "DELETE FROM PromotionCategories WHERE category_id = ?";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, categoryId);
            qu.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("CategoryPromotionDAO:DeletePromotionsByCategory - " + e.getMessage());
        }
    }

    public List<promotionDTO> selectByCategory(String categoryId) {
        String q = "SELECT id, discountPre, endDate, scope FROM PromotionCategories WHERE category_id = ?";
        List<promotionDTO> temp = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, categoryId);

            try (ResultSet rs = qu.executeQuery()) {
                while (rs.next()) {
                    String rawDate = rs.getString("endDate");

                    if (rawDate == null || rawDate.trim().isEmpty()) {
                        continue;
                    }

                    LocalDate localDate = LocalDate.parse(rawDate, DATE_FORMATTER);
                    String formattedDateForConstructor = localDate.format(DATE_FORMATTER);

                    PromotionScope scope = PromotionScope.PRODUCT;
                    String rawScope = rs.getString("scope");
                    if (rawScope != null) {
                        scope = PromotionScope.valueOf(rawScope);
                    }

                    promotionDTO promo = new promotionDTO(
                            rs.getString("id"),
                            rs.getDouble("discountPre"),
                            formattedDateForConstructor,
                            scope.name()
                    );
                    temp.add(promo);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("CategoryPromotionDAO:selectByCategory failed - " + e.getMessage(), e);
        }
        return temp;
    }

    public void Clean() {
        String q = "DELETE FROM PromotionCategories";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {
            qu.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("CategoryPromotionDAO:Clean - " + e.getMessage());
        }
    }
}
