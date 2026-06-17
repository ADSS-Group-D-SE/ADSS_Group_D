package SupplierModule.DataAccessLayer;
import CrossCuttingPackage.ContactDTO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContactDAO {
    private static final String url = "jdbc:sqlite:database.db";

    public void Insert(String supplierId, String name, String email, String phoneNumber) {

        String q = "INSERT INTO ContactInfo (supplierId, name, email, phoneNumber) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, supplierId);
            qu.setString(2, name);
            qu.setString(3, email);
            qu.setString(4, phoneNumber);

            qu.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("ContactInfoDAO:Insert - " + e.getMessage());
        }
    }

    public void InsertContacts(String supplierId, List<ContactDTO> list)
    {
        for(ContactDTO c: list)
            Insert(supplierId,c.name,c.email, c.phoneNumber);
    }

    public void RemoveContact(String supplierId,String name) {
        String q = "DELETE FROM ContactInfo WHERE supplierId = ? AND name = ?";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, supplierId);
            qu.setString(1, name);

            int rowsDeleted = qu.executeUpdate();

            if (rowsDeleted <= 0)
                throw new RuntimeException("ContactInfoDAO:RemoveContact - failed to find contact info belonging to supplier: " + supplierId);

        } catch (SQLException e) {
            throw new RuntimeException("ContactInfoDAO:RemoveContact - " + e.getMessage());
        }
    }

    public void RemoveSupplier(String supplierId) {
        String q = "DELETE FROM ContactInfo WHERE supplierId = ?";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, supplierId);

            int rowsDeleted = qu.executeUpdate();

            if (rowsDeleted <= 0)
                throw new RuntimeException("ContactInfoDAO:RemoveSupplier - failed to find contact info belonging to supplier: " + supplierId);

        } catch (SQLException e) {
            throw new RuntimeException("ContactInfoDAO:RemoveSupplier - " + e.getMessage());
        }
    }

    public List<ContactDTO> Select(String supplierId) {
        String q = "SELECT supplierId, name, email, phoneNumber FROM ContactInfo WHERE supplierId = ?";
        List<ContactDTO> res = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, supplierId);

            try (ResultSet rs = qu.executeQuery()) {
                boolean found = false;

                while (rs.next()) {
                    found = true;
                    res.add(new ContactDTO(rs.getString("name"),rs.getString("email"),rs.getString("phoneNumber")));
                }
                if (!found)
                    throw new RuntimeException("ContactInfoDAO:Select - failed to find contact info belonging to supplier: " + supplierId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("ContactInfoDAO:Select - " + e.getMessage());
        }
        return res;
    }

    public void Update(String supplierId, String name, String email, String phoneNumber) {
        String q = "UPDATE ContactInfo SET name = ?, email = ?, phoneNumber = ? WHERE supplierId = ?";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, name);
            qu.setString(2, email);
            qu.setString(3, phoneNumber);
            qu.setString(4, supplierId);

            int rowsUpdated = qu.executeUpdate();

            if (rowsUpdated <= 0)
                throw new RuntimeException("ContactInfoDAO:Update - failed to find contact info belonging to supplier: " + supplierId);

        } catch (SQLException e) {
            throw new RuntimeException("ContactInfoDAO:Update - " + e.getMessage());
        }
    }

    public void Clean() {
        String q = "DELETE FROM ContactInfo";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement qu = conn.prepareStatement(q)) {

            qu.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("ContactInfoDAO:Clean - " + e.getMessage());
        }
    }
}
