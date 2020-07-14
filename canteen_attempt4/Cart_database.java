//database for room
package com.example.canteen_attempt4;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

    @Database(entities = {Cart_class.class}, version = 1, exportSchema = false)
    public abstract class Cart_database extends RoomDatabase {

        public abstract Cart_dao cart_dao();
        private static volatile Cart_database INSTANCE;
        private static final int NUMBER_OF_THREADS = 4;
        static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);   //Execute on 4 nonUI threads

        static Cart_database getDatabase(final Context context) {
            if (INSTANCE == null) {
                synchronized (Cart_database.class) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.getApplicationContext(), Cart_database.class, "cart_database").build();
                    }
                }
            }
            return INSTANCE;
        }
    }
