package com.ait.dboshko1.shoppinglist.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.ait.dboshko1.shoppinglist.MainActivity;
import com.ait.dboshko1.shoppinglist.R;
import com.ait.dboshko1.shoppinglist.data.AppDatabase;
import com.ait.dboshko1.shoppinglist.data.Item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.ViewHolder>
        implements Filterable{

    private List<Item> filteredItemList;
    private List<Item> originalItemList;
    private Context context;

    public ItemRecyclerAdapter(List<Item> initialItems, Context context) {
        originalItemList = new ArrayList<>(initialItems);
        filteredItemList = new ArrayList<>(initialItems);
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
        Item item = filteredItemList.get(holder.getAdapterPosition());
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
                        Item item = filteredItemList.get(holder.getAdapterPosition());
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
                ((MainActivity) context).showEditDialog(filteredItemList.get(holder.getAdapterPosition()));
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
        filteredItemList.add(item);
        originalItemList.add(item);
        notifyItemInserted(filteredItemList.size() - 1);
    }


    private void removeItemByIndex(int index) {
        final Item item = filteredItemList.get(index);
        filteredItemList.remove(index);
        originalItemList.remove(item);
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
        filteredItemList.clear();
        originalItemList.clear();
        notifyDataSetChanged();
    }

    public void removePurchasedItems() {
        final List<Long> itemIds = removePurchasedItemsFromItemList();
        notifyDataSetChanged();

        new Thread() {
            @Override
            public void run() {
                AppDatabase.getAppDatabase(context).itemDAO().deletePurchased(itemIds);
            }
        }.start();

    }

    @NonNull
    private List<Long> removePurchasedItemsFromItemList() {
        final List<Long> itemIds = new ArrayList<>();
        iterateAndDeletePurchased(itemIds, originalItemList);
        iterateAndDeletePurchased(itemIds, filteredItemList);
        return itemIds;
    }

    private void iterateAndDeletePurchased(List<Long> itemIds, List<Item> items) {
        Iterator<Item> it = items.iterator();
        while(it.hasNext()) {
            Item item = it.next();
            if(item.isItemBought()) {
                itemIds.add(item.getId());
                it.remove();
            }
        }
    }

    public void updateItem(Item item) {
        int position = getIndexFromItem(item);
        filteredItemList.set(position, item);
        originalItemList.set(position, item);
        notifyItemChanged(position);
    }

    private int getIndexFromItem(Item item) {
        for (int i = 0; i < filteredItemList.size(); i++) {
            if(item.getId() == filteredItemList.get(i).getId()) {
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
        return filteredItemList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredItemList = new ArrayList<>(originalItemList);
                } else {
                    List<Item> filteredList = new ArrayList<>();
                    for (Item item : originalItemList) {
                        if (item.getItemName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(item);
                        }
                    }
                    filteredItemList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredItemList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredItemList = (ArrayList<Item>) filterResults.values;
                notifyDataSetChanged();
            }
        };
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
