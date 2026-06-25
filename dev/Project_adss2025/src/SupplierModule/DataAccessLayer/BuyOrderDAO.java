package SupplierModule.DataAccessLayer;
import CrossCuttingPackage.BuyOrderDTO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class BuyOrderDAO {

    private static final String url = "jdbc:sqlite:database.db";
    private static final BuyOrderItemsDAO itemsDAO = new BuyOrderItemsDAO();

    public void Insert(BuyOrderDTO dto) {
        String q = "INSERT INTO BuyOrders (boId, supId, regularDays, nextDeliveryDate) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            itemsDAO.InsertItems(dto.buyOrderID,dto.items); // insert items

            qu.setString(1, dto.buyOrderID);
            qu.setString(2, dto.supId);
            qu.setString(3, dto.regularDays);
            qu.setString(4, dto.nextDeliveryDate);

            qu.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("BuyOrdersDAO:Insert - " + e.getMessage());
        }
    }

    public void Remove(String boId) {
        String q = "DELETE FROM BuyOrders WHERE boId = ?";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            itemsDAO.RemoveItemsFromBO(boId);
            qu.setString(1, boId);

            int rowsDeleted = qu.executeUpdate();

            if (rowsDeleted <= 0)
                throw new RuntimeException("BuyOrdersDAO:Remove - no such buy order was found in db:" + boId);

        } catch (SQLException e) {
            throw new RuntimeException("BuyOrdersDAO:Remove - " + e.getMessage());
        }
    }

    public List<BuyOrderDTO> SelectAll() {
        String q = "SELECT boId, supId, regularDays, nextDeliveryDate FROM BuyOrders";
        List<BuyOrderDTO> res = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q);
             ResultSet rs = qu.executeQuery()) {

            while (rs.next()) {

                String boId= rs.getString("boId");
                res.add(new BuyOrderDTO(
                        boId,
                        rs.getString("supId"),
                        itemsDAO.SelectAllItemsByOrderId(boId),
                        rs.getString("regularDays"),
                        rs.getString("nextDeliveryDate"))
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException("BuyOrdersDAO:SelectAll - " + e.getMessage());
        }
        return res;
    }

    public void Update(String boId, String supId, String regularDays, String nextDeliveryDate) {
        String q = "UPDATE BuyOrders SET supId = ?, regularDays = ?, nextDeliveryDate = ? WHERE boId = ?";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, supId);
            qu.setString(2, regularDays);
            qu.setString(3, nextDeliveryDate);
            qu.setString(4, boId);

            int rowsUpdated = qu.executeUpdate();

            if (rowsUpdated <= 0)
                throw new RuntimeException("BuyOrdersDAO:Update - no such buy order was found in db:" + boId);

        } catch (SQLException e) {
            throw new RuntimeException("BuyOrdersDAO:Update - " + e.getMessage());
        }
    }

    public void Clean() {
        String q = "DELETE FROM BuyOrders";

        try (Connection conn = DriverManager.getConnection(url);PreparedStatement qu = conn.prepareStatement(q)) {

            itemsDAO.Clean();
            qu.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("BuyOrdersDAO:Clean - " + e.getMessage());
        }
    }
}


