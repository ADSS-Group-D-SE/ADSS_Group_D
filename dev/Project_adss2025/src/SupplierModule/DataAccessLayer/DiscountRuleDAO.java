package SupplierModule.DataAccessLayer;
import CrossCuttingPackage.ContactDTO;
import CrossCuttingPackage.DiscountRuleDTO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class DiscountRuleDAO {

    private static final String url = "jdbc:sqlite:database.db";

    public void Insert(String supplierId, String name, String catalogNumber, double discountPre, int minAmount) {
        String q = "INSERT INTO DiscountRules (supplierId, name, catalog_number, discount_pre, minAmount) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, supplierId);
            qu.setString(2, name);
            qu.setString(3, catalogNumber);
            qu.setDouble(4, discountPre);
            qu.setInt(5, minAmount);

            qu.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("DiscountRuleDAO:Insert - " + e.getMessage());
        }
    }

    public void Remove(String supplierId, String name) {
        String q = "DELETE FROM DiscountRules WHERE supplierId = ? AND name = ?";

        try (Connection conn = DriverManager.getConnection(url);PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, supplierId);
            qu.setString(2, name);

            int rowsDeleted = qu.executeUpdate();

            if (rowsDeleted <= 0)
                throw new RuntimeException("DiscountRuleDAO:Remove - failed to find discount rule: " + name + " belonging to supplier: " + supplierId);

        } catch (SQLException e) {
            throw new RuntimeException("DiscountRuleDAO:Remove - " + e.getMessage());
        }
    }

    public void RemoveBySupplierId(String supplierId) {
        String q = "DELETE FROM DiscountRules WHERE supplierId = ?";

        try (Connection conn = DriverManager.getConnection(url);PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, supplierId);

            int rowsDeleted = qu.executeUpdate();

            if (rowsDeleted <= 0)
                throw new RuntimeException("DiscountRuleDAO:RemoveBySupplierId - failed to find discount rules belonging to supplier: " + supplierId);

        } catch (SQLException e) {
            throw new RuntimeException("DiscountRuleDAO:RemoveBySupplierId - " + e.getMessage());
        }
    }

    public void Update(String supplierId, String name, String catalogNumber, double discountPre, int minAmount) {
        String q = "UPDATE DiscountRules SET catalog_number = ?, discount_pre = ?, minAmount = ? WHERE supplierId = ? AND name = ?";

        try (Connection conn = DriverManager.getConnection(url);PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, catalogNumber);
            qu.setDouble(2, discountPre);
            qu.setInt(3, minAmount);
            qu.setString(4, supplierId);
            qu.setString(5, name);

            int rowsUpdated = qu.executeUpdate();

            if (rowsUpdated <= 0)
                throw new RuntimeException("DiscountRuleDAO:Update - failed to find discount rule: " + name + " belonging to supplier: " + supplierId);

        } catch (SQLException e) {
            throw new RuntimeException("DiscountRuleDAO:Update - " + e.getMessage());
        }
    }

    public List<DiscountRuleDTO> Select(String supplierId) {
        String q = "SELECT supplierId, name, catalog_number, discount_pre, minAmount FROM DiscountRules WHERE supplierId = ?";
        List<DiscountRuleDTO> res = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, supplierId);

            ResultSet rs = qu.executeQuery();
            boolean found = false;

            while (rs.next()) {
                found = true;

                res.add(new DiscountRuleDTO(rs.getString("name"),
                        rs.getString("catalog_number"),
                        rs.getInt("minAmount"),
                        rs.getDouble("discount_pre")));
            }
            if (!found)
                throw new RuntimeException("DiscountRuleDAO:Select - failed to find discount rules belonging to supplier: " + supplierId);

        } catch (SQLException e) {
            throw new RuntimeException("DiscountRuleDAO:Select - " + e.getMessage());
        }
        return res;
    }

    public void Clean() {
        String q = "DELETE FROM DiscountRules";

        try (Connection conn = DriverManager.getConnection(url);PreparedStatement qu = conn.prepareStatement(q)) {

            qu.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("DiscountRuleDAO:RemoveBySupplierId - " + e.getMessage());
        }
    }
}
