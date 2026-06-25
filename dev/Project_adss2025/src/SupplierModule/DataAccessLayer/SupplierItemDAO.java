package SupplierModule.DataAccessLayer;
import CrossCuttingPackage.SupplierItemDTO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierItemDAO {

    private static final String url = "jdbc:sqlite:database.db";

    public void Insert(String orderId, String catalogNumber, int amount, double price) {
        String q = "INSERT INTO SupplierItems (orderId, catalog_number, amount, price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, orderId);
            qu.setString(2, catalogNumber);
            qu.setInt(3, amount);
            qu.setDouble(4, price);

            qu.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("SupplierItemsDAO:Insert - " + e.getMessage());
        }
    }

    public void InsertItems(String orderId,List<SupplierItemDTO> items)
    {
        for(SupplierItemDTO item:items)
            Insert(orderId,item.catalogNumber,item.amount,item.price);
    }

    public List<SupplierItemDTO> SelectAllByOrderId(String orderId) {
        String q = "SELECT orderId, catalog_number, amount, price FROM SupplierItems WHERE orderId = ?";
        boolean found = false;

        List<SupplierItemDTO> res = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, orderId);

            try (ResultSet rs = qu.executeQuery()) {
                while (rs.next()) {
                    found = true;
                    res.add(new SupplierItemDTO(
                            rs.getString("catalog_number"),
                            rs.getDouble("price"),
                            rs.getInt("amount")));
                }
            }

            if (!found)
                throw new RuntimeException("SupplierItemsDAO:SelectAllByOrderId - no items were found for order:" + orderId);

        } catch (SQLException e) {
            throw new RuntimeException("SupplierItemsDAO:SelectAllByOrderId - " + e.getMessage());
        }
        return res;
    }

    public void Clean() {
        String q = "DELETE FROM SupplierItems";

        try (Connection conn = DriverManager.getConnection(url);PreparedStatement qu = conn.prepareStatement(q)) {

            qu.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("SupplierItemsDAO:Clean - " + e.getMessage());
        }
    }
}
