package DomainLayer;

public class ContactPerson {

    private String name;
    private String phoneNumber;
    private String email;

    public ContactPerson(String name, String phoneNumber, String email) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Contact person name cannot be null or empty.");
        }
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Contact person name cannot be null or empty.");
        }
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "ContactPerson{" +
                "name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
