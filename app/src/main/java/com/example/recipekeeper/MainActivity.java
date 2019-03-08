package com.example.recipekeeper;

import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    DBHelper myDB;

    RecyclerView recyclerView;
    List<Recipe> recipeList;
    RecipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup action bar
        Toolbar toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);

        // Setup database
        myDB = new DBHelper(this);
        // Give all models a reference to the database.
        Recipe.db = myDB;

        // Setup recycleviewer
        recyclerView = findViewById(R.id.recipe_list);
        recipeList = new ArrayList<>();
        adapter = new RecipeAdapter(recipeList);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            /**
             * Whenever an item is moved
             * @param target new position
             * @return successful?
             */
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                // Set database ordering to match users new ordering.
                recipeList.get(viewHolder.getAdapterPosition()).setPosition(target.getAdapterPosition());
                recipeList.get(target.getAdapterPosition()).setPosition(viewHolder.getAdapterPosition());
                // Swap the elements in the ArrayList
                Collections.swap(recipeList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                // Update the adapter.
                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // No swiping operation.
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                        ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
            }
        });
        touchHelper.attachToRecyclerView(recyclerView);

        viewAll(null);
    }

    /**
     * Add custom menu items to action bar.
     * @param menu automatically generated menu
     * @return if action was successful.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Run method based on which menu item was selected.
     * @param item menu item selected
     * @return if action was successful.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                searchPressed();
                break;
            case R.id.help:
                helpPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    void searchPressed() {
        showMessage("TODO", "Run search function.");
    }

    void helpPressed() {
        showMessage("TODO", "Run help function.");
    }

    /**
     * Fetches all recipes and places them in the recycle view.
     */
    public void viewAll(View view)
    {
        recipeList.clear();
        recipeList.addAll(Recipe.getRecipeList(DBHelper.FILTER.ALL));
        adapter.notifyDataSetChanged();
    }

    /**
     * Fetches favourite recipes only and places them in the recycle view.
     */
    public void viewFavourites(View view)
    {
        recipeList.clear();
        recipeList.addAll(Recipe.getRecipeList(DBHelper.FILTER.FAVOURITES));
        adapter.notifyDataSetChanged();
    }

    public void addRecipe(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Recipe Title");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myDB.createNewRecipe(input.getText().toString(), "");
                viewAll(null);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    void showMessage (String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

}
