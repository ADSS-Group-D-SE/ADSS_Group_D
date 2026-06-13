package InventoryModule.DomainLayer;

public class Warehouse {
    private String name;

    public Warehouse(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse name cannot be empty.");
        }
        this.name = name.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse name cannot be empty.");
        }
        this.name = name.trim();
    }

    @Override
    public String toString() {
        return name;
    }
}
