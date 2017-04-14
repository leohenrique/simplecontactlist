package henrique.leonardo.contactlist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int EDIT = 0;
    private static final int DELETE = 1;

    EditText nameText, phoneText, emailTxt, addressTxt;
    List<Contact> contacts = new ArrayList<Contact>();
    ListView contactListView;
    ImageView contactImageImgView;
    Uri imageURI = Uri.parse("android.resource://henrique.leonardo.contactlist/drawable/no_user_logo.png");
    DatabaseHandler dbHandler;
    int longClickedItemIndex;
    ArrayAdapter<Contact> contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        contactListView = (ListView) findViewById(R.id.listContacts);
        nameText = (EditText) findViewById(R.id.edtContactName);
        phoneText = (EditText) findViewById(R.id.edtPhoneNumer);
        emailTxt = (EditText) findViewById(R.id.edtEmail);
        addressTxt = (EditText) findViewById(R.id.edtAddress);
        contactImageImgView = (ImageView) findViewById(R.id.imgViewContactImage);
        dbHandler = new DatabaseHandler(getApplicationContext());

        registerForContextMenu(contactListView);

        contactListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longClickedItemIndex = position;
                return false;
            }
        });

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("creator");
        tabSpec.setContent(R.id.tabCreator);
        tabSpec.setIndicator("creator");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("List");
        tabSpec.setContent(R.id.tabList);
        tabSpec.setIndicator("List");
        tabHost.addTab(tabSpec);

        final Button addButton = (Button) findViewById(R.id.btnAddContact);
        addButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Contact contact = new Contact(dbHandler.getContactCount(), String.valueOf(nameText.getText()),
                        String.valueOf(phoneText.getText()), String.valueOf(emailTxt.getText()), String.valueOf(addressTxt.getText()),
                        imageURI);
                if (!contactExists(contact)) {
                    dbHandler.createContact(contact);
                    contacts.add(contact);
                    contactAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), contact.getName()+" has been created", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), contact.getName()+" already exists. Please user a different name.", Toast.LENGTH_SHORT).show();

            }

        });

        nameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                addButton.setEnabled(!String.valueOf(nameText.getText()).trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        contactImageImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Contact Image"), 1);

            }
        });

        if (dbHandler.getContactCount() != 0)
            contacts.addAll(dbHandler.getAllContacts());

        populateList();

    }

    public void onCreateContextMenu(ContextMenu pMenu, View pView, ContextMenu.ContextMenuInfo pMenuInfo){
        super.onCreateContextMenu(pMenu, pView, pMenuInfo);

        pMenu.setHeaderIcon(R.drawable.pencil_icon);
        pMenu.setHeaderTitle("Contact Options");
        pMenu.add(Menu.NONE, EDIT, Menu.NONE, "Edit");
        pMenu.add(Menu.NONE, DELETE, Menu.NONE, "Delete");
    }

    public boolean onContextItemSelected(MenuItem pItem){
        switch (pItem.getItemId()){
            case EDIT:
                /// TODO: Implement editing
                break;
            case DELETE:
                dbHandler.deleteContact(contacts.get(longClickedItemIndex));
                contacts.remove(longClickedItemIndex);
                contactAdapter.notifyDataSetChanged();
                break;
        }
        return super.onContextItemSelected(pItem);
    }

    private boolean contactExists(Contact pContact){
        String name = pContact.getName();
        int contactCount = contacts.size();

        for (int i=0; i<contactCount; i++){
            if (name.trim().compareToIgnoreCase(contacts.get(i).getName().trim()) == 0)
                return true;
        }
        return false;
    }


    public void onActivityResult(int pReqCode, int pResCode, Intent data){
        if (pResCode == RESULT_OK){
            if (pReqCode == 1) {
                imageURI = data.getData();
                contactImageImgView.setImageURI(data.getData());
            }
        }
    }

    private void populateList(){
        contactAdapter = new ContactListAdapter();
        contactListView.setAdapter(contactAdapter);
    }

    private class ContactListAdapter extends ArrayAdapter<Contact>{
        public ContactListAdapter(){
            super (MainActivity.this, R.layout.listview_item, contacts);
        }

        public View getView(int position, View view, ViewGroup parent){
            if (view == null){
                view = getLayoutInflater().inflate(R.layout.listview_item, parent, false);
            }

            Contact currentContact = contacts.get(position);
            TextView name = (TextView) view.findViewById(R.id.contactName);
            name.setText(currentContact.getName());
            TextView phone = (TextView) view.findViewById(R.id.contactPhone);
            phone.setText(currentContact.getPhone());
            TextView email = (TextView) view.findViewById(R.id.contactEmail);
            email.setText(currentContact.getEmail());
            TextView address = (TextView) view.findViewById(R.id.contactAddress);
            address.setText(currentContact.getAddress());
            ImageView ivContactImage = (ImageView) view.findViewById(R.id.ivContactImage);
            ivContactImage.setImageURI(currentContact.getImageUri());
            return view;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
