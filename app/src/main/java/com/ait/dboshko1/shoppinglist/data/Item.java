package com.ait.dboshko1.shoppinglist.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Item implements Serializable{
    //TODO: Create an enum and figure out how to store it in room?
    public static final String FOOD = "Food";
    public static final String BOOK = "Book";
    public static final String ELECTRONIC = "Electronic";

    public Item(String itemName, String itemCategory, String itemDescription, double itemEstimatedPrice, boolean itemBought) {
        this.itemName = itemName;
        this.itemCategory = itemCategory;
        this.itemDescription = itemDescription;
        this.itemEstimatedPrice = itemEstimatedPrice;
        this.itemBought = itemBought;
    }

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "item_name")
    private String itemName;

    @ColumnInfo(name = "item_category")
    private String itemCategory;

    @ColumnInfo(name = "item_description")
    private String itemDescription;

    @ColumnInfo(name = "itemEstimatedPrice")
    private double itemEstimatedPrice;

    @ColumnInfo(name = "item_bought")
    private boolean itemBought;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public double getItemEstimatedPrice() {
        return itemEstimatedPrice;
    }

    public void setItemEstimatedPrice(double itemEstimatedPrice) {
        this.itemEstimatedPrice = itemEstimatedPrice;
    }

    public boolean isItemBought() {
        return itemBought;
    }

    public void setItemBought(boolean itemBought) {
        this.itemBought = itemBought;
    }

    public static String[] getCategories() {
        return new String[]{FOOD, BOOK, ELECTRONIC};
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", itemName='" + itemName + '\'' +
                ", itemCategory='" + itemCategory + '\'' +
                ", itemDescription='" + itemDescription + '\'' +
                ", itemEstimatedPrice=" + itemEstimatedPrice +
                ", itemBought=" + itemBought +
                '}';
    }
}
