//complete
//this is staff side
package com.example.canteen_attempt4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Database;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PendingOrders extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_orders);

        final ProgressBar p=findViewById(R.id.waiter);
        final LinearLayout l_layout=findViewById(R.id.l_layout);
        DatabaseReference myRef= FirebaseDatabase.getInstance().getReference("customers");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()!=0){
                final TableLayout layout=findViewById(R.id.layout);
                final TableRow t_row=new TableRow(PendingOrders.this);
                TextView item=new TextView(PendingOrders.this);
                item.setText("CustomerID  ");  item.setTextSize(18);  ; t_row.addView(item);
                item=new TextView(PendingOrders.this); item.setText("Status  "); item.setTextSize(18);  t_row.addView(item);
                item=new TextView(PendingOrders.this); item.setText(" Orders"); item.setTextSize(18); t_row.addView(item);
                layout.addView(t_row);
                p.setVisibility(View.GONE);

                for(final DataSnapshot s: dataSnapshot.getChildren())       //get all the customer IDs
                {
                    final TableRow t_row1=new TableRow(PendingOrders.this);
                    item=new TextView(PendingOrders.this);
                    item.setText(s.getKey());
                    item.setTextSize(18);
                    final TableRow.LayoutParams param=new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    param.setMargins(30,20,30,20);
                    item.setLayoutParams(param);
                    t_row1.addView(item);
                    final DatabaseReference IDRef=FirebaseDatabase.getInstance().getReference("customers/"+s.getKey()); //refer to the customer id
                    IDRef.addListenerForSingleValueEvent(new ValueEventListener(){
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                            for(DataSnapshot stat:dataSnapshot.getChildren()){  //get status
                            final TextView item1=new TextView(PendingOrders.this);
                            item1.setText(stat.getKey());   //set the status of order (paid or unpaid)
                            item1.setTextSize(18);
                            item1.setLayoutParams(param);
                            t_row1.addView(item1);

                            DatabaseReference statusRef=FirebaseDatabase.getInstance().getReference(IDRef.getPath()+"/"+stat.getKey());
                            statusRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    StringBuilder items_string=new StringBuilder();
                                    for(DataSnapshot order:dataSnapshot.getChildren())      //get items
                                    {
                                        items_string.append(order.getKey()+" = "+order.getValue().toString()+", "); //get all the items into a string
                                    }
                                    items_string.append(".");
                                    TextView item2=new TextView(PendingOrders.this);
                                    item2.setText(items_string.toString());
                                    item2.setTextSize(18);
                                    param.setMargins(5,20,5,20);
                                    item2.setLayoutParams(param);
                                    t_row1.addView(item2);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(PendingOrders.this,"Failed to fetch order from database",Toast.LENGTH_SHORT).show();
                                }
                            });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(PendingOrders.this,"Failed to fetch payment status from database",Toast.LENGTH_SHORT).show();
                        }
                    });  layout.addView(t_row1);
                }
            }
            else { p.setVisibility(View.GONE); TextView empty=new TextView(PendingOrders.this);
            empty.setText("There are no pending orders"); l_layout.addView(empty); }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PendingOrders.this,"Failed to fetch customerID from database",Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton scan=findViewById(R.id.scan_button);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //scan qr code of customer displayed on customer's phone and give received signal
                //intent to qr code scanning activity
                    Intent intent=new Intent(PendingOrders.this,qrReader.class);
                    PendingOrders.this.startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finishAffinity();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PendingOrders.this,WelcomeScreen.class));
    }
}