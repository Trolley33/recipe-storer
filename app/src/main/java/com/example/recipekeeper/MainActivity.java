package com.example.recipekeeper;

import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    DBHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup action bar
        Toolbar toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);

        // Setup database
        myDB = new DBHelper(this);

        viewAll();
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
        Toast.makeText(this, "Search pressed.", Toast.LENGTH_SHORT).show();
    }

    void helpPressed() {
        Toast.makeText(this, "Help pressed.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Fetches all recipes and places them in the recycle view.
     */
    public void viewAll()
    {
        Cursor res = myDB.getRecipeList(DBHelper.FILTER.ALL);
        if (res == null)
        {
            return;
        }
        if (res.getCount() == 0)
        {
            return;
        }

        ArrayList<String> recipeList = new ArrayList<>();
        while (res.moveToNext())
        {
            String buffer =
                    String.format("ID: %s", res.getString(0)) +
                    String.format("Name: %s", res.getString(1)) +
                    String.format("Overview: %s", res.getString(2));
            recipeList.add(buffer);
        }

        RecyclerView recyclerView = findViewById(R.id.recipe_list);
        for (String rec : recipeList)
        {
            recyclerView.
        }

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
