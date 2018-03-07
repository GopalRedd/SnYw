package com.ammson.snyw;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {



    TextView txtemail,txtname;
    Button bcapture,bupload,blogout;

    private SQLiteHandler db;
    private SessionManager session;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        txtemail=(TextView)findViewById(R.id.Email);
        txtname=(TextView)findViewById(R.id.Name);
        bcapture=(Button)findViewById(R.id.buttonCaptureImage);
        bupload=(Button)findViewById(R.id.buttonUploadImage);
        blogout=(Button)findViewById(R.id.btnLogout);



        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");
        // Displaying the user details on the screen
        txtname.setText(name);
        txtemail.setText(email);


        // Logout button click event
        blogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
        // Logout button click event
        bupload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        ImageUpload.class);
                startActivity(i);
                finish();
            }
        });
        bcapture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent j = new Intent(getApplicationContext(),
                        CameraUpload.class);
                startActivity(j);
                finish();
            }
        });
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();


    }
}
