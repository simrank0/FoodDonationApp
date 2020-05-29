package com.example.android.fooddonation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    ImageView img_mail, img_loc;
    Button img_phone;
    public static String ph,str,loc, name;
    TextView number, email, location, fname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Profile");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        number = (TextView) findViewById(R.id.number);
        email = (TextView) findViewById(R.id.email);
        location = (TextView) findViewById(R.id.location);
        fname = (TextView) findViewById(R.id.fname);
        Intent intent = getIntent();
        ph =  intent.getStringExtra("PHONE");
        str = intent.getStringExtra("MAIL");
        loc = intent.getStringExtra("LOC");
        name = intent.getStringExtra("NAME");


        number.setText(ph);
        location.setText(loc);
        email.setText(str);
        fname.setText(name);


        img_mail=findViewById(R.id.mail);
        img_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                System.out.println(ProfileActivity.str);
                String[] recipients={ProfileActivity.str};
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.setType("text/html");
                intent.setPackage("com.google.android.gm");
                startActivity(Intent.createChooser(intent, "Send mail"));
            }
        });

        img_phone=findViewById(R.id.phone);
        img_loc=findViewById(R.id.loc);

        img_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_DIAL);
                String p = "tel:" + ProfileActivity.ph;
                i.setData(Uri.parse(p));
                startActivity(i);
            }
        });

        img_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(ProfileActivity.this, loc , Toast.LENGTH_SHORT).show();}

        });
    }
}
