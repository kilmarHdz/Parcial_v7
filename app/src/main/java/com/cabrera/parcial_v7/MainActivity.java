package com.cabrera.parcial_v7;

import android.Manifest;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    public static ArrayList<Contacto> contactos = new ArrayList<>();
    private transient ViewPager_Adapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = this.getIntent();

        if(savedInstanceState!=null){
            contactos = savedInstanceState.getParcelableArrayList("CONTACT_ADAPTER");
        }
        if (contactos.size() == 0) {
            showContactos();
        }
        if(intent !=null){
            Contacto C = intent.getParcelableExtra("ADDED");
            if(C!=null){
                contactos.add(0,C);
                getIntent().removeExtra("ADDED");
            }
        }

        FragmentManager valueof = getSupportFragmentManager();
        if(viewPagerAdapter==null)
            viewPagerAdapter = new ViewPager_Adapter(valueof);
        ViewPager viewPager = findViewById(R.id.Pager);
        viewPager.setAdapter(viewPagerAdapter);


        viewPagerAdapter.addFragment(Recycler_Contactos.newInstance( 0,contactos), getString(R.string.normal));
        viewPagerAdapter.addFragment(Recycler_Contactos.newInstance(1,contactos), getString(R.string.favoritos));


        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        handleIntent(getIntent());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContactos();
            } else {
                Toast.makeText(this, R.string.GrantPhone, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Pattern p = Pattern.compile(query);
            Matcher m;
            for (Contacto c : contactos) {
                m = p.matcher(c.getName());
                if (m.find()) {
                    c.setSearched(true);
                }
                else
                    c.setSearched(false);

            }
            viewPagerAdapter.notifyDataSetChanged();
        }
    }



    private void showContactos() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            addContacts();
        }
    }

    public void addContacts() {
        try {
            ContentResolver cr = getContentResolver();
            Cursor general = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            ArrayList<String> Numeros;
            while (general.moveToNext()) {
                String id = general.getString(general.getColumnIndex(ContactsContract.Contacts._ID));
                Numeros=new ArrayList<>();
                String name = general.getString(general.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = "";
                String type = ReturnString(general.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                String profile = general.getString(general.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                String thumbnailUri = general.getString(general.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                String email = "";
                profile = thumbnailUri != null ? thumbnailUri : profile;

                if (Integer.parseInt(general.getString(general.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        Numeros.add(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    }
                    pCur.close();
                }

                Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                while (emailCur.moveToNext()) {
                    email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                }
                emailCur.close();



                contactos.add(new Contacto(Long.parseLong(id),name,Numeros,profile,email,false,type));
            }
            general.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String ReturnString(int type) {
        switch (type) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                return getString(R.string.HomeReturn);
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                return getString(R.string.MobileReturn);
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                return getString(R.string.WorkReturn);
            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                return getString(R.string.OtherReturn);
            default:
                return getString(R.string.CustomReturn);
        }
    }
}
