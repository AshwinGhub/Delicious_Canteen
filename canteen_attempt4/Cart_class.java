//for defining the entity for room
package com.example.canteen_attempt4;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Cart_class {
    @PrimaryKey @NonNull
    String item_name;

    @NonNull @ColumnInfo
    String quantity;
    @ColumnInfo
    String rate;
    @ColumnInfo
    String total;

    public Cart_class(String item_name,String quantity,String rate,String total)
    {
        this.item_name=item_name;
        this.quantity=quantity;
        this.rate=rate;
        this.total=total;
    }

    @NonNull
    public String getItem_name() {          //get item name from database
        return item_name;
    }

    @NonNull
    public String getQuantity() {       //get quantity
        return quantity;
    }

    public String getRate() {       //get the rate
        return rate;
    }

    public String getTotal() {      //get the total value from database
        return total;
    }

}
// the below class entity is for customer login credentials. But shouldn't the customer details be available online????
@Entity
class customerLogin{

}
