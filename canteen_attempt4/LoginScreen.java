//complete
package com.example.canteen_attempt4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginScreen extends AppCompatActivity {

    private FirebaseAuth mAuth=FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        final EditText user=findViewById(R.id.username); //username is emailid
        final EditText pass=findViewById(R.id.password);  //password that they created
        Button login_button=findViewById(R.id.login_button);


        user.setText("ashwinspradeep@gmail.com"); //remove this line after testing of app is complete. only for developer's ease of use
        pass.setText("123454321");  //remove this too after testing app

        //when login button pressed
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = user.getText().toString();     //get emailid from edittext field
                String password = pass.getText().toString();      //get password from user input(edittext) field

                if(password.equals("")) { //to prevent app from crashing without input from user
                  Toast.makeText(LoginScreen.this,"Please fill all the details",Toast.LENGTH_LONG).show(); }
                else if(email.equals(""))  //to prevent app from crashing
                {  Toast.makeText(LoginScreen.this,"Please fill all the details",Toast.LENGTH_LONG).show();  }
                else
                { //provide sign in email and password to firebase for authenticating
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginScreen.this, new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())  //if authentication is successful, ie, user identified
                                    {
                                        Log.d("LoginScreen", "signInWithEmail:success");
                                        Toast.makeText(LoginScreen.this, "Successfully signed in", Toast.LENGTH_SHORT).show();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Intent intent = new Intent(LoginScreen.this, WelcomeScreen.class);
                                        LoginScreen.this.startActivity(intent);
                                    } else {
                                        Log.w("LoginScreen", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginScreen.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(LoginScreen.this, OpeningPage.class));
    }
}