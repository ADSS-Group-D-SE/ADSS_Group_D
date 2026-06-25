package SupplierModule.DataAccessLayer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class BuyOrderItemsDAO {

    private static final String url = "jdbc:sqlite:database.db";

    public void InsertItem(String boId, String catalogNumber, int amount) {
        String q = "INSERT INTO BuyOrderItems (boId, catalog_number, amount) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, boId);
            qu.setString(2, catalogNumber);
            qu.setInt(3, amount);

            qu.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("BuyOrderItemsDAO:InsertItem - " + e.getMessage());
        }
    }

    public void InsertItems(String boId,HashMap<String,Integer> items)
    {
        for(Map.Entry<String,Integer> en: items.entrySet())
        {
            InsertItem(boId,en.getKey(),en.getValue());
        }
    }

    public void RemoveItem(String boId, String catalogNumber) {
        String q = "DELETE FROM BuyOrderItems WHERE boId = ? AND catalog_number = ?";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, boId);
            qu.setString(2, catalogNumber);

            int rowsDeleted = qu.executeUpdate();

            if (rowsDeleted <= 0)
                throw new RuntimeException("BuyOrderItemsDAO:RemoveItem - no such item was found in buy order:" + boId + ", catalog number:" + catalogNumber);

        } catch (SQLException e) {
            throw new RuntimeException("BuyOrderItemsDAO:RemoveItem - " + e.getMessage());
        }
    }

    public void RemoveItemsFromBO(String boId) {
        String q = "DELETE FROM BuyOrderItems WHERE boId = ?";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, boId);
            qu.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("BuyOrderItemsDAO:RemoveItem - " + e.getMessage());
        }
    }
    public HashMap<String,Integer> SelectAllItemsByOrderId(String boId) {
        String q = "SELECT boId, catalog_number, amount FROM BuyOrderItems WHERE boId = ?";
        HashMap<String,Integer> res = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, boId);

            try (ResultSet rs = qu.executeQuery()) {
                while (rs.next()) {
                    res.put(rs.getString("catalog_number"),rs.getInt("amount"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("BuyOrderItemsDAO:SelectAllItemsByOrderId - " + e.getMessage());
        }
        return res;
    }
    public void UpdateAmount(String boId, String catalogNumber, int amount) {
        String q = "UPDATE BuyOrderItems SET amount = ? WHERE boId = ? AND catalog_number = ?";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setInt(1, amount);
            qu.setString(2, boId);
            qu.setString(3, catalogNumber);

            int rowsUpdated = qu.executeUpdate();

            if (rowsUpdated <= 0)
                throw new RuntimeException("BuyOrderItemsDAO:UpdateAmount - no such item was found in buy order:" + boId + ", catalog number:" + catalogNumber);

        } catch (SQLException e) {
            throw new RuntimeException("BuyOrderItemsDAO:UpdateAmount - " + e.getMessage());
        }
    }
    public void Clean() {
        String q = "DELETE FROM BuyOrderItems";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("BuyOrderItemsDAO:Clean - " + e.getMessage());
        }
    }
}
