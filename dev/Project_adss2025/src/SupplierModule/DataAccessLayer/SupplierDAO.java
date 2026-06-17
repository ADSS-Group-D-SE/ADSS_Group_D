package SupplierModule.DataAccessLayer;
import CrossCuttingPackage.SupplierDTO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class SupplierDAO {

    private static final String url = "jdbc:sqlite:database.db";
    private static final SupplierAgreementDAO agreementDAO = new SupplierAgreementDAO();
    private static final ContactDAO contactDAO = new ContactDAO();

    public void Insert(String supId,String reg,String name,String bankAcc,String paymentTerms,String ddc)
    {
        String q = "INSERT INTO Suppliers (supplierId, name, regNumber, bankAccount, payTerms, ddc) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, supId);qu.setString(2, name);
            qu.setString(3, reg);
            qu.setString(4, bankAcc);
            qu.setString(5, paymentTerms);
            qu.setString(6, ddc);

            qu.executeUpdate();

        } catch (SQLException e) {
        throw new RuntimeException("SupplierDAO:Insert - " + e.getMessage());
        }
    }

/*
Method that removes a supplier from db.
 */
    public void Delete(String supplierId) {
        String q = "DELETE FROM Suppliers WHERE SupplierId = ?";

        try (Connection conn = DriverManager.getConnection(url);PreparedStatement qu = conn.prepareStatement(q)) {

        qu.setString(1, supplierId);

        int rowsDeleted = qu.executeUpdate();

        if (rowsDeleted <= 0)
            throw new RuntimeException("SuppliersDAO:RemoveSupplier - no such supplier was found in db:"+supplierId);

        agreementDAO.DeleteAgreement(supplierId);
        contactDAO.RemoveSupplier(supplierId); // deletes supplier's data from all tables.

        } catch (SQLException e) {
            throw  new RuntimeException("SuppliersDAO:RemoveSupplier - " + e.getMessage());
        }
    }

    public void UpdateSupplier(String supplierId, String name, String regNumber, String bankAccount, String payTerms, String ddc) {
        String q = "UPDATE Suppliers SET name = ?, regNumber = ?, bankAccount = ?, payTerms = ?, ddc = ? WHERE supplierId = ?";

        try (Connection conn = DriverManager.getConnection(url);PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, name);
            qu.setString(2, regNumber);
            qu.setString(3, bankAccount);
            qu.setString(4, payTerms);
            qu.setString(5, ddc);
            qu.setString(6, supplierId);

            int rowsUpdated = qu.executeUpdate();

            if (rowsUpdated <= 0)
                throw new RuntimeException("SupplierDAO:UpdateSupplier - no such supplier was found in db:"+supplierId);

        } catch (SQLException e) {
            throw  new RuntimeException("SuppliersDAO:UpdateSupplier - " + e.getMessage());
        }
    }

    public SupplierDTO Select(String supplierId) {
        String q = "SELECT supplierId, name, regNumber, bankAccount, payTerms, ddcFROM Suppliers WHERE supplierId = ?";
        SupplierDTO res;
        try (Connection conn = DriverManager.getConnection(url);PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, supplierId);

            try (ResultSet rs = qu.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("supplierId");

                    res = new SupplierDTO(id,
                            rs.getString("regNumber"),
                            rs.getString("name"),
                            rs.getString("bankAccount"),
                            rs.getString("payTerms"),
                            contactDAO.Select(id),
                            rs.getString("ddc"),
                            agreementDAO.SelectAgreement(id));
                } else
                    throw new RuntimeException("SupplierDAO:Select - No supplier found with supplierId: " + supplierId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("SupplierDAO:Select - " + e.getMessage());
        }
        return res;
    }

    public List<SupplierDTO> SelectAll() {
        String q = "SELECT supplierId, name, regNumber, bankAccount, payTerms, ddc FROM Suppliers";
        List<SupplierDTO> res = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url);PreparedStatement qu = conn.prepareStatement(q); ResultSet rs = qu.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("supplierId");

                SupplierDTO supplier = new SupplierDTO(
                        id,
                        rs.getString("regNumber"),
                        rs.getString("name"),
                        rs.getString("bankAccount"),
                        rs.getString("payTerms"),
                        contactDAO.Select(id),
                        rs.getString("ddc"),
                        agreementDAO.SelectAgreement(id)
                );
                res.add(supplier);
            }

        } catch (SQLException e) {
            throw new RuntimeException("SupplierDAO:SelectAll - " + e.getMessage());
        }
        return res;
    }

    public void Clean() {
        String q = "DELETE FROM Suppliers";

        try (Connection conn = DriverManager.getConnection(url);PreparedStatement qu = conn.prepareStatement(q)) {

            qu.executeUpdate();
            agreementDAO.Clean();
            contactDAO.Clean();

        } catch (SQLException e) {
            throw  new RuntimeException("SuppliersDAO:Clean - " + e.getMessage());
        }
    }
}
