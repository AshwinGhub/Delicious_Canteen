//complete
package com.example.canteen_attempt4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

public class DeleteCategory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_category);

        final LinearLayout l_layout=findViewById(R.id.layout);
        final ProgressBar p=findViewById(R.id.progress);

        final DatabaseReference myRef= FirebaseDatabase.getInstance().getReference("category");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int children=(int)dataSnapshot.getChildrenCount();
                if(children!=0){
                    for(DataSnapshot s: dataSnapshot.getChildren())
                    {
                        final Button bg=new Button(DeleteCategory.this);
                        bg.setText(s.getKey());
                        l_layout.addView(bg);
                        bg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder alert=new AlertDialog.Builder(DeleteCategory.this);
                                alert.setMessage("Deleting this category would delete all items in it. Are you sure you want to delete "+bg.getText().toString()+"?");
                                alert.setCancelable(true);
                                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        myRef.child(bg.getText().toString()).removeValue();
                                        Toast.makeText(DeleteCategory.this, bg.getText().toString()+" deleted", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(DeleteCategory.this,DeleteCategory.class);
                                        DeleteCategory.this.startActivity(intent);
                                    }
                                });
                                alert.setNegativeButton("No",null);
                                alert.show();
                            }
                        });
                    } p.setVisibility(View.GONE);
                }
                else        //when there is no category avaiable (children=0)
                {
                    p.setVisibility(View.GONE);
                    TextView empty=findViewById(R.id.message);
                    empty.setText("No more categories to delete");
                    myRef.setValue("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(DeleteCategory.this,"Could not connect to database",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(DeleteCategory.this,SelectCategory.class);
        DeleteCategory.this.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finishAffinity();
    }
}