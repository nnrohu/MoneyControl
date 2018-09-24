package com.example.nnroh.moneycontrol.App;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.nnroh.moneycontrol.Adapter.DebtorRecyclerAdapter;
import com.example.nnroh.moneycontrol.Adapter.PersonRecyclerAdapter;
import com.example.nnroh.moneycontrol.Data.Debt;
import com.example.nnroh.moneycontrol.Data.Person;
import com.example.nnroh.moneycontrol.Data.PersonDebt;
import com.example.nnroh.moneycontrol.Data.local.DataManager;
import com.example.nnroh.moneycontrol.R;
import com.karan.churi.PermissionManager.PermissionManager;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String PERSON_NUMBER = "com.example.nnroh.moneycontrol.PERSON_NUMBER";

    private PersonRecyclerAdapter mPersonRecyclerAdapter;
    private DebtorRecyclerAdapter mDebtorRecyclerAdapterByMe;
    private List<Person> mPersonList;
    private List<PersonDebt> mDebtListMe, mDebtListToMe;
    private PermissionManager permission;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManagerForPerson;
    private DataManager dm;
    private GridLayoutManager mGridLayoutManagerForDebt;
    private DebtorRecyclerAdapter mDebtorRecyclerAdapterToMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        permission =new PermissionManager() {};
        permission.checkAndRequestPermissions(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddPersonActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        dm = new DataManager(this);
        initializeDisplayContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPersonList.clear();
        mPersonList.addAll(dm.getAllPersonWithDebts());
        mPersonRecyclerAdapter.notifyDataSetChanged();
        mDebtListMe.clear();
        mDebtListMe.addAll(dm.getAllPersonDebtsByType(Debt.DEBT_TYPE_IOWE));
        mDebtorRecyclerAdapterByMe.notifyDataSetChanged();
        mDebtListToMe.clear();
        mDebtListToMe.addAll(dm.getAllPersonDebtsByType(Debt.DEBT_TYPE_OWED));
        mDebtorRecyclerAdapterToMe.notifyDataSetChanged();
    }


    private void initializeDisplayContent() {
        mRecyclerView = (RecyclerView) findViewById(R.id.item_person_list);

        //person adapter
        mGridLayoutManagerForPerson = new GridLayoutManager(this, 4);
        mPersonList = dm.getAllPersonWithDebts();
        mPersonRecyclerAdapter = new PersonRecyclerAdapter(this, mPersonList);

        //debtor adapter
        mGridLayoutManagerForDebt = new GridLayoutManager(this, 2);
        // debtor type by me
        mDebtListMe = dm.getAllPersonDebtsByType(Debt.DEBT_TYPE_IOWE);
        mDebtorRecyclerAdapterByMe = new DebtorRecyclerAdapter(this, mDebtListMe);
        //debtor type to me
        mDebtListToMe = dm.getAllPersonDebtsByType(Debt.DEBT_TYPE_OWED);
        mDebtorRecyclerAdapterToMe = new DebtorRecyclerAdapter(this, mDebtListToMe);

        displayPerson();

    }


    private void displayPerson() {
        mRecyclerView.setLayoutManager(mGridLayoutManagerForPerson);
        mRecyclerView.setAdapter(mPersonRecyclerAdapter);

        selectNavigationMenuItem(R.id.nav_person);

    }

    private void displayDebtToMe(){
        mRecyclerView.setLayoutManager(mGridLayoutManagerForDebt);
        mRecyclerView.setAdapter(mDebtorRecyclerAdapterToMe);
        selectNavigationMenuItem(R.id.nav_owed_to_me);
    }

    private void displayDebtByMe(){
        mRecyclerView.setLayoutManager(mGridLayoutManagerForDebt);
        mRecyclerView.setAdapter(mDebtorRecyclerAdapterByMe);
        selectNavigationMenuItem(R.id.nav_owed_by_me);
    }

    private void selectNavigationMenuItem(int id) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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
            displayDebtToMe();
        } else if (id == R.id.nav_owed_by_me) {
            displayDebtByMe();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
