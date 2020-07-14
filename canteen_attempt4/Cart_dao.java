//dao for room
package com.example.canteen_attempt4;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface Cart_dao {
        @Insert
        void insert(Cart_class item);   //insert one item
        @Query("Select count(*) from Cart_class")
        int get_item_count();                   //get the count on number of items in room database
        @Query("Select item_name from Cart_class")
        List<String> get_item_names();
        @Query("Select * from Cart_class")
        List<Cart_class> getAll();      //getAll() function with return type -> list of Cart_class objects
        @Delete
        void delete_item(Cart_class item);  //delete item function
        @Query("Delete from Cart_class")
        void deleteAll();
}
