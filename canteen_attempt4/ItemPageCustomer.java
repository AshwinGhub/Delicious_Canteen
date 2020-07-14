//complete
//select items here. each item quantity can be selected in next page
package com.example.canteen_attempt4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
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

public class ItemPageCustomer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_page_customer);

        Intent intent=getIntent();
        final String category_name=intent.getStringExtra("category");
        final ProgressBar p=findViewById(R.id.progress);
        final LinearLayout l_layout=findViewById(R.id.layout);
        TextView category_label=findViewById(R.id.category_label);
        category_label.setText(category_name);

        //get the item names from database and create buttons and textviews based on availability
        DatabaseReference myRef= FirebaseDatabase.getInstance().getReference("category/"+category_name);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int children=(int)dataSnapshot.getChildrenCount();
                if(children!=0)
                {
                    for(final DataSnapshot s: dataSnapshot.getChildren())
                    {
                            if(s.child("available").getValue().toString().equals("1"))  //if item is available button is created
                            {
                                Button bg = new Button(ItemPageCustomer.this);
                               String bg_name=s.getKey()+" - Available";
                               bg.setText(bg_name);
                               l_layout.addView(bg);
                               //button click only for items that are available
                               bg.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //intent to quantity choosing page for placing order
                                    Intent intent=new Intent(ItemPageCustomer.this,ItemToCart.class);
                                    intent.putExtra("item",s.getKey());
                                    intent.putExtra("category",category_name);
                                    ItemPageCustomer.this.startActivity(intent);
                                }
                            }); }
                        else {  //for items that are not available a text message is only displayed
                                TextView unavail=new TextView(ItemPageCustomer.this);
                                String unavail_name=s.getKey()+" - Unavailable";
                                unavail.setTextSize(20);
                                unavail.setGravity(Gravity.CENTER); //center align the text
                                unavail.setText(unavail_name);
                                l_layout.addView(unavail);
                        }

                    } p.setVisibility(View.GONE);
                }
                else
                {
                    p.setVisibility(View.GONE);
                    TextView message=findViewById(R.id.message);
                    message.setText("No items in "+category_name);  //display message when no categories exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ItemPageCustomer.this,"Could not connect to database",Toast.LENGTH_SHORT).show();
            }
        });
    }
}