package SupplierModule.DomainLayer;

import CrossCuttingPackage.ContactDTO;
import SupplierModule.DataAccessLayer.ContactDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactInfo {
    private String name;
    private String phoneNumber;
    private String email;
    private static final ContactDAO dao = new ContactDAO();

    public ContactInfo(String name, String email, String phoneNumber) {
        setName(name);
        setEmail(email);
        setPhoneNumber(phoneNumber);
    }

    public ContactInfo(ContactDTO c)
    {
        this(c.name,c.email, c.phoneNumber);
    }

    public static List<ContactDTO> convert(HashMap<String,ContactInfo> map)
    {
        List<ContactDTO> res = new ArrayList<>();
        for(Map.Entry<String,ContactInfo> en: map.entrySet())
        {
            res.add(en.getValue().toDTO());
        }
        return res;
    }

    public static HashMap<String,ContactInfo> convert(List<ContactDTO> list)
    {
        HashMap<String,ContactInfo> res = new HashMap<>();
        for(ContactDTO c : list)
            res.put(c.name,new ContactInfo(c));
        return res;
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
        return "[Name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email +"]" + '\'';
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

