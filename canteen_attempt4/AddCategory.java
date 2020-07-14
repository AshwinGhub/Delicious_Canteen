//complete
package com.example.canteen_attempt4;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddCategory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        final EditText category_input=findViewById(R.id.category_input);
        Button add_button=findViewById(R.id.add_category_button);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!category_input.getText().toString().equals("")){
                DatabaseReference myRef= FirebaseDatabase.getInstance().getReference("category/"+category_input.getText().toString()); //here the logic is that we refer to a category name that is given by the user
                myRef.setValue("0");    //Firebase automatically creates the user given category name and sets a value of "0" to it
                Toast.makeText(AddCategory.this,"New Category created",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddCategory.this,SelectCategory.class));}
                else{
                    AlertDialog.Builder alert=new AlertDialog.Builder(AddCategory.this);
                    alert.setMessage("Category name cannot be empty");
                    alert.setPositiveButton("Okay",null); alert.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(AddCategory.this,SelectCategory.class);
        AddCategory.this.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause(); finishAffinity();
    }
}