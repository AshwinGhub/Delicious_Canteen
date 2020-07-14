//this is where quantity is selected
//complete
package com.example.canteen_attempt4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Locale;

public class ItemToCart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_to_cart);

        final TextView qty_value=findViewById(R.id.qty_value);
        Button plus=findViewById(R.id.plus_button);
        Button minus=findViewById(R.id.minus_button);
        Intent intent=getIntent();
        String category_name=intent.getStringExtra("category");     //get category name from previous activity (ItemPageCustomer)
        final String item_name=intent.getStringExtra("item");       //get item name from previous activity (ItemPageCustomer)
        final TextView item_label=findViewById(R.id.item_label);
        final TextView rate_label=findViewById(R.id.rate_label);
        final TextView total=findViewById(R.id.total_amount);

        //get rate from database
        DatabaseReference myRef=FirebaseDatabase.getInstance().getReference("category/"+category_name+"/"+item_name);   //refer to item name in database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                item_label.setText(item_name+" rate = Rs.");
                rate_label.setText(dataSnapshot.child("rate").getValue().toString());  //take rate value from category/categoryName/itemName/rate
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ItemToCart.this,"Could not connect to database",Toast.LENGTH_SHORT).show();
            }
        });

        //when plus button is pressed
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //increment value of qty_value
                int qty=Integer.parseInt(qty_value.getText().toString());
                qty+=1;
                qty_value.setText(String.format(Locale.ENGLISH,"%d",qty)); //Integer.toString(qty) can be used. But compiler suggested this way
                //show total value logic = quantity*rate   we convert rate to int, multiply with qty and convert back to string
                total.setText(String.format(Locale.ENGLISH,"%d",qty*Integer.parseInt(rate_label.getText().toString())));
            }
        });

        //when minus button is pressed
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //decrement value of qty_value
                int qty=Integer.parseInt(qty_value.getText().toString());
                if(qty!=0)  //decrement only when qty is zero. Else error occurs
                {
                    qty-=1;
                    qty_value.setText(String.format(Locale.ENGLISH,"%d",qty));
                    total.setText(String.format(Locale.ENGLISH,"%d",qty*Integer.parseInt(rate_label.getText().toString())));  //set total value obtained by multiplying quantity and rate
                }
            }
        });

        final Handler handle=new Handler(); //Handler is used later to jump from non UI thread to main thread for Toasting and alerting

        //when add to cart button is pressed
        Button cart_button=findViewById(R.id.add_to_cart_button);
        cart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(qty_value.getText().equals("0")){    //if quantity is zero then add to cart button should not work
                    new AlertDialog.Builder(ItemToCart.this).setMessage("Quantity should be non zero").setPositiveButton("Okay",null).show();   //alert is displayed when quantity value is zero
                }
                else{       //for non-zero qty add item and quantity to room database
                Cart_database.databaseWriteExecutor.execute(new Runnable() {    //execute in new thread
                    @Override
                    public void run() {
                        Cart_database Instance=Cart_database.getDatabase(ItemToCart.this);  //Database instance assigned
                        Cart_dao dao=Instance.cart_dao();       //dao instance is also assigned. Not created
                        List<String> item_names=dao.get_item_names();       //List of existing item names are obtained from database
                        boolean flag=false;     //flag represents whether item already exists in cart
                        for(String name_of_each: item_names)        //for each loop
                        {
                            if(name_of_each.equals(item_name))
                            {
                                flag=true;          //if item exists, then flag is set
                            }
                        }
                        if(!flag){  //if item does not already exist in database we add the item to database and go back to menuPageCustomer activity
                        Cart_class obj=new Cart_class(item_name,qty_value.getText().toString(),rate_label.getText().toString(),total.getText().toString());
                        dao.insert(obj);        //insert the item object to data access object
                        handle.post(new Runnable() {        //handler used to update main UI thread.
                            @Override
                            public void run() {     //run the handler and make changes in main thread
                                Toast.makeText(ItemToCart.this,"Item added to cart successfully",Toast.LENGTH_LONG).show(); //Toast should not be in other thread. Always must execute in main thread. Else app crashes
                                Intent intent=new Intent(ItemToCart.this,MenuPageCustomer.class);
                                ItemToCart.this.startActivity(intent);
                            }
                        });
                        }
                        else{       //if item already exists in database
                            handle.post(new Runnable() {        //update main thread to display alert to user
                                @Override
                                public void run() {
                                    AlertDialog.Builder alert=new AlertDialog.Builder(ItemToCart.this);
                                    alert.setMessage("This item already exists in database. If you wish to update item quantity, please go to cart and update it");
                                    alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent=new Intent(ItemToCart.this,MenuPageCustomer.class);
                                            ItemToCart.this.startActivity(intent);
                                        }
                                    });
                                    alert.show();
                                }
                            });
                        }
                    }
                }); }
            }
        });
    }
}

//Ctrl+Shift+/ for block comment