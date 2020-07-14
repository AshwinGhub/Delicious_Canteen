//not complete.
//this activity displays all items in the cart. Pay option goes to google pay api. The data is written in users field of database when payment is done.
package com.example.canteen_attempt4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class CartToPayment extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_to_payment);

        final List<String> name=new ArrayList<>();  //new List<> is wrong because List<> is abstract. This is a list of strings to store the item names
        final List<String> name_qty=new ArrayList<>();  //this is list of strings to store the quantity of each item
        final TableLayout layout=findViewById(R.id.table);
        final Handler handle=new Handler(); //handle UI thread operation from non UI thread below
        final Button pay_button=findViewById(R.id.pay_button);

        //seperate thread is used for getting data from room
        Cart_database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Cart_database Instance=Cart_database.getDatabase(CartToPayment.this);
                final Cart_dao dao=Instance.cart_dao();
                if(dao.get_item_count()==0) //cart is empty
                {
                    TextView empty=new TextView(CartToPayment.this);
                    empty.setText("Cart is empty");
                    empty.setTextSize(20);
                    layout.addView(empty);
                    handle.post(new Runnable() {    //make pay button invisible in UI thread
                        @Override
                        public void run() {
                            pay_button.setVisibility(View.GONE);
                        }
                    });
                }
                else {  //cart is not empty
                List<Cart_class> objs=dao.getAll();     //get list of all cart class objects
                TableRow t_row=new TableRow(CartToPayment.this);

                TextView item;
                layout.addView(t_row);

                item=new TextView(CartToPayment.this);
                item.setText("Item");
                item.setTextSize(20);
                t_row.addView(item);

                item=new TextView(CartToPayment.this);
                item.setText("Qty");
                item.setTextSize(20);
                t_row.addView(item);

                item=new TextView(CartToPayment.this);
                item.setText("Rate");
                item.setTextSize(20);
                t_row.addView(item);

                item=new TextView(CartToPayment.this);
                item.setText("Total");
                item.setTextSize(20);
                t_row.addView(item);

                int grand_total=0;

                for(final Cart_class obj: objs)
                {
                    t_row=new TableRow(CartToPayment.this);
                    layout.addView(t_row);

                    item=new TextView(CartToPayment.this);
                    String temp=obj.getItem_name();     //get item name from database
                    item.setText(temp);
                    item.setTextSize(17);
                    t_row.addView(item);
                    name.add(temp);         //put item name into list

                    item=new TextView(CartToPayment.this);
                    temp=obj.getQuantity();
                    item.setText(temp);
                    item.setTextSize(17);
                    t_row.addView(item);
                    item.setGravity(Gravity.RIGHT);
                    name_qty.add(temp);

                    item=new TextView(CartToPayment.this);
                    temp=obj.getRate();
                    item.setText(temp);
                    item.setTextSize(17);
                    t_row.addView(item);
                    item.setGravity(Gravity.RIGHT);

                    item=new TextView(CartToPayment.this);
                    temp=obj.getTotal();
                    item.setText(temp);
                    item.setTextSize(17);
                    item.setGravity(Gravity.RIGHT);
                    t_row.addView(item);

                    final ImageButton delete=new ImageButton(CartToPayment.this);
                    delete.setBackgroundResource(R.drawable.delete_icon); //delete button from drawable folder of res directory
                    //https://material.io/resources/icons/?icon=delete&style=baseline link from where cool icons can be downloaded
                    t_row.addView(delete);
                    handle.post(new Runnable() {
                        @Override
                        public void run() {     //button click listener must be added in the main UI thread
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Cart_database.databaseWriteExecutor.execute(new Runnable() {    //delete room item in non UI thread
                                    @Override
                                    public void run() {
                                        dao.delete_item(obj);
                                    }
                                });
                                    finish(); startActivity(getIntent());   //refresh the current activity
                                    Toast.makeText(CartToPayment.this,"Deleted",Toast.LENGTH_SHORT).show();
                               }
                           });
                        }
                    });

                    grand_total+=Integer.parseInt(obj.getTotal());  //add all the total values to get grand total
                }
                TextView grand_label=new TextView(CartToPayment.this);
                grand_label.setText("Grand Total = Rs."+grand_total);
                grand_label.setTextSize(20);
                grand_label.setGravity(Gravity.CENTER);
                layout.addView(grand_label);    }
            }
        }); //nonUI thread ends here

        final TextView myID=findViewById(R.id.id_label);
        //when pay button is pressed
        pay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check whether already logged in. If not, intent to CustomerSignIn activity
                
                //After this pay money
                //for testing we are directly writing to database with admission number taken from user
                AlertDialog.Builder admission_alert=new AlertDialog.Builder(CartToPayment.this);
                admission_alert.setTitle("Place order");
                final EditText adno_input=new EditText(CartToPayment.this); //admission number input from user
                adno_input.setHint("Enter admission number");
                admission_alert.setView(adno_input);        //add the edit text view to alert dialog box
                admission_alert.setPositiveButton("Sign in", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            //code to signup. for testing we are going with directly writing to database
                        myID.setText(adno_input.getText()); //set the ID label in the activity to the admission number provided by user to convert to qr code
                        DatabaseReference myRef= FirebaseDatabase.getInstance().getReference("customers/"+adno_input.getText().toString()+"/paid");
                        int count=0;
                        for(String s: name){
                            myRef.child(s).setValue(name_qty.get(count));
                            count+=1;
                        }
                        ImageView qr=findViewById(R.id.qr_code);
                        Bitmap bitmap=encodeAsBitmap(myID.getText().toString());    //call bitmap generating function to get qr code bitmap
                        qr.setImageBitmap(bitmap);  //display the bitmap which shows qr code
                        Toast.makeText(CartToPayment.this,"Item added to database. Payment part and login/signup under construction",Toast.LENGTH_LONG).show();
                    }
                });
                admission_alert.show();
            }
        });
    }

    Bitmap encodeAsBitmap(String ID)    //convert customer ID to qr bitmap
    {       //code from https://stackoverflow.com/questions/28232116/android-using-zxing-generate-qr-code
        BitMatrix result;
        try{
            result=new MultiFormatWriter().encode(ID, BarcodeFormat.QR_CODE,800,800);
        }
        catch(Exception e)
        {
            result=null;
            Toast.makeText(CartToPayment.this,"Unsupported format to generate qr from",Toast.LENGTH_SHORT).show();
        }
        int w=result.getWidth(); int h=result.getHeight();
        int[] pixels=new int[w*h];
        for(int y=0;y<h;y++)
        {
            int offset=y*w;
            for(int x=0;x<w;x++)
            {
                pixels[offset+x]=result.get(x,y)?BLACK:WHITE;
            }
        }
        Bitmap b = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        b.setPixels(pixels,0,800,0,0,w,h);
        return b;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,MenuPageCustomer.class));
    }
}