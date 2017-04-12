package henrique.leonardo.contactlist;

import android.net.Uri;

/**
 * Created by Leonardo on 06/04/2017.
 */

public class Contact {

    private String name;
    private String phone;
    private String email;
    private String address;
    private Uri imageUri;
    private int id;

    public Contact(int pId, String pName, String pPhone, String pEmail, String pAddress, Uri pImageURI) {
        super();
        id = pId;
        name = pName;
        phone = pPhone;
        email = pEmail;
        address = pAddress;
        imageUri = pImageURI;
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

    public Uri getImageUri() { return imageUri; }

    public int getId() { return id; }

}
