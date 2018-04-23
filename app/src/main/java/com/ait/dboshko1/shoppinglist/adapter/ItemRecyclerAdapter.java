package com.ait.dboshko1.shoppinglist.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ait.dboshko1.shoppinglist.ItemEditAndCreateDialog;
import com.ait.dboshko1.shoppinglist.MainActivity;
import com.ait.dboshko1.shoppinglist.R;
import com.ait.dboshko1.shoppinglist.data.AppDatabase;
import com.ait.dboshko1.shoppinglist.data.Item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.ViewHolder> {

    private List<Item> itemList;
    private Context context;

    public ItemRecyclerAdapter(List<Item> initialItems, Context context) {
        itemList = initialItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row,
                parent, false);

        return new ViewHolder(viewRow);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = itemList.get(holder.getAdapterPosition());
        setImage(holder, item);
        fillFormsFromItem(holder, item);
        setupButtonListeners(holder);
        setupCBListener(holder);
    }

    private void setupCBListener(final ViewHolder holder) {
        holder.cbPurchased.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        Item item = itemList.get(holder.getAdapterPosition());
                        item.setItemBought(!item.isItemBought());
                        AppDatabase.getAppDatabase(context).itemDAO().update(item);
                    }
                }.start();
            }
        });
    }

    private void setupButtonListeners(final ViewHolder holder) {

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItemByIndex(holder.getAdapterPosition());
            }
        });

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).showEditDialog(itemList.get(holder.getAdapterPosition()));
            }
        });

    }

    private void fillFormsFromItem(ViewHolder holder, Item item) {
        holder.tvName.setText(item.getItemName());
        holder.tvPrice.setText(String.format(Locale.US, "%.2f", item.getItemEstimatedPrice()));
        holder.tvDescription.setText(item.getItemDescription());
        holder.cbPurchased.setChecked(item.isItemBought());
    }

    public void addItem(Item item) {
        itemList.add(item);
        notifyItemInserted(itemList.size() - 1);
    }

    public void removeItemByIndex(int index) {
        final Item item = itemList.get(index);
        itemList.remove(index);
        notifyItemRemoved(index);

        new Thread() {
            @Override
            public void run() {
                AppDatabase.getAppDatabase(context).itemDAO().delete(
                        item);
            }
        }.start();
    }

    public void removeAllItems() {
        new Thread() {
            @Override
            public void run() {
                AppDatabase.getAppDatabase(context).itemDAO().deleteAll();
            }
        }.start();
        itemList.clear();
        notifyDataSetChanged();
    }

    public void removePurchasedItems() {
        final List<Long> itemIds = new ArrayList<>();
        Iterator<Item> it = itemList.iterator();
        while(it.hasNext()) {
            Item item = it.next();
            if(item.isItemBought()) {
                itemIds.add(item.getId());
                it.remove();
            }
        }
        notifyDataSetChanged();

        new Thread() {
            @Override
            public void run() {
                AppDatabase.getAppDatabase(context).itemDAO().deletePurchased(itemIds);
            }
        }.start();

    }

    public void updateItem(Item item) {
        int position = getIndexFromItem(item);
        itemList.set(position, item);
        notifyItemChanged(position);
    }

    private int getIndexFromItem(Item item) {
        for (int i = 0; i < itemList.size(); i++) {
            if(item.getId() == itemList.get(i).getId()) {
                return i;
            }
        }
        return -1;
    }

    private void setImage(ViewHolder holder, Item item) {
        switch (item.getItemCategory()) {
            case Item.FOOD:
                holder.imgIcon.setImageResource(R.drawable.food);
                break;
            case Item.BOOK:
                holder.imgIcon.setImageResource(R.drawable.book);
                break;
            case Item.ELECTRONIC:
                holder.imgIcon.setImageResource(R.drawable.laptop);
                break;
        }
    }



    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imgIcon;
        private TextView tvName;
        private TextView tvDescription;
        private TextView tvPrice;
        private CheckBox cbPurchased;
        private Button btnDelete;
        private Button btnEdit;


        public ViewHolder(View itemView) {
            super(itemView);

            imgIcon = itemView.findViewById(R.id.imgIcon);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvItemDescription);
            tvPrice = itemView.findViewById(R.id.tvItemPrice);
            cbPurchased = itemView.findViewById(R.id.cbPurchased);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);

        }
    }
}
