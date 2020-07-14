//complete but requires an alert when no data connection is available
//to select the category for canteen staff
package com.example.canteen_attempt4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.NetworkInterface;

public class SelectCategory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);

        Intent intent=getIntent();   //getIntent from welcome screen (or when back key pressed from selectfood activity )
        final ProgressBar p=findViewById(R.id.progress);
        Button b1=findViewById(R.id.add_category_button);
        final Button b2=findViewById(R.id.delete_category_button);
        final LinearLayout l_layout=findViewById(R.id.layout_category);

        DatabaseReference myRef= FirebaseDatabase.getInstance().getReference("category");   //refer to category in database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int children=(int)dataSnapshot.getChildrenCount();
                if(children!=0)
                {
                    b2.setVisibility(View.VISIBLE); //delete button is made visible when there are categories present to delete
                    for(final DataSnapshot s: dataSnapshot.getChildren())
                    {
                        Button bg=new Button(SelectCategory.this);  //create a button
                        bg.setText(s.getKey());             //set name of button to category name obtained from the database
                        l_layout.addView(bg);               //add category name button to the layout view
                        bg.setOnClickListener(new View.OnClickListener() {      //when button is pressed corresponding category opens and the items in that category are displayed
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(SelectCategory.this,SelectFood.class); //intent to selectfood page where items in the current category are shown
                                intent.putExtra("category",s.getKey());     //pass category name to the selectfood activity
                                SelectCategory.this.startActivity(intent);
                            }
                        });
                    }
                        p.setVisibility(View.GONE); //disable the progressbar (circular) when data is loaded from database
                }
                else    //category is empty
                {
                    p.setVisibility(View.GONE);
                    TextView empty=findViewById(R.id.message);
                    empty.setText("No categories available");  //provide a message stating that database is empty
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SelectCategory.this,"Error loading from database",Toast.LENGTH_SHORT).show();
            }
        });

        //when add category button is pressed
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SelectCategory.this,AddCategory.class);
                SelectCategory.this.startActivity(intent);
            }
        });

        //when delete category button is pressed
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SelectCategory.this,DeleteCategory.class);
                SelectCategory.this.startActivity(intent);
            }
        });
    }

    //when back key is pressed in phone go back to welcome screen
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(SelectCategory.this,WelcomeScreen.class);
        SelectCategory.this.startActivity(intent);
    }

    //when middle button/home button in android phone is pressed we exit app and finish all activities for security
    @Override
    protected void onPause() {
        super.onPause();
        finishAffinity();
    }
}