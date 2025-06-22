package com.example.statussaverWAIG;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = com.example.statussaverWAIG.Entity.class, version = 1)
public abstract class EntityDatabase extends RoomDatabase {
        private static EntityDatabase instance;
        public abstract com.example.statussaverWAIG.DAO userDAO();
        public static synchronized EntityDatabase getInstance(Context context){
            if(instance == null){
                instance = Room.databaseBuilder(context.getApplicationContext(),
                        EntityDatabase.class,"entity_database").fallbackToDestructiveMigration()
                        .addCallback(roomCallback)
                        .build();
            }
            return instance;
        }
        private static Callback roomCallback = new Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
            }
        };
}
