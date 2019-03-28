package com.example.recipekeeper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class CategoryListActivity extends AppCompatActivity
{
    Category selectedCategory;

    private RecyclerView recyclerView;
    private List<Recipe> recipeList;
    private RecipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_viewer);

        Intent intent = getIntent();
        int id = Integer.parseInt(intent.getExtras().get(HomeActivity.CATEGORY_ID_MESSAGE).toString());


        selectedCategory = Category.getFromID(id);

        // Setup action bar
        Toolbar toolbar = findViewById(R.id.action_bar);
        toolbar.setTitle(selectedCategory.getName());
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recipeList = new ArrayList<>();
        adapter = new RecipeAdapter(recipeList);

        recyclerView = findViewById(R.id.recipe_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        refreshRecipes(null);
    }

    void refreshRecipes(View view)
    {
        recipeList.clear();
        ArrayList<Recipe> rList = selectedCategory.getRecipeList();
        recipeList.addAll(rList);

        adapter.notifyDataSetChanged();
    }

    /**
     * Add custom menu items to action bar.
     * @param menu automatically generated menu
     * @return if action was successful.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_menu, menu);
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
            case R.id.delete:
                deletePressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void deletePressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Really Delete Category?");
        builder.setMessage("This cannot be undone.");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedCategory.delete();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
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
