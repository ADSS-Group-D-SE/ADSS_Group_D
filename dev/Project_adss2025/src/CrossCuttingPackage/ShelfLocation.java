package CrossCuttingPackage;

public class ShelfLocation {
    private String aisle;
    private int position;

    public ShelfLocation(String aisle, int position) {
        this.aisle = aisle;
        this.position = position;
    }

    public ShelfLocation(String locationStr) {
        if (locationStr == null || locationStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty.");
        }

        String normalized = locationStr.trim().toUpperCase();
        String locationPattern = "^[A-Z]-[0-9]+$";

        if (!normalized.matches(locationPattern)) {
            throw new IllegalArgumentException("Invalid location format. Expected format: Letter-Number (e.g., A-12).");
        }

        String[] parts = normalized.split("-");
        this.aisle = parts[0];
        this.position = Integer.parseInt(parts[1]);
    }

    public String getAisle() {
        return aisle;
    }

    public void setAisle(String aisle) {
        this.aisle = aisle;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return aisle + "-" + position;
    }
}
