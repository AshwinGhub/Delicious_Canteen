//complete
package com.example.canteen_attempt4;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class AddItem extends AppCompatActivity {

    private static String available_value="0";    //indicates not available

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        final EditText item_name=findViewById(R.id.item_input);
        final EditText rate_input=findViewById(R.id.rate_input);
        Button Add_button=findViewById(R.id.Add_button);
        Intent intent=getIntent();
        final String category=intent.getStringExtra("category");
        TextView category_label=findViewById(R.id.category_name);
        category_label.setText(category);   //display category in which new item is added for reference

        //when add button is pressed item is to be added to firebase database
        Add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!item_name.getText().toString().equals("")&&!rate_input.getText().toString().equals("")){
                //Here the below code is as if the item rate and qty already exists in the database. But what happens is firebase automatically creates the new item child and subchilds qty and rate
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef=database.getReference("category/"+category+"/"+item_name.getText()+"/rate");
                myRef.setValue(rate_input.getText().toString());  //write rate value from app to database
                myRef=database.getReference("category/"+category+"/"+item_name.getText()+"/available");
                myRef.setValue(available_value);  //available_value is obtained from staff using radio button
                Intent intent=new Intent(AddItem.this,SelectFood.class);
                intent.putExtra("category",category);
                AddItem.this.startActivity(intent);
                Toast.makeText(AddItem.this, item_name.getText().toString()+" added to "+category, Toast.LENGTH_SHORT).show(); }
                else{   //display alert if details are not provided completely
                    new AlertDialog.Builder(AddItem.this).setMessage("Please fill all fields").setPositiveButton("Okay",null).show();
                }
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
            Intent intent=new Intent(AddItem.this,SelectFood.class);
            intent.putExtra("category",category);
            AddItem.this.startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause(); finishAffinity();
    }

    //radio button option available logic
    public void AvailableOrUnavailable(View view){
        boolean checked=((RadioButton)view).isChecked();
        switch(view.getId())    // Check which radio button was clicked
        {
            case R.id.available:    if (checked)
                                    available_value="1";
                                    break;
                        default:    if (checked)        //By default the item is set to unavailable
                                    available_value="0";
                                    break;
        }
    }

}