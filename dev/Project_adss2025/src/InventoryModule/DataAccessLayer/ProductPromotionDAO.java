package InventoryModule.DataAccessLayer;

import CrossCuttingPackage.promotionDTO;
import InventoryModule.DomainLayer.PromotionScope;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ProductPromotionDAO {

    private static final String url = "jdbc:sqlite:database.db";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void insertPromotion(String catalogNumber, promotionDTO promotion) {
        String q = "INSERT INTO PromotionProducts (id, discountPre, catalog_number, endDate, scope) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, promotion.getId());
            qu.setDouble(2, promotion.getDiscountPercentage());
            qu.setString(3, catalogNumber);
            qu.setString(4, promotion.getEndDate());
            qu.setString(5, promotion.getScope());

            qu.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("PromotionProductDAO:InsertItem - " + e.getMessage());
        }
    }

    public void insertPromotions(String catalogNumber, List<promotionDTO> promotions) {
        for (promotionDTO promo : promotions) {
            this.insertPromotion(catalogNumber, promo);
        }
    }

    public void Update(String catalogNumber,promotionDTO promotion) {
        String sql = "INSERT OR REPLACE INTO PromotionProducts (id, discountPre, catalog_number, endDate, scope) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(sql)) {

            qu.setString(1, promotion.getId());

            qu.setDouble(2, promotion.getDiscountPercentage());

            qu.setString(3, catalogNumber);

            qu.setString(4, promotion.getEndDate().toString());

            qu.setString(5, promotion.getScope());

            int rowsUpdated = qu.executeUpdate();

            if (rowsUpdated <= 0) {
                throw new RuntimeException("PromotionProductDAO:Update - failed to find a promotion with ID: " + promotion.getId());
            }

        } catch (SQLException e) {
            throw new RuntimeException("PromotionProductDAO:Update - " + e.getMessage());
        }
    }

    public void updatePromotions( String catalognumber,List<promotionDTO> promotions) {
        for (promotionDTO promo : promotions) {
            this.Update(catalognumber,promo);
        }
    }

    public void RemoveItem(String id) {
        String q = "DELETE FROM PromotionProducts WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, id);
            int rowsDeleted = qu.executeUpdate();

            if (rowsDeleted <= 0) {
                throw new RuntimeException("PromotionProductDAO:RemoveItem - failed to find a promotion with ID: " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException("PromotionProductDAO:RemoveItem - " + e.getMessage());
        }
    }

    public void DeletePromotionsByProduct(String catalogNumber) {
        String q = "DELETE FROM PromotionProducts WHERE catalog_number = ?";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, catalogNumber);
            qu.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("PromotionProductDAO:DeletePromotionsByProduct - " + e.getMessage());
        }
    }

    public List<promotionDTO> selectByProduct(String catalogNumber) {
        String q = "SELECT id, discountPre, endDate, scope FROM PromotionProducts WHERE catalog_number = ?";
        List<promotionDTO> temp = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, catalogNumber);

            try (ResultSet rs = qu.executeQuery()) {
                while (rs.next()) {
                    LocalDate localDate = LocalDate.parse(rs.getString("endDate"), DATE_FORMATTER);
                    String formattedDateForConstructor = localDate.format(DATE_FORMATTER);

                    PromotionScope scope = PromotionScope.valueOf(rs.getString("scope"));

                    promotionDTO promo = new promotionDTO(
                            rs.getString("id"),
                            rs.getDouble("discountPre"),
                            formattedDateForConstructor,
                            scope.name()
                    );

                    temp.add(promo);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("PromotionProductDAO:selectByProduct - " + e.getMessage());
        }
        return temp;
    }

    public void Clean() {
        String q = "DELETE FROM PromotionProducts";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {
            qu.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("PromotionProductDAO:Clean - " + e.getMessage());
        }
    }
}
