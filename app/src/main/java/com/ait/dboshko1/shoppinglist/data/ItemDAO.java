package com.ait.dboshko1.shoppinglist.data;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ItemDAO {
    @Query("SELECT * FROM item")
    List<Item> getAll();

    @Insert
    long insertItem(Item item);

    @Update
    void update(Item item);

    @Delete
    void delete(Item item);

    @Query("DELETE FROM item")
    void deleteAll();

    @Query("DELETE FROM item WHERE id in (:itemIds)")
    void deletePurchased(List<Long> itemIds);
}
