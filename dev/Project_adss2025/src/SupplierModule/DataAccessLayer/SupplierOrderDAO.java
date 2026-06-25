package SupplierModule.DataAccessLayer;
import CrossCuttingPackage.SupplierItemDTO;
import CrossCuttingPackage.SupplierOrderDTO;
import SupplierModule.DomainLayer.SupplierOrder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SupplierOrderDAO {

    private static final String url = "jdbc:sqlite:database.db";
    private static final SupplierItemDAO itemsDAO = new SupplierItemDAO();

    public void Insert(SupplierOrderDTO orderDTO) {
        String q = "INSERT INTO SupplierOrders (orderId, supplierId, orderDate, orderStatus) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            itemsDAO.InsertItems(orderDTO.orderId, orderDTO.items); //insert items of order before

            qu.setString(1, orderDTO.orderId);
            qu.setString(2, orderDTO.supplierId);
            qu.setString(3, orderDTO.orderDate.toString());//FORMATING?
            qu.setString(4, orderDTO.status.toString());

            qu.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException("SupplierOrdersDAO:Insert - " + e.getMessage());
        }
    }

    public void Delete(String orderId) {
        String q = "DELETE FROM SupplierOrders WHERE orderId = ?";

        try (Connection conn = DriverManager.getConnection(url);PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, orderId);

            int rowsDeleted = qu.executeUpdate();

            if (rowsDeleted <= 0)
                throw new RuntimeException("SupplierOrderDAO:Delete - no such order was found in db:"+orderId);


        } catch (SQLException e) {
            throw  new RuntimeException("SupplierOrderDAO:Delete - " + e.getMessage());
        }
    }

    public SupplierOrderDTO SelectByOrderId(String orderId) {
        String q = "SELECT orderId, supplierId, orderDate, orderStatus FROM SupplierOrders WHERE orderId = ?";
        boolean found = false;
        SupplierOrderDTO res = null;

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, orderId);
            ResultSet rs = qu.executeQuery();

            while (rs.next()) {
                found = true;
                String id = rs.getString("orderId");
                res =(new SupplierOrderDTO(
                        id,
                        rs.getString("supplierId"),
                        LocalDate.parse(rs.getString("orderDate")),
                        itemsDAO.SelectAllByOrderId(id),
                        SupplierOrder.MapOrderStatus(rs.getString("orderStatus"))));
            }
            if (!found)
                throw new RuntimeException("SupplierOrdersDAO:SelectByOrderId - no such order was found in db:" + orderId);

        } catch (SQLException e) {
            throw new RuntimeException("SupplierOrdersDAO:SelectByOrderId - " + e.getMessage());
        }
        return res;
    }

    public  List<SupplierOrderDTO> SelectAll() {
        String q = "SELECT orderId, supplierId, orderDate, orderStatus FROM SupplierOrders";
        List<SupplierOrderDTO> res = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            ResultSet rs = qu.executeQuery();

            while (rs.next()) {
                String id = rs.getString("orderId");
                res.add(new SupplierOrderDTO(
                        id,
                        rs.getString("supplierId"),
                        LocalDate.parse(rs.getString("orderDate")),
                        itemsDAO.SelectAllByOrderId(id),
                        SupplierOrder.MapOrderStatus(rs.getString("orderStatus"))));
            }

        } catch (SQLException e) {
            throw new RuntimeException("SupplierOrdersDAO:SelectAll - " + e.getMessage());
        }
        return res;
    }

    public List<SupplierOrderDTO> SelectAllByDateRange(String startDate, String endDate) {
        String q = "SELECT orderId, supplierId, orderDate, orderStatus FROM SupplierOrders WHERE orderDate BETWEEN ? AND ?";
        boolean found = false;

        List<SupplierOrderDTO> res = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url);PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, startDate);
            qu.setString(2, endDate);

            ResultSet rs = qu.executeQuery();

            while (rs.next()) {
                found = true;
                String id = rs.getString("orderId");
                res.add(new SupplierOrderDTO(
                        id,
                        rs.getString("supplierId"),
                        LocalDate.parse(rs.getString("orderDate")),
                        itemsDAO.SelectAllByOrderId(id),
                        SupplierOrder.MapOrderStatus(rs.getString("orderStatus"))));

            }
            if (!found)
                throw new RuntimeException("SupplierOrdersDAO:SelectAllByDateRange - no orders were found between:" + startDate + " and " + endDate);

        } catch (SQLException e) {
            throw new RuntimeException("SupplierOrdersDAO:SelectAllByDateRange - " + e.getMessage());
        }
        return res;
    }

    public  List<SupplierOrderDTO> SelectAllBySupplierId(String supplierId) {
        String q = "SELECT orderId, supplierId, orderDate, orderStatus FROM SupplierOrders WHERE supplierId = ?";
        boolean found = false;

        List<SupplierOrderDTO> res = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, supplierId);
            ResultSet rs = qu.executeQuery();

            while (rs.next()) {
                found = true;
                String id = rs.getString("orderId");
                res.add(new SupplierOrderDTO(
                        id,
                        rs.getString("supplierId"),
                        LocalDate.parse(rs.getString("orderDate")),
                        itemsDAO.SelectAllByOrderId(id),
                        SupplierOrder.MapOrderStatus(rs.getString("orderStatus"))));
            }

            if (!found)
                throw new RuntimeException("SupplierOrdersDAO:SelectAllBySupplierId - no orders were found for supplier:" + supplierId);

        } catch (SQLException e) {
            throw new RuntimeException("SupplierOrdersDAO:SelectAllBySupplierId - " + e.getMessage());
        }
        return res;
    }

    public void UpdateStatus(String orderId, String orderStatus) {
        String q = "UPDATE SupplierOrders SET orderStatus = ? WHERE orderId = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, orderStatus);
            qu.setString(2, orderId);

            int rowsUpdated = qu.executeUpdate();

            if (rowsUpdated <= 0)
                throw new RuntimeException("SupplierOrdersDAO:UpdateStatus - no such order was found in db:" + orderId);

        } catch (SQLException e) {
            throw new RuntimeException("SupplierOrdersDAO:UpdateStatus - " + e.getMessage());
        }
    }

    public void Clean() {
        String q = "DELETE FROM SupplierOrders";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            itemsDAO.Clean();
            qu.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("SupplierOrdersDAO:Clean - " + e.getMessage());
        }
    }

}
