//This is the page that allows to select either customer or staff
//This is currently canteen server side app and customer side app all in one. A zip file is available for earlier working version in Documents\canteen_app_zip_files
//At this phase, successfully done:
// 1. Rate updation possible
// 2. User authentication for server
// 3. customer select quantity of food
// 4. customer side display rate of food
// 5. Allow staff to add new item in database as well as delete existing item in database
// what is left for next phase:
// 1. display pending orders at server side
// 2. customer sign up and login. When placing order, customer needs to be signed in
// 3. Bill calculation - Create the cart activity page (rooms can be used if needed)
// 4. Google pay API
// 5. Transaction - create user field in main database and add customers from customer side, then access from server side
// 6. alert when there is no internet connection
// 7. app crashing in landscape mode welcome screen
// Note : Data stored in firebase is string format
package com.example.canteen_attempt4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OpeningPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_page);

        /*Cart_database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Cart_database Instance=Cart_database.getDatabase(OpeningPage.this);
                Cart_dao dao=Instance.cart_dao();
                dao.deleteAll();
            }
        }); */
        Button staff=findViewById(R.id.staff_button);
        Button customer=findViewById(R.id.customer_button);

        staff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //intent to canteen staff login page
                Intent intent=new Intent(OpeningPage.this,LoginScreen.class);
                OpeningPage.this.startActivity(intent);
            }
        });

        customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //intent to customer menu page. login can be done at a later stage
                    Intent intent=new Intent(OpeningPage.this,MenuPageCustomer.class);
                    OpeningPage.this.startActivity(intent);
            }
        });
    }

   @Override
    public void onBackPressed() {
        finishAffinity();
    }
}