package com.ait.dboshko1.shoppinglist;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ait.dboshko1.shoppinglist.adapter.ItemRecyclerAdapter;
import com.ait.dboshko1.shoppinglist.data.AppDatabase;
import com.ait.dboshko1.shoppinglist.data.Item;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemEditAndCreateDialog.ItemHandler {

    public static final String KEY_ITEM_TO_EDIT = "KEY_ITEM_TO_EDIT";
    private ItemRecyclerAdapter itemAdapter;
    private SearchView searchView;

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

    @Override
    protected void onNewIntent(Intent intent) {
        checkForSearchIntent(intent);
    }

    private void checkForSearchIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            itemAdapter.getFilter().filter(query);
        }
    }

    private DrawerLayout initDrawerLayout() {
        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
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
        Toolbar toolbar = findViewById(R.id.toolbar);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        initSearchManager(menu);
        return true;
    }

    private void initSearchManager(Menu menu) {
        // Code adapted from tutorial at
        // https://www.androidhive.info/2017/11/android-recyclerview-with-search-filter-functionality/
        // as well as android documentation on search

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                itemAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                itemAdapter.getFilter().filter(query);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
            searchView.clearFocus();
            return;
        }
        super.onBackPressed();
    }

    private RecyclerView initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerItem);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        return recyclerView;
    }

    private void initFAB() {
        FloatingActionButton fab = findViewById(R.id.fab);
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

        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), "Edit Dialog");
    }
}
