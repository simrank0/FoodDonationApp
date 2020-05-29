package com.example.android.fooddonation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class RestaurantActivity extends AppCompatActivity {
    private static final int res=0;

    ImageView img;
    Button sub, profile;
    EditText name;
    private Uri file;
    private FirebaseStorage storage;
    private StorageReference reference;
    private String userId, userOwner;
    Restaurant restaurant;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("restaurants");
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        img = (ImageView)findViewById(R.id.menu_img);
        sub = (Button)findViewById(R.id.add);
        profile = (Button)findViewById(R.id.profile);
        name = (EditText) findViewById(R.id.name);

        firebaseAuth = FirebaseAuth.getInstance();

        userId = getIntent().getStringExtra(RestaurantAdapter.USER_ID);
        userOwner = getIntent().getStringExtra(RestaurantAdapter.USER_OWNER);

        storage = FirebaseStorage.getInstance();
        reference= storage.getReference();

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent.createChooser(intent,"Select Image"),1);
            }
        });

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name==null){
                    Toast.makeText(RestaurantActivity.this, "Enter Name", Toast.LENGTH_SHORT).show();
                }else{
                    String n = name.getText().toString().trim();
                    String u = firebaseAuth.getUid();
                    userId = databaseReference.push().getKey();
                    upload();
                    Restaurant restaurant = new Restaurant(n, u);
                    databaseReference.child(userId).setValue(restaurant);}
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                query= FirebaseDatabase.getInstance().getReference("users").orderByChild("userId").equalTo(userOwner);
                query.addListenerForSingleValueEvent(valueEventListener);
            }
        });

    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                String usermail =  dataSnapshot.child(userOwner).child("mail").getValue(String.class);
                String location =  dataSnapshot.child(userOwner).child("address").getValue(String.class);
                String phone =  dataSnapshot.child(userOwner).child("contact").getValue(String.class);
                String name =  dataSnapshot.child(userOwner).child("firstName").getValue(String.class);
                Intent intent = new Intent(RestaurantActivity.this, ProfileActivity.class);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            file = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),file);
                img.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void upload(){
        if(file!=null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference reff = reference.child("images/" + UUID.randomUUID().toString());
            reff.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(RestaurantActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploading" + (int)progress+"%");
                }
            });
        }
    }
}
