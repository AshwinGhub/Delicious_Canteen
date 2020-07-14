//complete
//displays available categories
package com.example.canteen_attempt4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
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

public class MenuPageCustomer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_page_customer);

        final ProgressBar p=findViewById(R.id.progress);
        final LinearLayout l_layout=findViewById(R.id.layout);

        //create reference to category field of database
        final DatabaseReference myRef=FirebaseDatabase.getInstance().getReference("category");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int children=(int)dataSnapshot.getChildrenCount();      //get number of items in category
                if(children==0){    //check whether empty
                    p.setVisibility(View.GONE);     //progress bar is cancelled when data is loaded
                    TextView empty=findViewById(R.id.message);  //display message that menu is empty
                    empty.setText("Menu is empty");
                }
                else{
                for(DataSnapshot s: dataSnapshot.getChildren()) //take snapshot of each child in category field
                {
                    final Button bg=new Button(MenuPageCustomer.this);    //create button
                    l_layout.addView(bg);
                    bg.setText(s.getKey());             //button name is the name of the category
                    bg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {       //when button(category) is pressed intent to items activity
                            //intent to item display page
                            Intent intent=new Intent(MenuPageCustomer.this,ItemPageCustomer.class);
                            intent.putExtra("category",bg.getText().toString());
                            MenuPageCustomer.this.startActivity(intent);
                        }
                    });
                }
                    p.setVisibility(View.GONE);
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MenuPageCustomer.this,"Unable to connect to database",Toast.LENGTH_SHORT).show();
            }
        });

        final Handler handle=new Handler();
        final Button viewcart=findViewById(R.id.view_cart_button);  //button hidden initially
        //button must be visible when items are present in room database. So we first access room in seperate thread
        Cart_database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Cart_database item=Cart_database.getDatabase(MenuPageCustomer.this);
                Cart_dao dao=item.cart_dao();
                if(dao.get_item_count()!=0)
                {
                    //post change in UI of main thread
                    handle.post(new Runnable() {
                           @Override
                           public void run() {
                               viewcart.setVisibility(View.VISIBLE);
                           }
                       });
                }
            }
        });
        //open cart page on click and get data from room database
        viewcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MenuPageCustomer.this,CartToPayment.class);
                MenuPageCustomer.this.startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(MenuPageCustomer.this,OpeningPage.class); //remember here opening page opened up means database is wiped clean
        MenuPageCustomer.this.startActivity(intent);
    }
}