package DataAccessLayer;

import DomainLayer.ContactPerson;
import DomainLayer.InventoryItem;
import DomainLayer.InventoryManager;
import DomainLayer.OrderItem;
import DomainLayer.OrderManager;
import DomainLayer.PaymentTerms;
import DomainLayer.QuantityDiscount;
import DomainLayer.Supplier;
import DomainLayer.SupplierAgreement;
import DomainLayer.SupplierAgreement.SupplyMethod;
import DomainLayer.SupplierItem;
import DomainLayer.SupplierManager;
import DomainLayer.SupplierOrder;
import DomainLayer.SupplierOrder.OrderStatus;
import ServiceLayer.SupplierService;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SuperLeeDataStore {

    private LocalTableDatabase database;

    public SuperLeeDataStore(Path databaseDirectory) {
        this.database = new LocalTableDatabase(databaseDirectory);
    }

    public boolean hasData() {
        return database.hasData();
    }

    public void clear() {
        database.clear();
    }

    public void save(SupplierService supplierService, InventoryManager inventoryManager) {
        saveSuppliers(supplierService.getAllSuppliers());
        saveOrders(supplierService.getAllOrders());
        saveInventory(inventoryManager.getAllInventoryItems());
    }

    public void loadInto(SupplierManager supplierManager, OrderManager orderManager,
            InventoryManager inventoryManager) {
        supplierManager.clear();
        orderManager.clear();
        inventoryManager.clear();

        loadSuppliers(supplierManager);
        loadOrders(orderManager);
        loadInventory(inventoryManager);
    }

    private void saveSuppliers(List<Supplier> suppliers) {
        List<Map<String, String>> supplierRows = new ArrayList<>();
        List<Map<String, String>> contactRows = new ArrayList<>();
        List<Map<String, String>> agreementRows = new ArrayList<>();
        List<Map<String, String>> dayRows = new ArrayList<>();
        List<Map<String, String>> itemRows = new ArrayList<>();
        List<Map<String, String>> discountRows = new ArrayList<>();

        for (Supplier supplier : suppliers) {
            Map<String, String> supplierRow = row();
            supplierRow.put("supplierId", intValue(supplier.getSupplierId()));
            supplierRow.put("companyId", supplier.getCompanyId());
            supplierRow.put("name", supplier.getName());
            supplierRow.put("bankAccount", supplier.getBankAccount());
            PaymentTerms terms = supplier.getPaymentTerms();
            supplierRow.put("paymentMethod", terms != null ? terms.getPaymentMethod() : "");
            supplierRow.put("netDays", terms != null ? intValue(terms.getNetDays()) : "0");
            supplierRows.add(supplierRow);

            for (ContactPerson contact : supplier.getContactPersons()) {
                Map<String, String> contactRow = row();
                contactRow.put("supplierId", intValue(supplier.getSupplierId()));
                contactRow.put("name", contact.getName());
                contactRow.put("phone", contact.getPhoneNumber());
                contactRow.put("email", contact.getEmail());
                contactRows.add(contactRow);
            }

            SupplierAgreement agreement = supplier.getAgreement();
            if (agreement == null) {
                continue;
            }

            Map<String, String> agreementRow = row();
            agreementRow.put("supplierId", intValue(supplier.getSupplierId()));
            agreementRow.put("supplyMethod", agreement.getSupplyMethod().name());
            agreementRow.put("deliveryDays", intValue(agreement.getDeliveryDays()));
            agreementRow.put("frozen", booleanValue(agreement.isFrozen()));
            agreementRows.add(agreementRow);

            for (Integer day : agreement.getFixedSupplyDays()) {
                Map<String, String> dayRow = row();
                dayRow.put("supplierId", intValue(supplier.getSupplierId()));
                dayRow.put("day", intValue(day));
                dayRows.add(dayRow);
            }

            for (SupplierItem item : agreement.getItems()) {
                Map<String, String> itemRow = row();
                itemRow.put("supplierId", intValue(supplier.getSupplierId()));
                itemRow.put("catalogNumber", intValue(item.getCatalogNumber()));
                itemRow.put("internalItemId", intValue(item.getInternalItemId()));
                itemRow.put("description", item.getItemDescription());
                itemRow.put("price", doubleValue(item.getPrice()));
                itemRow.put("manufacturer", item.getManufacturer());
                itemRows.add(itemRow);

                for (QuantityDiscount discount : item.getQuantityDiscounts()) {
                    Map<String, String> discountRow = row();
                    discountRow.put("supplierId", intValue(supplier.getSupplierId()));
                    discountRow.put("catalogNumber", intValue(item.getCatalogNumber()));
                    discountRow.put("minQuantity", intValue(discount.getMinQuantity()));
                    discountRow.put("discountPercent", doubleValue(discount.getDiscountPercent()));
                    discountRows.add(discountRow);
                }
            }
        }

        database.writeTable("suppliers", columns("supplierId", "companyId", "name",
                "bankAccount", "paymentMethod", "netDays"), supplierRows);
        database.writeTable("contacts", columns("supplierId", "name", "phone", "email"), contactRows);
        database.writeTable("agreements", columns("supplierId", "supplyMethod",
                "deliveryDays", "frozen"), agreementRows);
        database.writeTable("delivery_days", columns("supplierId", "day"), dayRows);
        database.writeTable("agreement_items", columns("supplierId", "catalogNumber",
                "internalItemId", "description", "price", "manufacturer"), itemRows);
        database.writeTable("discount_rules", columns("supplierId", "catalogNumber",
                "minQuantity", "discountPercent"), discountRows);
    }

    private void saveOrders(List<SupplierOrder> orders) {
        List<Map<String, String>> orderRows = new ArrayList<>();
        List<Map<String, String>> itemRows = new ArrayList<>();

        for (SupplierOrder order : orders) {
            Map<String, String> orderRow = row();
            orderRow.put("orderId", intValue(order.getOrderId()));
            orderRow.put("supplierId", intValue(order.getSupplierId()));
            orderRow.put("orderDate", dateValue(order.getOrderDate()));
            orderRow.put("expectedDeliveryDate", dateValue(order.getExpectedDeliveryDate()));
            orderRow.put("status", order.getStatus().name());
            orderRow.put("urgent", booleanValue(order.isUrgent()));
            orderRows.add(orderRow);

            for (OrderItem item : order.getItems()) {
                Map<String, String> itemRow = row();
                itemRow.put("orderId", intValue(order.getOrderId()));
                itemRow.put("catalogNumber", intValue(item.getCatalogNumber()));
                itemRow.put("internalItemId", intValue(item.getInternalItemId()));
                itemRow.put("description", item.getItemDescription());
                itemRow.put("quantity", intValue(item.getQuantity()));
                itemRow.put("unitPrice", doubleValue(item.getUnitPrice()));
                itemRow.put("discountPercent", doubleValue(item.getDiscountPercent()));
                itemRows.add(itemRow);
            }
        }

        database.writeTable("orders", columns("orderId", "supplierId", "orderDate",
                "expectedDeliveryDate", "status", "urgent"), orderRows);
        database.writeTable("order_items", columns("orderId", "catalogNumber", "internalItemId",
                "description", "quantity", "unitPrice", "discountPercent"), itemRows);
    }

    private void saveInventory(List<InventoryItem> inventoryItems) {
        List<Map<String, String>> itemRows = new ArrayList<>();
        for (InventoryItem item : inventoryItems) {
            Map<String, String> itemRow = row();
            itemRow.put("internalItemId", intValue(item.getInternalItemId()));
            itemRow.put("name", item.getName());
            itemRow.put("warehouseQuantity", intValue(item.getWarehouseQuantity()));
            itemRow.put("shelfQuantity", intValue(item.getShelfQuantity()));
            itemRow.put("minimumQuantity", intValue(item.getMinimumQuantity()));
            itemRow.put("expectedIncomingQuantity", intValue(item.getExpectedIncomingQuantity()));
            itemRows.add(itemRow);
        }
        database.writeTable("inventory_items", columns("internalItemId", "name",
                "warehouseQuantity", "shelfQuantity", "minimumQuantity",
                "expectedIncomingQuantity"), itemRows);
    }

    private void loadSuppliers(SupplierManager supplierManager) {
        Map<Integer, SupplierAgreement> agreementsBySupplier = new HashMap<>();
        Map<String, SupplierItem> itemsBySupplierAndCatalog = new HashMap<>();

        for (Map<String, String> row : database.readTable("suppliers")) {
            Supplier supplier = new Supplier(
                    parseInt(row.get("supplierId")),
                    row.get("companyId"),
                    row.get("name"),
                    row.get("bankAccount"),
                    new PaymentTerms(row.get("paymentMethod"), parseInt(row.get("netDays")))
            );
            supplierManager.registerSupplier(supplier);
        }

        for (Map<String, String> row : database.readTable("contacts")) {
            Supplier supplier = supplierManager.getSupplier(parseInt(row.get("supplierId")));
            if (supplier != null) {
                supplier.addContactPerson(new ContactPerson(row.get("name"), row.get("phone"), row.get("email")));
            }
        }

        for (Map<String, String> row : database.readTable("agreements")) {
            int supplierId = parseInt(row.get("supplierId"));
            Supplier supplier = supplierManager.getSupplier(supplierId);
            if (supplier == null) {
                continue;
            }
            SupplierAgreement agreement = new SupplierAgreement(SupplyMethod.valueOf(row.get("supplyMethod")));
            if (agreement.getSupplyMethod() == SupplyMethod.ON_ORDER) {
                agreement.setDeliveryDays(parseInt(row.get("deliveryDays")));
            }
            supplier.setAgreement(agreement);
            agreementsBySupplier.put(supplierId, agreement);
        }

        for (Map<String, String> row : database.readTable("delivery_days")) {
            SupplierAgreement agreement = agreementsBySupplier.get(parseInt(row.get("supplierId")));
            if (agreement != null) {
                agreement.addFixedSupplyDay(parseInt(row.get("day")));
            }
        }

        for (Map<String, String> row : database.readTable("agreement_items")) {
            int supplierId = parseInt(row.get("supplierId"));
            SupplierAgreement agreement = agreementsBySupplier.get(supplierId);
            if (agreement == null) {
                continue;
            }
            SupplierItem item = new SupplierItem(
                    parseInt(row.get("catalogNumber")),
                    parseInt(row.get("internalItemId")),
                    row.get("description"),
                    parseDouble(row.get("price")),
                    row.get("manufacturer")
            );
            agreement.addItem(item);
            itemsBySupplierAndCatalog.put(supplierCatalogKey(supplierId, item.getCatalogNumber()), item);
        }

        for (Map<String, String> row : database.readTable("discount_rules")) {
            int supplierId = parseInt(row.get("supplierId"));
            int catalogNumber = parseInt(row.get("catalogNumber"));
            SupplierItem item = itemsBySupplierAndCatalog.get(supplierCatalogKey(supplierId, catalogNumber));
            if (item != null) {
                item.addQuantityDiscount(new QuantityDiscount(
                        parseInt(row.get("minQuantity")),
                        parseDouble(row.get("discountPercent"))));
            }
        }

        for (Map<String, String> row : database.readTable("agreements")) {
            if (parseBoolean(row.get("frozen"))) {
                SupplierAgreement agreement = agreementsBySupplier.get(parseInt(row.get("supplierId")));
                if (agreement != null) {
                    agreement.freeze();
                }
            }
        }
    }

    private void loadOrders(OrderManager orderManager) {
        Map<Integer, List<OrderItem>> itemsByOrder = new HashMap<>();
        for (Map<String, String> row : database.readTable("order_items")) {
            int orderId = parseInt(row.get("orderId"));
            OrderItem item = new OrderItem(
                    parseInt(row.get("catalogNumber")),
                    parseInt(row.get("internalItemId")),
                    row.get("description"),
                    parseInt(row.get("quantity")),
                    parseDouble(row.get("unitPrice")),
                    parseDouble(row.get("discountPercent"))
            );
            itemsByOrder.computeIfAbsent(orderId, key -> new ArrayList<>()).add(item);
        }

        for (Map<String, String> row : database.readTable("orders")) {
            int orderId = parseInt(row.get("orderId"));
            SupplierOrder order = new SupplierOrder(
                    orderId,
                    parseInt(row.get("supplierId")),
                    parseBoolean(row.get("urgent"))
            );
            order.setOrderDate(parseDate(row.get("orderDate")));
            for (OrderItem item : itemsByOrder.getOrDefault(orderId, new ArrayList<>())) {
                order.addItem(item);
            }
            order.setExpectedDeliveryDate(parseOptionalDate(row.get("expectedDeliveryDate")));
            order.setStatus(OrderStatus.valueOf(row.get("status")));
            orderManager.registerOrder(order);
        }
    }

    private void loadInventory(InventoryManager inventoryManager) {
        for (Map<String, String> row : database.readTable("inventory_items")) {
            InventoryItem item = new InventoryItem(
                    parseInt(row.get("internalItemId")),
                    row.get("name"),
                    parseInt(row.get("warehouseQuantity")),
                    parseInt(row.get("shelfQuantity")),
                    parseInt(row.get("minimumQuantity"))
            );
            item.setExpectedIncomingQuantity(parseInt(row.get("expectedIncomingQuantity")));
            inventoryManager.registerInventoryItem(item);
        }
    }

    private String supplierCatalogKey(int supplierId, int catalogNumber) {
        return supplierId + ":" + catalogNumber;
    }

    private Map<String, String> row() {
        return new LinkedHashMap<>();
    }

    private List<String> columns(String... names) {
        return Arrays.asList(names);
    }

    private String intValue(int value) {
        return Integer.toString(value);
    }

    private String doubleValue(double value) {
        return Double.toString(value);
    }

    private String booleanValue(boolean value) {
        return Boolean.toString(value);
    }

    private String dateValue(LocalDate date) {
        return date == null ? "" : date.toString();
    }

    private int parseInt(String value) {
        return Integer.parseInt(value);
    }

    private double parseDouble(String value) {
        return Double.parseDouble(value);
    }

    private boolean parseBoolean(String value) {
        return Boolean.parseBoolean(value);
    }

    private LocalDate parseDate(String value) {
        return LocalDate.parse(value);
    }

    private LocalDate parseOptionalDate(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return LocalDate.parse(value);
    }
}
