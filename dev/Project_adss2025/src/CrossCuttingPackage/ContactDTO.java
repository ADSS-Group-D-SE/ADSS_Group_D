package CrossCuttingPackage;

public class ContactDTO {

    public String name;
    public String email;
    public String phoneNumber;

    /**
     * Class made for contact data transfer.
     * @param name
     * @param email
     * @param phoneNumber
     */
    public ContactDTO(String name,String email,String phoneNumber)
    {
        this.name=name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
