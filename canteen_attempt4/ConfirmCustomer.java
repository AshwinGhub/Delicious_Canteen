package com.example.canteen_attempt4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ConfirmCustomer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_customer);

        Toast.makeText(ConfirmCustomer.this,getIntent().getStringExtra("ID"),Toast.LENGTH_LONG).show();
        final String id=getIntent().getStringExtra("ID");
        final LinearLayout layout=findViewById(R.id.layout);
        final LinearLayout.LayoutParams param=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.setMargins(0,0,0,15);
        TextView id_label=new TextView(this); id_label.setText("Customer ID = "+id);
        id_label.setTextSize(20);
        id_label.setLayoutParams(param);
        layout.addView(id_label);

        //get order details from database
        final DatabaseReference idRef= FirebaseDatabase.getInstance().getReference("customers/"+id);
        idRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {  //to get payment status and items from database
                TextView status;   //initializing status view to prevent warning in android studio
                for(DataSnapshot stat: dataSnapshot.getChildren())
                {
                    status=new TextView(ConfirmCustomer.this);
                    status.setText("Status : "+stat.getKey());
                    status.setTextSize(20);
                    status.setLayoutParams(param);
                    layout.addView(status);
                final DatabaseReference statusRef=FirebaseDatabase.getInstance().getReference(idRef.getPath()+"/"+stat.getKey());
                statusRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {  //this is to get items ordered
                        //move customer to completed orders in database
                        DatabaseReference CompleteRef=FirebaseDatabase.getInstance().getReference("complete/"+id);
                        TextView item;
                        for(DataSnapshot order: dataSnapshot.getChildren())
                        {
                            item=new TextView(ConfirmCustomer.this);
                            item.setText(order.getKey()+"="+order.getValue());
                            item.setTextSize(20);
                            layout.addView(item);
                            CompleteRef.child(order.getKey()).setValue(order.getValue());
                        }
                        //remove this customer from pending orders section
                       DatabaseReference DeleteIDRef=FirebaseDatabase.getInstance().getReference("customers/"+id);
                        DeleteIDRef.removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(ConfirmCustomer.this,"Could not load item from database",Toast.LENGTH_LONG).show();
                    }
                }); }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ConfirmCustomer.this,"Could not load payment status from database",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
       startActivity(new Intent(ConfirmCustomer.this,PendingOrders.class));
    }
}