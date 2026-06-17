package SupplierModule.DomainLayer;

import CrossCuttingPackage.ContactDTO;

public class ContactInfo {
    private String name;
    private String phoneNumber;
    private String email;

    public ContactInfo(String name, String phoneNumber, String email) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Contact person name cannot be null or empty.");
        }
        this.setName(name);
        this.setPhoneNumber(phoneNumber);
        this.setEmail(email);
    }

    public ContactInfo(ContactDTO c)
    {
        setName(c.name);
        setEmail(c.email);
        setPhoneNumber(c.phoneNumber);
    }

    public ContactDTO toDTO() {return new ContactDTO(this.name,this.email,this.phoneNumber);}

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
        if (!isValidName(name))
            throw new IllegalArgumentException("Contact person name cannot be null or empty.");

        this.name = name;
    }


    public void setPhoneNumber(String phoneNumber) {
        if(!isValidPhoneNumber(phoneNumber))
            throw new IllegalArgumentException("Phone number:" + phoneNumber + " is null or not in the correct format.");

        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        if(!isValidEmail(email))
            throw new IllegalArgumentException("Email address:" + email + " is null or not in the correct format.");

        this.email = email;
    }

    @Override
    public String toString() {
        return "Name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'';
    }
    /**
     Helper methods to verify
     */
    private static boolean isValidEmail(String email) {
        if (email == null)
            return false;

        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private static boolean isValidPhoneNumber(String phone) {
        if (phone == null)
            return false;

        return phone.matches("^\\+?[0-9]{7,15}$");
    }

    private static boolean isValidName(String name)
    {
        if (name == null || name.isEmpty())
            return false;

        return true;
    }
}

