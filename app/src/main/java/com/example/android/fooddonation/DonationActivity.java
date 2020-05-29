package com.example.android.fooddonation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DonationActivity extends AppCompatActivity {

    private EditText editTextFood;
    private EditText editTextAmount, editTextCharges;
    private Spinner spinnerMeasurement;
    private Spinner spinnerDonation;
    private Spinner spinnerCooked;
    private CheckBox checkBoxPick;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("donations");
    private DatabaseReference dbuser= FirebaseDatabase.getInstance().getReference("users");
    private Donation donation;
    private String donationId;
    private String donationOwner;
    private String[] measurements, cooked;
    private FirebaseAuth firebaseAuth;
    private Button btnSubmit, btnProfile;
    TextView CHARGES;
    Query query;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);
        firebaseAuth = FirebaseAuth.getInstance();
        donationId = getIntent().getStringExtra(DonationAdapter.DONATION_ID);
        donationOwner = getIntent().getStringExtra(DonationAdapter.DONATION_OWNER);
        measurements = getResources().getStringArray(R.array.measurements);
        cooked = getResources().getStringArray(R.array.cooked);
        editTextFood = findViewById(R.id.editText_donation_food);
        editTextAmount = findViewById(R.id.editText_donation_amount);
        editTextCharges= findViewById(R.id.editText_charges);
        spinnerMeasurement = findViewById(R.id.spinner_donation_amount);
        spinnerDonation = findViewById(R.id.spinner_donation_donation);
        spinnerCooked = findViewById(R.id.spinner_donation_cooked);
        checkBoxPick = findViewById(R.id.checkBox_donation_pick);
        btnSubmit = findViewById(R.id.button_donation_submit);
        btnProfile= findViewById(R.id.button_profile);
        CHARGES = (TextView) findViewById(R.id.charges);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                boolean isDonation = spinnerDonation.getSelectedItem().toString().equals("Donation");

                String isCooked = spinnerCooked.getSelectedItem().toString();
                String food = editTextFood.getText().toString().trim();
                String amount = editTextAmount.getText().toString().trim();
                String charges = editTextCharges.getText().toString().trim();
                boolean isPick;
                if(!isDonation){
                    isPick= false;
                    checkBoxPick.setEnabled(false);
                    checkBoxPick.setChecked(false);
                    editTextCharges.setVisibility(View.INVISIBLE);
                    CHARGES.setVisibility(View.INVISIBLE);
                }
                else isPick = checkBoxPick.isChecked();
                String measure = spinnerMeasurement.getSelectedItem().toString();
                String userId;
                String display;

                if (TextUtils.isEmpty(food)) {
                    display = "Please fill in the food";
                } else if (TextUtils.isEmpty(amount)) {
                    display = "Please fill in the amount";
                } else {
                    if (donationId != null) {
                        userId = donation.getUserId();
                        display = "Successfully updated ";
                    } else {
                        userId = firebaseAuth.getUid();
                        display = "Successfully created ";
                        donationId = databaseReference.push().getKey();
                    }
                    Donation donation = new Donation(isDonation, isCooked, food, amount, userId, isPick, measure, charges);
                    databaseReference.child(donationId).setValue(donation);
                    display += isDonation ? "Donation" : "Donation request";
                }

                new CountDownTimer(43200000 , 1000) {
                    public void onTick(long millisUntilFinished) {
                    }
                    public void onFinish() {
                        databaseReference.child(donationId).removeValue();
                    }
                }.start();

                Toast.makeText(DonationActivity.this, display, Toast.LENGTH_SHORT).show();
            onBackPressed();}
            });
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                query= FirebaseDatabase.getInstance().getReference("users").orderByChild("userId").equalTo(donationOwner);
                query.addListenerForSingleValueEvent(valueEventListener);
            }
        });
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                String usermail =  dataSnapshot.child(donationOwner).child("mail").getValue(String.class);
                String location =  dataSnapshot.child(donationOwner).child("address").getValue(String.class);
                String phone =  dataSnapshot.child(donationOwner).child("contact").getValue(String.class);
                String name =  dataSnapshot.child(donationOwner).child("firstName").getValue(String.class);
                Intent intent = new Intent(DonationActivity.this, ProfileActivity.class);
                intent.putExtra("PHONE",phone);
                intent.putExtra("MAIL", usermail);
                intent.putExtra("LOC",location);
                intent.putExtra("NAME",name);
                startActivity(intent);
                }
            }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (donationId != null) {
            checkOwner();
            databaseReference.child(donationId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    donation = dataSnapshot.getValue(Donation.class);
                    editTextFood.setText(donation.getFood());
                    editTextAmount.setText(donation.getAmount());
                    editTextCharges.setText(donation.getCharges());
                    spinnerDonation.setSelection(donation.isDonation() ? 0 : 1);
                    checkBoxPick.setChecked(donation.isPick());
                    for (int i = 0; measurements.length > cooked.length ? i < measurements.length : i < cooked.length; i++) {
                        if (i < measurements.length) {
                            if (donation.getMeasure().equals(measurements[i]))
                                spinnerMeasurement.setSelection(i);
                        }
                        if (i < cooked.length) {
                            if (donation.getCooked().equals(cooked[i]))
                                spinnerCooked.setSelection(i);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    public boolean checkOwner() {
        boolean isOwner = donationOwner.equals(firebaseAuth.getUid());
        if (!isOwner) {
            System.out.println(donationOwner);
            editTextFood.setEnabled(false);
            editTextAmount.setEnabled(false);
            editTextCharges.setEnabled(false);
            spinnerMeasurement.setEnabled(false);
            spinnerDonation.setEnabled(false);
            spinnerCooked.setEnabled(false);
            checkBoxPick.setEnabled(false);
            btnSubmit.setEnabled(false);
            btnSubmit.setVisibility(View.INVISIBLE);
            btnProfile.setEnabled(true);
            btnProfile.setVisibility(View.VISIBLE);

        }
        return isOwner;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (donationId != null && checkOwner())
            getMenuInflater().inflate(R.menu.activity_donation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_donation_delete:
                delete();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DonationActivity.this);
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseReference.child(donationId).removeValue();
                finish();
            }
        });
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setMessage("Delete your request?");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}