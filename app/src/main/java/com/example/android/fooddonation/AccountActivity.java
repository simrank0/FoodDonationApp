package com.example.android.fooddonation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;

public class AccountActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
    private EditText editTextEmail, editTextFirstName, editTextLastName, editTextPhone;
    private Button buttonUpdate, buttonchangepassword;
    private TextView textViewLocation;
    private ImageView imageLocation;
    private String coordinates, address;
    Double Latitude, Longitude;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = databaseReference.child(firebaseAuth.getUid());
        editTextEmail = findViewById(R.id.editText_account_email);
        editTextFirstName = findViewById(R.id.editText_account_firstName);
        editTextLastName = findViewById(R.id.editText_account_lastName);
        editTextPhone = findViewById(R.id.editText_account_phone);
        buttonUpdate = findViewById(R.id.button_account_update);
        buttonchangepassword = findViewById(R.id.button_account_changePass);
        textViewLocation = findViewById(R.id.textView_account_location);
        imageLocation = findViewById(R.id.imageView_account_location);
        progressBar = findViewById(R.id.progressBar_account);
        progressBar.setVisibility(GONE);

        imageLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                intentBuilder.setLatLngBounds(new LatLngBounds(new LatLng(-6.369028, 34.888822), new LatLng(4.038296, 41.832181)));
                try {
                    startActivityForResult(intentBuilder.build(AccountActivity.this), 0);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        buttonchangepassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent( AccountActivity.this, ResetActivity.class), 0);
            }
        });
        buttonUpdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String firstNameText = editTextFirstName.getText().toString();
                final String lastName = editTextLastName.getText().toString();
                final String location = textViewLocation.getText().toString();
                final String phone = editTextPhone.getText().toString();
                String display = "";
                if (TextUtils.isEmpty(firstNameText)) {
                    display = "Please enter a valid first name";
                    editTextFirstName.requestFocus();
                } else if (TextUtils.isEmpty(lastName)) {
                    display = "Please enter a valid last name";
                    editTextLastName.requestFocus();
                } else if (TextUtils.isEmpty(location)) {
                    display = "Please select a valid location";
                    imageLocation.performClick();
                } else if (TextUtils.isEmpty(phone) || phone.length() != 10) {
                    display = "Please enter a valid phone number";
                    editTextPhone.requestFocus();
                } else {
                    gone(true);
                    firebaseAuth.getCurrentUser().updateEmail(editTextEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AccountActivity.this, "Successfully Updated", Toast.LENGTH_LONG).show();
                                databaseReference.child("lastName").setValue(lastName);
                                databaseReference.child("location").setValue(location);
                                databaseReference.child("coordinates").setValue(address);
                                databaseReference.child("firstName").setValue(firstNameText);
                                databaseReference.child("contact").setValue(phone);
                            } else {
                                Toast.makeText(AccountActivity.this, "There was an error while updating", Toast.LENGTH_LONG).show();

                            }
                            gone(false);
                        }
                    });
                }
                if (!display.equals(""))
                    Toast.makeText(AccountActivity.this, display, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                coordinates = place.getLatLng().latitude + ", " + place.getLatLng().longitude;
                Latitude = place.getLatLng().latitude;
                Longitude = place.getLatLng().longitude;
                address = getCompleteAddressString(Latitude,Longitude);
                textViewLocation.setText(address);
            }
        }
    }

    @SuppressLint("LongLogTag")
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    @Override
    protected void onStart() {
        super.onStart();
        editTextEmail.setText(firebaseAuth.getCurrentUser().getEmail());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                editTextFirstName.setText(user.getFirstName());
                editTextLastName.setText(user.getLastName());
                editTextPhone.setText(user.getContact());
                textViewLocation.setText(user.getLocation());
                coordinates = user.getCoordinates();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void gone(boolean b) {
        progressBar.setVisibility(b ? VISIBLE : GONE);
        editTextEmail.setEnabled(!b);
        buttonUpdate.setEnabled(!b);
        imageLocation.setEnabled(!b);
        buttonchangepassword.setEnabled(!b);
    }
}