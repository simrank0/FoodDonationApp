package com.example.android.fooddonation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.fooddonation.R.id;
import static com.example.android.fooddonation.R.string.navigation_drawer_close;
import static com.example.android.fooddonation.R.string.navigation_drawer_open;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int NAV_MY_DONATIONS = id.nav_my_donations;
    public static final int NAV_REQUESTS = id.nav_requests;
    public static final int NAV_DONATIONS = id.nav_donations;
    public static final int NAV_LIMITED = id.nav_limited;
    public static final int NAV_SIGN_OUT = id.action_sign_out;
    public static final int NAV_RESTAURANT = id.nav_restaurant;
    public static int selectedNav;
    Query query;
    String name;
    private DrawerLayout drawer;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("donations");
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference("offers");

    private TextView user_name;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView textViewEmpty;
    private FirebaseAuth firebaseAuth;
    String currentuser;

    FloatingActionButton fab;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user_name = (TextView) findViewById(id.user_name);
        query= FirebaseDatabase.getInstance().getReference("users").orderByChild("userId").equalTo(currentuser);
        query.addListenerForSingleValueEvent(valueEventListener);
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(id.list_donations);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(MainActivity.this, new LinearLayoutManager(MainActivity.this).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getTitle()=="Restaurants"){
                    startActivityForResult(new Intent(MainActivity.this, RestaurantActivity.class), 0);
                }
                else if(getTitle()=="Limited Time Offers"){
                    startActivityForResult(new Intent(MainActivity.this, OfferActivity.class), 0);
                }
                else
                    startActivityForResult(new Intent(MainActivity.this, DonationActivity.class), 0);
            }
        });


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, navigation_drawer_open, navigation_drawer_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
                                                 @Override
                                                 public void onClick(View v) {
                                                     drawer.openDrawer(Gravity.LEFT);

                                                 }
        });
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(NAV_DONATIONS);

        selectedNav = NAV_DONATIONS;
        progressBar = findViewById(id.progressBar_list);
        textViewEmpty = findViewById(id.text_list_empty);


    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            name =  dataSnapshot.child(currentuser).child("firstName").getValue(String.class);
            user_name.setText(name);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        getData();
    }


    public void getData() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        Query finalReference = databaseReference.orderByChild("donation");
        switch (selectedNav) {
            case NAV_DONATIONS:
                finalReference = databaseReference.orderByChild("donation").equalTo(true);
                break;
            case NAV_REQUESTS:
                finalReference = databaseReference.orderByChild("donation").equalTo(false);
                break;
            case NAV_MY_DONATIONS:
                finalReference = databaseReference.orderByChild("userId").equalTo(firebaseAuth.getUid());
                break;
            case NAV_LIMITED:
                finalReference = db.orderByChild("amount");
                break;
            case NAV_RESTAURANT:
                finalReference = FirebaseDatabase.getInstance().getReference("restaurants");

        }

        finalReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Item> names = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(getTitle()=="Limited Time Offers"){
                        Offers off = snapshot.getValue(Offers.class);
                        Item item = new Item();
                        item.setFood(off.getFood());
                        item.setId(snapshot.getKey());
                        item.setUserId(off.getUserId());
                        names.add(item); }
                    else if(getTitle()=="Restaurants"){
                        Restaurant res = snapshot.getValue(Restaurant.class);
                        Item item = new Item();
                        item.setFood(res.getFood());
                        item.setId(snapshot.getKey());
                        item.setUserId(res.getUserId());
                        names.add(item);
                    }
                    else {
                        Donation donation = snapshot.getValue(Donation.class);
                        Item item = new Item();
                        item.setFood(donation.getFood());
                        item.setId(snapshot.getKey());
                        item.setUserId(donation.getUserId());
                        names.add(item);
                    }
                }
                progressBar.setVisibility(View.GONE);
                if(getTitle()!="Limited Time Offers"){
                recyclerView.setAdapter(new DonationAdapter(MainActivity.this, names));}
                else recyclerView.setAdapter(new OfferAdapter(MainActivity.this, names));
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                if (names.isEmpty()) {
                    textViewEmpty.setText("No items found");
                    textViewEmpty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    textViewEmpty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Database Error", databaseError.getMessage());

            }

        });
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, AccountActivity.class));
            return true;
        } else if (id== R.id.nav_help){
            startActivity(new Intent(this, HelpActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        selectedNav = id;
        switch (id) {
            case NAV_DONATIONS:
                setTitle("Food Donations");
                getData();
                break;
            case NAV_REQUESTS:
                setTitle("Donation Requests");
                getData();
                break;
            case NAV_MY_DONATIONS:
                setTitle("My Donations");
                getData();
                break;
            case NAV_LIMITED:
                setTitle("Limited Time Offers");
                getData();
                break;
            case NAV_RESTAURANT:
                setTitle("Restaurants");
                getData();
                break;
            case NAV_SIGN_OUT:
                firebaseAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}