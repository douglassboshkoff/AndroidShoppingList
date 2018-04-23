package com.ait.dboshko1.shoppinglist;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.ait.dboshko1.shoppinglist.adapter.ItemRecyclerAdapter;
import com.ait.dboshko1.shoppinglist.data.AppDatabase;
import com.ait.dboshko1.shoppinglist.data.Item;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemEditAndCreateDialog.ItemHandler {

    public static final String KEY_ITEM_TO_EDIT = "KEY_ITEM_TO_EDIT";
    private ItemRecyclerAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFAB();
        RecyclerView recyclerView = initRecyclerView();
        initItems(recyclerView);
        DrawerLayout drawerLayout = initDrawerLayout();
        initToolbar(drawerLayout);
    }

    private DrawerLayout initDrawerLayout() {
        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        switch (menuItem.getItemId()) {
                            case R.id.actionDeleteAll:
                                itemAdapter.removeAllItems();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                break;
                            case R.id.actionCreateItem:
                                showItemCreateDialog();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                break;

                            case R.id.actionDeletePurchased:
                                itemAdapter.removePurchasedItems();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                break;
                        }

                        return false;
                    }
                });
        return drawerLayout;
    }

    private void initToolbar(final DrawerLayout drawerLayout) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /*
    private void initTabLayout() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
    } */

    private RecyclerView initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerItem);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        return recyclerView;
    }

    private void initFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showItemCreateDialog();
            }
        });
    }


    private void showItemCreateDialog() {
        new ItemEditAndCreateDialog().show(getFragmentManager(), "Create Dialog");
    }

    private void initItems(final RecyclerView recyclerView) {
        new Thread() {
            @Override
            public void run() {
                final List<Item> items =
                        AppDatabase.getAppDatabase(MainActivity.this).itemDAO().getAll();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        itemAdapter = new ItemRecyclerAdapter(items, MainActivity.this);
                        recyclerView.setAdapter(itemAdapter);
                    }
                });
            }
        }.start();
    }



    @Override
    public void onNewItemCreated(final Item newItem) {
        new Thread() {
            @Override
            public void run() {
                long id = AppDatabase.getAppDatabase(MainActivity.this).itemDAO().insertItem(newItem);
                newItem.setId(id);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        itemAdapter.addItem(newItem);
                    }
                });
            }
        }.start();
    }

    @Override
    public void onItemEdited(final Item editItem) {
        new Thread() {
            @Override
            public void run() {
                AppDatabase.getAppDatabase(MainActivity.this).itemDAO().update(editItem);
            }
        }.start();

        itemAdapter.updateItem(editItem);
    }

    public void showEditDialog(Item item) {
        ItemEditAndCreateDialog dialog = new ItemEditAndCreateDialog();

        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_ITEM_TO_EDIT, item);

        Log.d("Item", "showEditDialog: " + item.toString());

        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), "Edit Dialog");
    }
}
