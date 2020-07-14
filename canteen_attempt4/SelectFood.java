//complete
package com.example.canteen_attempt4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SelectFood extends AppCompatActivity {

    private static final String TAG="SelectFood";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_food);
        Button b4=findViewById(R.id.add_item_button);
        final Button b5=findViewById(R.id.delete_item_button);
        final LinearLayout l_layout=(LinearLayout)findViewById(R.id.l_layout);
        Intent intent=getIntent();
        final String category=intent.getStringExtra("category");  //category name from previous activity
        final ProgressBar p=findViewById(R.id.progress);
        final TextView category_name=findViewById(R.id.category_name);
        category_name.setText(category);        //display the current category in which we are in

        //get all children from firebase so that we know which all items are present and hence create buttons for each item
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference myRef=database.getReference("category/"+category);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int children =(int)dataSnapshot.getChildrenCount();
                Log.d(TAG,"children = "+children);
                if(children!=0){
                for(DataSnapshot s: dataSnapshot.getChildren())
                {
                    final String name=s.getKey();  //get name of child (item name) in specific category we are in
                    Button bg=new Button(SelectFood.this);  //here context is the activity where button is to be created
                    bg.setText(name);       //assign child name to button text
                    l_layout.addView(bg);
                    //when bg button is clicked, main_activity to open
                    bg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(SelectFood.this,MainActivity.class);
                            intent.putExtra("category",category); //category name is passed to main activity class
                            intent.putExtra("type",name);   //item_name is also passed to main activity class
                            SelectFood.this.startActivity(intent);
                        }
                    });
                } p.setVisibility(View.GONE);
                    b5.setVisibility(View.VISIBLE);} //delete progress bar when content is loaded

                else { p.setVisibility(View.GONE);
                    b5.setVisibility(View.GONE);       //if there are no items available then delete button is not available
                    TextView empty=findViewById(R.id.message);
                    empty.setText("There are no items in this category");
                    empty.setTextSize(20); }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost: onCancelled", databaseError.toException());
            }
        });

        //When add item button pressed new activity opens using intent to that activity
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SelectFood.this,AddItem.class);  //AddItem activity class called
                intent.putExtra("category",category);
                SelectFood.this.startActivity(intent);
            }
        });

        //When delete item button is pressed new activity opens using intent
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SelectFood.this,DeleteItem.class);
                intent.putExtra("category",category);   //send category name to delete item activity
                SelectFood.this.startActivity(intent);
            }
        });

    }

    //when back key pressed exit app by deleting all activities at once
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(SelectFood.this,SelectCategory.class);
        SelectFood.this.startActivity(intent);
    }

    //when app is paused with home button app must close all activities and start over when user returns for security
    @Override
    protected void onPause() {
        super.onPause();
        finishAffinity();
    }
}