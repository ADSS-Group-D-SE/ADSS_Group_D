package InventoryModule.DataAccessLayer;

import CrossCuttingPackage.FaultyProductDTO;
import InventoryModule.DomainLayer.FaultyProductDL;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;

public class FaultyReportDAO {
    private static final String url = "jdbc:sqlite:database.db";
    public static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("dd/MM/yyyy")
            .optionalStart()
            .appendPattern(" HH:mm")
            .optionalEnd()
            .toFormatter();


    /**
     * Returns the highest report_id currently stored, or 0 if there are no reports.
     * Used to seed the in-memory id counter so generated ids never collide with persisted ones.
     */
    public int getMaxReportId() {
        String q = "SELECT COALESCE(MAX(report_id), 0) FROM FaultyProductReports";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(q);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("FaultyReportDAO:getMaxReportId - " + e.getMessage());
        }
    }

    public void Insert(FaultyProductDTO report) {
        String q = "INSERT INTO FaultyProductReports (report_id, name, catalog_number, location, description, dateOnReport) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement ps = conn.prepareStatement(q)) {

            ps.setInt(1, report.getReportID());
            ps.setString(2, report.getName());
            ps.setString(3, report.getCatalogNumber());
            ps.setString(4, report.getLocation());
            ps.setString(5, report.getDescription());

            ps.setString(6, report.getDateOnReport());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("FaultyReportDAO:Insert - " + e.getMessage());
        }
    }


    public List<FaultyProductDL> SelectByDateRange(LocalDate fromDate, LocalDate toDate) {
        String q = "SELECT report_id, name, catalog_number, location, description, dateOnReport FROM FaultyProductReports WHERE (substr(dateOnReport, 7, 4) || '-' || substr(dateOnReport, 4, 2) || '-' || substr(dateOnReport, 1, 2)) BETWEEN ? AND ?";
        List<FaultyProductDL> results = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(q)) {

            ps.setString(1, fromDate.toString());
            ps.setString(2, toDate.toString());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String rawDate = rs.getString("dateOnReport");

                if (rawDate == null || rawDate.trim().isEmpty())
                    continue;

                LocalDate reportDate = LocalDate.parse(rawDate.substring(0, 10), DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                FaultyProductDL report = new FaultyProductDL(
                        rs.getInt("report_id"),
                        rs.getString("name"),
                        rs.getString("catalog_number"),
                        rs.getString("location"),
                        rs.getString("description"),
                        reportDate
                );

                results.add(report);
            }

        } catch (SQLException e) {
            throw new RuntimeException("FaultyReportDAO:SelectByDateRange - " + e.getMessage());
        }

        return results;
    }

    public void Clean() {
        String q = "DELETE FROM FaultyProductReports";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(q)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("FaultyReportDAO:Clean - " + e.getMessage());
        }
    }

    public void deleteby(FaultyProductDTO dto) {
        String q = "DELETE FROM FaultyProductReports WHERE report_id = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(q)) {

            ps.setInt(1, dto.getReportID());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("FaultyReportDAO:Delete - " + e.getMessage());
        }
    }

    public FaultyProductDL findbyid(int reportId) {
        String q = "SELECT report_id, name, catalog_number, location, description, dateOnReport " +
                "FROM FaultyProductReports " +
                "WHERE report_id = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(q)) {

            ps.setInt(1, reportId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    String dateStr = rs.getString("dateOnReport");
                    java.time.LocalDate localDate = java.time.LocalDate.parse(dateStr, DATE_FORMATTER);
                    java.time.LocalDateTime reportTime = localDate.atStartOfDay();

                    return new FaultyProductDL(
                            rs.getInt("report_id"),
                            rs.getString("name"),
                            rs.getString("catalog_number"),
                            rs.getString("location"),
                            rs.getString("description"),
                            reportTime.toLocalDate()
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("FaultyReportDAO:findbyid - " + e.getMessage());
        }

        return null;
    }
}
