//complete
package com.example.canteen_attempt4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
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

import org.w3c.dom.Text;

public class DeleteItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_item);

        final LinearLayout l_layout1=findViewById(R.id.l_layout1);
        final Intent intent=getIntent();
        final String category=intent.getStringExtra("category");    //get category name from previous activity to display items in that category from database
        final ProgressBar p=findViewById(R.id.progress);
        TextView category_label=findViewById(R.id.category_label);
        category_label.setText(category);

        //get all the children from firebase
        final DatabaseReference myRef=FirebaseDatabase.getInstance().getReference("category/"+category); //refer the category so that item in that category can be deleted
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int children=(int)dataSnapshot.getChildrenCount();
                if(children!=0){
                for(DataSnapshot s: dataSnapshot.getChildren())
                {
                    final Button bg=new Button(DeleteItem.this);   //create button
                    bg.setText(s.getKey());         //button name is same as child name from firebase which is the name of the item
                    bg.setVisibility(View.VISIBLE);
                    l_layout1.addView(bg);       //added button to linear layout

                    //when item button is clicked, the corresponding child in firebase must be deleted
                    bg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //warning message code asking confirmation for deleting item
                            AlertDialog.Builder warn=new AlertDialog.Builder(DeleteItem.this);
                            warn.setTitle("Delete item").setMessage("Are you sure you want to delete item "+bg.getText().toString()+"?");
                            warn.setCancelable(true);
                            warn.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //child in firebase is referenced using name of button and removed
                                    myRef.child(bg.getText().toString()).removeValue(); //myRef is database name and child of database is located with button name
                                    Toast.makeText(DeleteItem.this,bg.getText().toString()+" deleted",Toast.LENGTH_SHORT).show();
                                    //intent to same activity so that new set of buttons are loaded from database
                                    Intent intent=new Intent(DeleteItem.this,DeleteItem.class);
                                    intent.putExtra("category",category);   //provide category when activity restarts so that delete item page shows list of items left in the category we are in
                                    DeleteItem.this.startActivity(intent);
                                }
                            });
                            warn.setNegativeButton("No",null);
                            warn.show();
                        }
                    });
                }
                //delete progress bar once content is loaded
                p.setVisibility(View.GONE);}
                else { p.setVisibility(View.GONE);
                    TextView empty=findViewById(R.id.Message);
                    empty.setText("No more items to delete");
                    DatabaseReference myRef=FirebaseDatabase.getInstance().getReference("category"); //this is necessary because firebase will otherwise delete the whole category
                    myRef.child(category).setValue("0");        //we create a category of same name again with value 0 so that category still exists in the category page
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    AlertDialog.Builder warn=new AlertDialog.Builder(DeleteItem.this);
                    warn.setMessage("Could not connect to database");
                    warn.show();
            }
        });
    }

    //when back key pressed go to selectfood page
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            Intent intent1=getIntent();
            String category=intent1.getStringExtra("category"); //category name is to be sent back to selectfood activity
            Intent intent=new Intent(DeleteItem.this,SelectFood.class);
            intent.putExtra("category",category);   //pass the category name to selectfood activity
            DeleteItem.this.startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause(); finishAffinity();
    }
}