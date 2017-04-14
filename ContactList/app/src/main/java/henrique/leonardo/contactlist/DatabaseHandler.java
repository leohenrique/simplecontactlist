package henrique.leonardo.contactlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leonardo on 11/04/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String
            DATABASE_NAME = "contacts",
            TABLE_CONTACTS = "contacts",
            KEY_ID = "id",
            KEY_NAME = "name",
            KEY_PHONE = "phone",
            KEY_EMAIL = "email",
            KEY_ADDRESS = "address",
            KEY_IMAGE_URI = "imageUri";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TABLE_CONTACTS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                KEY_NAME + " TEXT, "+
                KEY_PHONE + " TEXT, "+
                KEY_EMAIL + " TEXT, "+
                KEY_ADDRESS + " TEXT, "+
                KEY_IMAGE_URI + " TEXT "+
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_CONTACTS);
        onCreate(db);
    }

    public void createContact(Contact pContact){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, pContact.getName());
        values.put(KEY_PHONE, pContact.getPhone());
        values.put(KEY_EMAIL, pContact.getEmail());
        values.put(KEY_ADDRESS, pContact.getAddress());
        values.put(KEY_IMAGE_URI, pContact.getImageUri().toString());

        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }

    public Contact getContact(int pId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTS, new String[] {KEY_ID, KEY_NAME, KEY_PHONE, KEY_EMAIL, KEY_ADDRESS, KEY_IMAGE_URI},
                KEY_ID + "=?", new String[] {String.valueOf(pId)}, null, null, null, null);

        if (cursor != null){
            cursor.moveToFirst();

            Contact contact = new Contact(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    Uri.parse(cursor.getString(5)));
            return contact;
        } else
            return null;
    }

    public void deleteContact(Contact pContact){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID +"=?", new String[] { String.valueOf(pContact.getId())} );
        db.close();
    }

    public int getContactCount(){
        /// Select * from Contacts
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_CONTACTS, null);
        int count = cursor.getCount();
        db.close();
        cursor.close();

        return count;
    }

    public int updateContact(Contact pContact){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, pContact.getName());
        values.put(KEY_PHONE, pContact.getPhone());
        values.put(KEY_ADDRESS, pContact.getAddress());
        values.put(KEY_EMAIL, pContact.getEmail());
        values.put(KEY_IMAGE_URI, pContact.getImageUri().toString());

        int aRowsAffected = db.update(TABLE_CONTACTS, values, KEY_ID + "=?", new String[]{String.valueOf(pContact.getId())});
        db.close();
        return aRowsAffected;
    }

    public List<Contact> getAllContacts(){
        List<Contact> contacts = new ArrayList<Contact>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_CONTACTS, null);

        if (cursor.moveToFirst()){
            do {
                contacts.add(new Contact(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2),  cursor.getString(3), cursor.getString(4), Uri.parse(cursor.getString(5))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contacts;
    }
}

