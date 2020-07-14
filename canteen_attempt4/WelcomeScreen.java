//button for pending orders remaining
package com.example.canteen_attempt4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class WelcomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        //getIntent() is not required because we are not using it anywhere. Intent automatically happens. Just need to instantiate it if we need to get some value from the intent
        Button manage=findViewById(R.id.manage_button);

        manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WelcomeScreen.this,SelectCategory.class);
                WelcomeScreen.this.startActivity(intent);
            }
        });

        Button pending_orders=findViewById(R.id.pending_order_button);
        pending_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WelcomeScreen.this,PendingOrders.class);
                WelcomeScreen.this.startActivity(intent);
            }
        });

        Button signout=findViewById(R.id.sign_out_button);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //when sign out button clicked
                FirebaseAuth.getInstance().signOut();   //sign out from account when sign out button is pressed
                Toast.makeText(WelcomeScreen.this,"You have signed out",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(WelcomeScreen.this,OpeningPage.class));
            }
        });

    }

    //when back button is pressed it never goes to initial login screen but remains in welcome screen itself
    @Override
    public void onBackPressed() {
        Toast.makeText(WelcomeScreen.this,"You cannot go back from this stage. Please sign out to exit this screen",Toast.LENGTH_LONG).show();
    }

    //when home button pressed or app is switched, the app closes completely for security
    @Override
    protected void onPause() {
        super.onPause();
        finishAffinity();
    }
}