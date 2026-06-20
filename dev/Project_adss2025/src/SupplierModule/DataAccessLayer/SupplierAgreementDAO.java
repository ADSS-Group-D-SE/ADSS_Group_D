package SupplierModule.DataAccessLayer;
import CrossCuttingPackage.DiscountRuleDTO;
import CrossCuttingPackage.SupplierAgreementDTO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupplierAgreementDAO {

    private static final String url = "jdbc:sqlite:database.db";
    private static final DiscountRuleDAO discountRuleDAO = new DiscountRuleDAO();

    public void InsertItem(String supplierId, String catalogNumber, double price) {

        String q = "INSERT INTO SupplierAgreements(supplierId, catalog_number, price) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, supplierId);
            qu.setString(2, catalogNumber);
            qu.setDouble(3, price);

            qu.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("SupplierAgreementDAO:Insert - " + e.getMessage());
        }
    }

    public void InsertItems(String supplierId, SupplierAgreementDTO agDTO)
    {
        discountRuleDAO.InsertRules(supplierId,agDTO.rules);
        for(Map.Entry<String,Double> en: agDTO.agreement.entrySet())
            this.InsertItem(supplierId,en.getKey(),en.getValue());
    }

    public void Update(String supplierId, String catalogNumber, double price) {
        String q = "UPDATE SupplierAgreements SET price = ? WHERE supplierId = ? AND catalog_number = ?";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setDouble(1, price);
            qu.setString(2, supplierId);
            qu.setString(3, catalogNumber);

            int rowsUpdated = qu.executeUpdate();

            if (rowsUpdated  <= 0)
                throw new RuntimeException("SupplierAgreementDAO:Update - failed to find an agreement belonging to supplier: " + supplierId);

        } catch (SQLException e) {
            throw new RuntimeException("SupplierAgreementDAO:Update - " + e.getMessage());
        }
    }

    public void RemoveItem(String supplierId, String catalogNumber)
    {
        String q = "DELETE FROM SupplierAgreements WHERE supplierId = ? AND catalog_number = ?";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            discountRuleDAO.RemoveBySupplierIdAndCatalog(supplierId,catalogNumber); // removes discountRule Aswell.

            qu.setString(1, supplierId);
            qu.setString(2, catalogNumber);

            int rowsDeleted = qu.executeUpdate();

            if (rowsDeleted <= 0)
                throw new RuntimeException("SupplierAgreementDAO:RemoveItem - failed to find an agreement belonging to supplier: " + supplierId);



        } catch (SQLException e) {
            throw new RuntimeException("SupplierAgreementDAO:RemoveItem - " + e.getMessage());
        }
    }

    public void DeleteAgreement(String supplierId) {
        String q = "DELETE FROM SupplierAgreements WHERE supplierId = ?";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            discountRuleDAO.RemoveBySupplierId(supplierId); // deletes all supplierAgreement discountRule.
            qu.setString(1, supplierId);

            qu.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("SupplierAgreementDAO:DeleteAgreement - " + e.getMessage());
        }
    }

    public SupplierAgreementDTO SelectAgreement(String supplierId) {

        String q = "SELECT supplierId, catalog_number, price FROM SupplierAgreements WHERE supplierId = ?";
        HashMap<String,Double> temp = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, supplierId);

            try (ResultSet rs = qu.executeQuery()) {
                boolean found = false;

                while (rs.next()) {
                    found = true;
                    temp.put(rs.getString("catalog_number"),rs.getDouble("price"));
                }
                if (!found)
                    throw new RuntimeException("SupplierAgreementDAO:SelectBySupplierId - failed to find agreements belonging to supplier: " + supplierId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("SupplierAgreementDAO:SelectAgreement - " + e.getMessage());
        }
        return new SupplierAgreementDTO(supplierId,temp,discountRuleDAO.Select(supplierId));
    }

    public void Clean() {
        String q = "DELETE FROM SupplierAgreements";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            discountRuleDAO.Clean();
            qu.executeUpdate();
             // clean all the discountRules linked to agreements.

        } catch (SQLException e) {
            throw new RuntimeException("SupplierAgreementDAO:Clean - " + e.getMessage());
        }
    }
}
