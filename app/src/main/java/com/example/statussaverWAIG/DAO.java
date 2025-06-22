package com.example.statussaverWAIG;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface DAO {
    String nt1 = "DELETE FROM number_table",nt2 = "DELETE FROM number_table WHERE id = :id",
    nt3 = "SELECT * FROM number_table ORDER BY id DESC";

    @Insert
    void insert(com.example.statussaverWAIG.Entity user);

    @Update
    void update(com.example.statussaverWAIG.Entity user);

    @Delete
    void delete(com.example.statussaverWAIG.Entity user);


    @Query(nt1)
    void deleteAlluser();

    @Query(nt2)
    void deletenumber(int id);

    @Query(nt3)
    LiveData<List<Entity>> getAlluser();


}
