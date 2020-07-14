//complete
//this page is to update the rate and availability of items
package com.example.canteen_attempt4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private String available_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b=findViewById(R.id.upload_button);
        final EditText input=(EditText)findViewById(R.id.input_number);
        Intent intent=getIntent();  //Intent to link from selectfood activity
        final String category=intent.getStringExtra("category"); //receive the category name from selectfood activity
        String item=intent.getStringExtra("type");  //receive the itemname from selectfood activity
        TextView label=findViewById(R.id.item_label);
        label.setText(item+" rate");

        //below code to read database. type references item(vada/chicken) in database. Reads data from database whenever there is change in value of item
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;
        myRef=database.getReference("category/"+category+"/"+item+"/rate");  //equivalent to category/category_name/item_name/rate in database path
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);  //get the read value convert to string
                Log.d("MainActivity", "Value is: " + value);  //message shows up in logcat of android studio when data updated in database
                input.setText(value);          //value represents the rate of item in database. It is shown in edit text field for reference for user while updating
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("MainActivity", "Failed to read value.", error.toException());
                Toast.makeText(MainActivity.this,"Failed to obtain rate from database",Toast.LENGTH_SHORT).show();
                TextView value=findViewById(R.id.text_appear);
                value.setText(error.toException().toString());
            }
        });

        //below code is for showing item current availability to staff from database qty value
        myRef=database.getReference("category/"+category+"/"+item+"/available");  //finding category/category_name/item_name/available value gives availability detail of item in the specific category
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again whenever data at this location is updated.
                available_value = dataSnapshot.getValue(String.class);  //get the read value convert to string
                Log.d("MainActivity", "Value is: " + available_value);  //message shows up in logcat of android studio when data updated in database
                TextView avail=findViewById(R.id.text_appear);
                if(available_value.equals("0"))
                {
                        avail.setText("Currently unavailable");
                }
                else
                {
                    avail.setText("Currently available");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("MainActivity", "Failed to read value.", error.toException());
                Toast.makeText(MainActivity.this,"Failed to get availability detail",Toast.LENGTH_SHORT).show();
            }
        });

        //below code is for update button
        b.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                //when update pressed, data is written to firebase database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                Intent intent=getIntent();
                String item=intent.getStringExtra("type");
                DatabaseReference myRef;
                myRef=database.getReference("category/"+category+"/"+item+"/rate");
                myRef.setValue(input.getText().toString());//data in edit text field is written to database
                myRef=database.getReference("category/"+category+"/"+item+"/available");
                myRef.setValue(available_value);
            }
        });
    }

    //when back key pressed go to selectfood page
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
                Intent intent1=getIntent();
                String category=intent1.getStringExtra("category");
                String item=intent1.getStringExtra("type");
                Intent intent=new Intent(MainActivity.this,SelectFood.class);
                intent.putExtra("category",category);
                intent.putExtra("type",item);
                MainActivity.this.startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        finishAffinity();
    }

    //logic for assigning qty value based on which radio button is pressed by user
    public void AvailableOrUnavailable(View view){
        boolean checked=((RadioButton)view).isChecked();
        switch(view.getId())    // Check which radio button was clicked
        {
            case R.id.available:    if (checked)
                available_value="1";
                break;
            case R.id.unavailable:     if (checked)
                available_value="0";
                break;
        }
    }
}