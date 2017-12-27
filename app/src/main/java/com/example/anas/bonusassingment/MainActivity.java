package com.example.anas.bonusassingment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.anas.bonusassingment.Service.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ArrayList<Phone> list;
    ArrayList<Phone> contactList = new ArrayList<>();
    private static final String TAG = "MTAG";
    List<Phone> existingContacts = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // In an actual app, you'd want to request a permission when the user performs an action
        // that requires that permission.
        getPermissionToReadUserContacts();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        Phone temp = new Phone();
                        temp.setId(Integer.parseInt(id));
                        temp.setName(name);
                        temp.setphoneNo(phoneNo);
                        contactList.add(temp.getId()-1,temp);

                        Log.d(TAG, "ID: " + id);
                        Log.d(TAG, "Name: " + name);
                        Log.d(TAG, "Phone Number: " + phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
        PhoneDetail service = ServiceGenerator.createService(PhoneDetail.class);
        Call<List<Phone>> PhoneList;
        PhoneList = service.getPhoneList();
        PhoneList.enqueue(new Callback<List<Phone>>() {
            @Override
            public void onResponse(Call<List<Phone>> call, Response<List<Phone>> response) {
                Log.d(TAG, "onResponse: " + response.body());
                list = (ArrayList<Phone>) response.body();
                if (list != null) {
                    ArrayList<Phone> listOne = new ArrayList<Phone>();
                    ArrayList<Phone> listTwo = new ArrayList<Phone>();
                    listOne.addAll(contactList);
                    listTwo.addAll(list);
                    listOne.removeIf(listTwo::contains);
                    for (int i = 0; i<listOne.size() ; i++) {
                        Phone phon = listOne.get(i);
                        String phoneNo = phon.getphoneNo();
                        String name = phon.getName();
                        Call<Phone> save = service.savePhone(phoneNo,name);
                        save.enqueue(new Callback<Phone>() {
                            @Override
                            public void onResponse(Call<Phone> call, Response<Phone> response) {
                                Log.d(TAG, "onResponse nested: " + response.body());
                            }

                            @Override
                            public void onFailure(Call<Phone> call, Throwable t) {
                                Log.d(TAG, "onFailure nested: " +t);
                            }
                        });
                    }

                    Log.d(TAG, "the new list " + listOne);
                }
            }

            @Override
            public void onFailure(Call<List<Phone>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t);
            }
        });

    }

    // Identifier for the permission request
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;

    // Called when the user is performing an action which requires the app to read the
    // user's contacts
    public void getPermissionToReadUserContacts() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_CONTACTS)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS_PERMISSIONS_REQUEST);
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}

