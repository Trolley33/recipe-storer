package com.example.recipekeeper;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.util.List;

public class RecipeViewActivity extends AppCompatActivity
{

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    Recipe selectedRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_viewer);

        Intent intent = getIntent();
        int id = Integer.parseInt(intent.getExtras().get(HomeActivity.RECIPE_ID_MESSAGE).toString());

        selectedRecipe = Recipe.getFromID(id);

        // Setup action bar
        Toolbar toolbar = findViewById(R.id.action_bar);
        toolbar.setTitle(selectedRecipe.getName());
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
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

    void showMessage (String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                case 0:
                    RecipeViewFragment fragment = new RecipeViewFragment();
                    fragment.setCategories(selectedRecipe.getCategories());
                    return fragment;

                case 1:
                    RecipeViewFragment fragment2 = new RecipeViewFragment();
                    fragment2.setCategories(selectedRecipe.getCategories());
                    return fragment2;
                case 2:
                    RecipeViewFragment fragment3 = new RecipeViewFragment();
                    fragment3.setCategories(selectedRecipe.getCategories());
                    return fragment3;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}