package com.example.nnroh.moneycontrol.App;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.nnroh.moneycontrol.Adapter.DebtorRecyclerAdapter;
import com.example.nnroh.moneycontrol.Adapter.PersonRecyclerAdapter;
import com.example.nnroh.moneycontrol.Data.Debt;
import com.example.nnroh.moneycontrol.Data.PersonDebt;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract.DebtsEntry;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract.PersonsEntry;
import com.example.nnroh.moneycontrol.R;
import com.karan.churi.PermissionManager.PermissionManager;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    public static final String PERSON_NUMBER = "com.example.nnroh.moneycontrol.PERSON_NUMBER";
    public static final String PERSON_NAME = "com.example.nnroh.moneycontrol.PERSON_NAME";
    public static final String PERSON_IMAGE= "com.example.nnroh.moneycontrol.PERSON_IMAGE";
    public static final int LOADER_PERSON = 0;
    public static final int LOADER_DEBT_TO_ME = 1;
    public static final int LOADER_DEBT_BY_ME = 2;

    private PersonRecyclerAdapter mPersonRecyclerAdapter;
    private DebtorRecyclerAdapter mDebtorRecyclerAdapterByMe;
    private List<PersonDebt> mDebtListMe, mDebtListToMe;
    private PermissionManager permission;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManagerForPerson;
    private LinearLayoutManager mLayoutManagerForDebt;
    private DebtorRecyclerAdapter mDebtorRecyclerAdapterToMe;
    private TextView mTotalAmountToMe;
    private TextView mTotalAmountByMe;
    private ProgressDialog mProgressDialog;
    private boolean mQueryLoadForPersonFinished;


    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_DEBT_BY_ME, null, this);
        getLoaderManager().restartLoader(LOADER_DEBT_TO_ME, null, this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(true);

        permission = new PermissionManager(){};
        permission.checkAndRequestPermissions(this);

        FloatingActionButton fab =  findViewById(R.id.fab_add_person);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddPersonActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        mTotalAmountToMe = (TextView) navigationView.getMenu().findItem(R.id.nav_owed_to_me).getActionView();
        mTotalAmountByMe = (TextView) navigationView.getMenu().findItem(R.id.nav_owed_by_me).getActionView();

        navigationView.setNavigationItemSelectedListener(this);
        initializeDisplayContent();
        getLoaderManager().initLoader(LOADER_PERSON, null, this);

    }


    private void initializeDisplayContent() {
        mRecyclerView =  findViewById(R.id.item_person_list);
        //person adapter
        mGridLayoutManagerForPerson = new GridLayoutManager(this, 4);


        mPersonRecyclerAdapter = new PersonRecyclerAdapter(this, null);

        //debtor adapter
        mLayoutManagerForDebt = new LinearLayoutManager(this);
        // debtor type by me
        mDebtorRecyclerAdapterByMe = new DebtorRecyclerAdapter(this, null);
        //debtor type to me
        mDebtorRecyclerAdapterToMe = new DebtorRecyclerAdapter(this, null);

        displayPerson();

    }




    private void displayPerson() {
        mRecyclerView.setLayoutManager(mGridLayoutManagerForPerson);
        mRecyclerView.setAdapter(mPersonRecyclerAdapter);

        selectNavigationMenuItem(R.id.nav_person);

    }

    private void displayDebtToMe() {
        mRecyclerView.setLayoutManager(mLayoutManagerForDebt);
        mRecyclerView.setAdapter(mDebtorRecyclerAdapterToMe);
        selectNavigationMenuItem(R.id.nav_owed_to_me);
    }

    private void displayDebtByMe() {
        mRecyclerView.setLayoutManager(mLayoutManagerForDebt);
        mRecyclerView.setAdapter(mDebtorRecyclerAdapterByMe);
        selectNavigationMenuItem(R.id.nav_owed_by_me);
    }


    private void selectNavigationMenuItem(int id) {
        NavigationView navigationView =  findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.findItem(id).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_person) {
            displayPerson();
        } else if (id == R.id.nav_owed_to_me) {
            if (mQueryLoadForPersonFinished)
            displayDebtToMe();
        } else if (id == R.id.nav_owed_by_me) {
            if (mQueryLoadForPersonFinished)
            displayDebtByMe();
        } else if (id == R.id.nav_share) {
            shareApp();
        }

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my app at: https://drive.google.com/open?id=1X3j_B3scMYbmSwAhDQpeIS4zlTBgd9r8");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        mProgressDialog.show();
        CursorLoader loader = null;
        if(id == LOADER_PERSON) {
            loader = createLoaderPerson();
        }
        else if (id == LOADER_DEBT_TO_ME) {
            loader = createLoaderDebtToMe();
        }
        else if (id == LOADER_DEBT_BY_ME) {
            loader = createLoaderDebtByMe();
        }
        return loader;
    }

    private CursorLoader createLoaderDebtByMe() {
        Uri uri = DebtsEntry.CONTENT_URI_JOIN;

        String selection = DebtsEntry.COLUMN_TYPE + " = ? ";

        String[] selectionArgs = {String.valueOf(Debt.DEBT_TYPE_IOWE)};

        String[] projection = {
                PersonsEntry.getQName(PersonsEntry.COLUMN_IMAGE_URI),
                PersonsEntry.getQName(PersonsEntry.COLUMN_NAME),
                PersonsEntry.getQName(PersonsEntry.COLUMN_PHONE_NO),
                DebtsEntry.getQName(DebtsEntry.COLUMN_DATE_DUE),
                DebtsEntry.getQName(DebtsEntry.COLUMN_AMOUNT),
                DebtsEntry.getQName(DebtsEntry.COLUMN_DATE_ENTERED),
                DebtsEntry.getQName(DebtsEntry.COLUMN_ENTRY_ID) ,
                DebtsEntry.getQName(DebtsEntry.COLUMN_STATUS),
                DebtsEntry.getQName(DebtsEntry.COLUMN_NOTE)
        };

        return new CursorLoader(this, uri, projection, selection, selectionArgs, null);
    }

    @NonNull
    private CursorLoader createLoaderDebtToMe() {
        Uri uri = DebtsEntry.CONTENT_URI_JOIN;

        String selection = DebtsEntry.COLUMN_TYPE + " = ? ";

        String[] selectionArgs = {String.valueOf(Debt.DEBT_TYPE_OWED)};

        String[] projection = {
                PersonsEntry.getQName(PersonsEntry.COLUMN_IMAGE_URI),
                PersonsEntry.getQName(PersonsEntry.COLUMN_NAME),
                PersonsEntry.getQName(PersonsEntry.COLUMN_PHONE_NO),
                DebtsEntry.getQName(DebtsEntry.COLUMN_DATE_DUE),
                DebtsEntry.getQName(DebtsEntry.COLUMN_AMOUNT),
                DebtsEntry.getQName(DebtsEntry.COLUMN_DATE_ENTERED),
                DebtsEntry.getQName(DebtsEntry.COLUMN_ENTRY_ID) ,
                DebtsEntry.getQName(DebtsEntry.COLUMN_STATUS),
                DebtsEntry.getQName(DebtsEntry.COLUMN_NOTE)
        };

        return new CursorLoader(this, uri, projection, selection, selectionArgs, null);
    }


    @NonNull
    private CursorLoader createLoaderPerson() {
        mQueryLoadForPersonFinished = false;
        Uri uri = PersonsEntry.CONTENT_URI;
        String[] personColumns = {
                PersonsEntry.COLUMN_IMAGE_URI,
                PersonsEntry.COLUMN_NAME,
                PersonsEntry.COLUMN_PHONE_NO};
        return new CursorLoader(this, uri, personColumns, null, null, null);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mProgressDialog.dismiss();
        mQueryLoadForPersonFinished = true;
        if (loader.getId() == LOADER_PERSON) {
            mPersonRecyclerAdapter.changeCursour(data);
        }
        else if (loader.getId() == LOADER_DEBT_TO_ME) {
            mDebtorRecyclerAdapterToMe.changeCursour(data);
            loadTotalAmountToMe(data);
        }
        else if (loader.getId() == LOADER_DEBT_BY_ME) {
            mDebtorRecyclerAdapterByMe.changeCursour(data);
            loadTotalAmountByMe(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (loader.getId() == LOADER_PERSON) {
            mPersonRecyclerAdapter.changeCursour(null);
        }
        else if (loader.getId() == LOADER_DEBT_TO_ME) {
            mDebtorRecyclerAdapterToMe.changeCursour(null);
        }
        else if (loader.getId() == LOADER_DEBT_BY_ME) {
            mDebtorRecyclerAdapterByMe.changeCursour(null);
        }

    }

    private void loadTotalAmountToMe(Cursor data) {
        double totalAmt = 0;
        int amountPos = data.getColumnIndex(DebtsEntry.COLUMN_AMOUNT);
        int count = data.getCount();
        for (int i = 0; i < count; i++){
            data.moveToPosition(i);
            totalAmt += Double.parseDouble(data.getString(amountPos));
        }
        mTotalAmountToMe.setText(String.valueOf(totalAmt));
    }

    private void loadTotalAmountByMe(Cursor data) {
        double totalAmt = 0;
        int amountPos = data.getColumnIndex(DebtsEntry.COLUMN_AMOUNT);
        int count = data.getCount();
        for (int i = 0; i < count; i++){
            data.moveToPosition(i);
            totalAmt += Double.parseDouble(data.getString(amountPos));
        }
        mTotalAmountByMe.setText(String.valueOf(totalAmt));
    }
}
