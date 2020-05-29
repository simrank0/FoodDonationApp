package com.example.android.fooddonation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class OfferActivity extends AppCompatActivity {
    Button btnSubmit, btnProfile;
    EditText editTextFood, editTextAmount, editTextCharges, editTextPick, editTextDate;
    private String offerId, offerOwner;
    private Offers offer;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("offers");
    Query query;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        btnSubmit = findViewById(R.id.button_donation_submit);
        btnProfile = findViewById(R.id.button_profile);

        editTextFood = findViewById(R.id.editText_donation_food);
        editTextAmount = findViewById(R.id.editText_donation_amount);
        editTextCharges= findViewById(R.id.editText_charges);
        editTextPick = findViewById(R.id.editText_time);
        editTextDate = findViewById(R.id.editText_exp);

        offerId = getIntent().getStringExtra(OfferAdapter.OFFER_ID);
        offerOwner = getIntent().getStringExtra(OfferAdapter.OFFER_OWNER);

        firebaseAuth = FirebaseAuth.getInstance();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String food = editTextFood.getText().toString().trim();
                String amount = editTextAmount.getText().toString().trim();
                String charges = editTextCharges.getText().toString().trim();
                String Pick = editTextPick.getText().toString().trim();
                String time = editTextDate.getText().toString().trim();
                String userId;
                String display;

                if (TextUtils.isEmpty(food)) {
                    display = "Please fill in the food";
                } else if (TextUtils.isEmpty(amount)) {
                    display = "Please fill in the amount";
                } else if (TextUtils.isEmpty(charges)) {
                    display = "Please fill in the Delivery Charges";
                } else if (TextUtils.isEmpty(Pick)) {
                    display = "Please fill in the Pick up timing you prefer";
                } else {
                    if (offerId != null) {
                        userId = offer.getUserId();
                        display = "Successfully updated ";
                    } else {
                        userId = firebaseAuth.getUid();
                        display = "Successfully created ";
                        offerId = databaseReference.push().getKey();
                    }
                    Offers offer = new Offers(food, amount, time, Pick,charges, userId);
                    databaseReference.child(offerId).setValue(offer);
                }

                Toast.makeText(OfferActivity.this, display, Toast.LENGTH_SHORT).show();
                onBackPressed();}
        });
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                query= FirebaseDatabase.getInstance().getReference("users").orderByChild("userId").equalTo(offerOwner);
                query.addListenerForSingleValueEvent(valueEventListener);
            }
        });
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                String usermail =  dataSnapshot.child(offerOwner).child("mail").getValue(String.class);
                String location =  dataSnapshot.child(offerOwner).child("address").getValue(String.class);
                String phone =  dataSnapshot.child(offerOwner).child("contact").getValue(String.class);
                String name =  dataSnapshot.child(offerOwner).child("firstName").getValue(String.class);
                Intent intent = new Intent(OfferActivity.this, ProfileActivity.class);
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
        if (offerId != null) {
            checkOwner();
            databaseReference.child(offerId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    offer = dataSnapshot.getValue(Offers.class);
                    editTextFood.setText(offer.getFood());
                    editTextAmount.setText(offer.getAmount());
                    editTextCharges.setText(offer.getCharges());
                    editTextDate.setText(offer.getExpDate());
                    editTextPick.setText(offer.getPick());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    public boolean checkOwner() {
        boolean isOwner = offerOwner.equals(firebaseAuth.getUid());
        if (!isOwner) {
            System.out.println(offerOwner);
            editTextFood.setEnabled(false);
            editTextAmount.setEnabled(false);
            editTextCharges.setEnabled(false);
            editTextDate.setEnabled(false);
            editTextPick.setEnabled(false);
            btnSubmit.setEnabled(false);
            btnSubmit.setVisibility(View.INVISIBLE);
            btnProfile.setEnabled(true);
            btnProfile.setVisibility(View.VISIBLE);
        }
        return isOwner;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (offerId != null && checkOwner())
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
        AlertDialog.Builder builder = new AlertDialog.Builder(OfferActivity.this);
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseReference.child(offerId).removeValue();
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