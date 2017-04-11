package henrique.leonardo.contactlist;

/**
 * Created by Leonardo on 06/04/2017.
 */

public class Contact {

    private String name;
    private String phone;
    private String email;
    private String address;

    public Contact(String pName, String pPhone, String pEmail, String pAddress) {
        super();
        name = pName;
        phone = pPhone;
        email = pEmail;
        address = pAddress;
    }


    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }
}
