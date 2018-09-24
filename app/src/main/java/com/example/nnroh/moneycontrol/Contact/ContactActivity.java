package com.example.nnroh.moneycontrol.Contact;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.nnroh.moneycontrol.App.AddPersonActivity;
import com.example.nnroh.moneycontrol.R;

import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private static final int REQUEST_PERMISSION = 2001;

    ArrayList<Contact> listContacts;
    ListView lvContacts;

    SearchView sv;
    private ContactsAdapter mAdapterContacts;
    private MenuItem mSearchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
                PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_CONTACTS
            }, REQUEST_PERMISSION);
        }

        listContacts = new ContactFetcher(this).fetchAll();
        lvContacts = (ListView) findViewById(R.id.lvContacts);
        mAdapterContacts = new ContactsAdapter(this, listContacts);
        lvContacts.setAdapter(mAdapterContacts);
        lvContacts.setTextFilterEnabled(false);

        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = (Contact) parent.getItemAtPosition(position);
                String personName = contact.name;
                String personPhoto = contact.photo;

                if (contact.numbers.size() > 0 && contact.numbers.get(0) != null) {
                    String personNumber = contact.numbers.get(0).number;

                    Intent intent = new Intent();
                    intent.putExtra(AddPersonActivity.PERSON_NAME, personName);
                    intent.putExtra(AddPersonActivity.PERSON_NUMBER, personNumber);
                    intent.putExtra(AddPersonActivity.PERSON_PHOTO, personPhoto);

                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact, menu);
        mSearchMenuItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);

        sv = (SearchView) mSearchMenuItem.getActionView();
        sv.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        sv.setSubmitButtonEnabled(true);
        sv.setOnQueryTextListener(this);
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length != 0) {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    finish();
                }
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!sv.isIconified()){
            sv.setIconified(true);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mAdapterContacts.getFilter().filter(newText);

        return false;
    }

}
