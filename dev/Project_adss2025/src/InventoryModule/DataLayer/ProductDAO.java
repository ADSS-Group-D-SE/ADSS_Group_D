package InventoryModule.DataLayer;

import CrossCuttingPackage.productDTO;
import InventoryModule.DomainLayer.ProductDL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    private static final String url = "jdbc:sqlite:database.db";
    private static final ProductPromotionDAO promotionDAO = new ProductPromotionDAO();


    public void Insert(productDTO product)
    {
        String sql = "INSERT INTO Products(catalog_number, name, mainCategory, subCategory, subSubCategory, " +
                "warehouse, location, manufacturer, amount_on_shelves, amount_on_stock, " +
                "price_to_consumer, price_to_supply, supplier_discount, minAmountAlert) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);PreparedStatement ps = conn.prepareStatement(sql)) {

            promotionDAO.insertPromotions(product.getCatalogNumber(),product.getProductDiscounts());
            ps.setString(1, product.getCatalogNumber());
            ps.setString(2, product.getName());
            ps.setString(3, product.getMain_category_id());
            ps.setString(4, product.getSub_category_id());
            ps.setString(5, product.getSubsub_category_id());
            ps.setString(6, product.getWarehouseName().toString());
            ps.setString(7, product.getShelfLocation().toString());
            ps.setString(8, product.getManufacturer());
            ps.setInt(9, product.getAmountOnShelves());
            ps.setInt(10, product.getAmountOnStock());
            ps.setDouble(11, product.getPriceToConsumer());
            ps.setDouble(12, product.getPriceToSupply());
            ps.setDouble(13, product.getSupplierDiscount());
            ps.setInt(14, product.getMinAmountAlert());

            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("ProductDAO:Insert - " + e.getMessage());
        }
    }


    public void Delete(String catalog_number) {
        String q = "DELETE FROM Products WHERE catalog_number = ?";

        try (Connection conn = DriverManager.getConnection(url);PreparedStatement qu = conn.prepareStatement(q)) {

            qu.setString(1, catalog_number);

            promotionDAO.DeletePromotionsByProduct(catalog_number);

            int rowsDeleted = qu.executeUpdate();

            if (rowsDeleted <= 0)
                throw new RuntimeException("ProductDAO:RemoveProduct - no such product was found in db:"+catalog_number);


        } catch (SQLException e) {
            throw  new RuntimeException("ProductDAO:RemoveProduct - " + e.getMessage());
        }
    }


    public void UpdateProduct(productDTO product) {
        String sql = "UPDATE Products SET name = ?, mainCategory = ?, subCategory = ?, subSubCategory = ?, " +
                "warehouse = ?, location = ?, manufacturer = ?, amount_on_shelves = ?, amount_on_stock = ?, " +
                "price_to_consumer = ?, price_to_supply = ?, supplier_discount = ?, minAmountAlert = ? " +
                "WHERE catalog_number = ?";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement ps = conn.prepareStatement(sql)) {
            promotionDAO.updatePromotions(product.getProductDiscounts());

            ps.setString(1, product.getName());
            ps.setString(2, product.getMain_category_id());
            ps.setString(3, product.getSub_category_id());
            ps.setString(4, product.getSubsub_category_id());
            ps.setString(5, product.getWarehouseName().toString());
            ps.setString(6, product.getShelfLocation().toString());
            ps.setString(7, product.getManufacturer());
            ps.setInt(8, product.getAmountOnShelves());
            ps.setInt(9, product.getAmountOnStock());
            ps.setDouble(10, product.getPriceToConsumer());
            ps.setDouble(11, product.getPriceToSupply());
            ps.setDouble(12, product.getSupplierDiscount());
            ps.setInt(13, product.getMinAmountAlert());

            ps.setString(14, product.getCatalogNumber());

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated <= 0) {
                throw new RuntimeException("ProductDAO:UpdateProduct - no such product was found in db: " + product.getCatalogNumber());
            }

        } catch (SQLException e) {
            throw new RuntimeException("ProductDAO:UpdateProduct - " + e.getMessage());
        }
    }



    public List<productDTO> SelectAll() {
        String q = "SELECT catalog_number, name, mainCategory, subCategory, subSubCategory, " +
                "warehouse, location, manufacturer, amount_on_shelves, amount_on_stock, " +
                "price_to_consumer, price_to_supply, supplier_discount, minAmountAlert FROM Products";
        List<productDTO> res = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement qu = conn.prepareStatement(q);
             ResultSet rs = qu.executeQuery()) {

            while (rs.next()) {
                String catalogNumber = rs.getString("catalog_number");

                productDTO product = new productDTO(
                        rs.getString("name"),
                        catalogNumber,
                        rs.getString("mainCategory"),
                rs.getString("subCategory"),
                        rs.getString("subSubCategory"),
                        rs.getString("warehouse"),
                        rs.getString("location"),
                        rs.getString("manufacturer"),
                        rs.getInt("amount_on_shelves"),
                        rs.getInt("amount_on_stock"),
                        promotionDAO.selectByProduct(catalogNumber),
                        rs.getDouble("price_to_consumer"),
                        rs.getDouble("price_to_supply"),
                        rs.getDouble("supplier_discount"),
                        rs.getInt("minAmountAlert")
                );


                res.add(product);
            }

        } catch (SQLException e) {
            throw new RuntimeException("ProductDAO:SelectAll - " + e.getMessage());
        }
        return res;
    }


    public void Clean() {
        String q = "DELETE FROM Products";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement qu = conn.prepareStatement(q)) {

            promotionDAO.Clean();

            qu.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("ProductDAO:Clean - " + e.getMessage());
        }
    }




}