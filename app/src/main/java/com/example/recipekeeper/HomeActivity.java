package com.example.recipekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class HomeActivity extends AppCompatActivity {

    // Extra string values
    public static final String RECIPE_ID_MESSAGE = "com.example.recipekeeper.extra.RECIPE_ID";
    public static final String SEARCH_MESSAGE = "com.example.recipekeeper.extra.SEARCH_STRING";
    public static final String CATEGORY_ID_MESSAGE = "com.example.recipekeeper.extra.CATEGORY_ID";

    /**
     * When activity is created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set content to custom layout.
        setContentView(R.layout.activity_home);

        // Setup custom action bar.
        Toolbar toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Get viewpager object, and bind pager adapter.
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Get tablayout and add listeners.
        TabLayout tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }

    /**
     * Add custom menu items to action bar.
     *
     * @param menu automatically generated menu
     * @return if action was successful.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        // Get search view from action bar menu.
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));

        // Add listeners for searching.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * When search is triggered.
             * @param term to search for.
             */
            @Override
            public boolean onQueryTextSubmit(String term) {
                // Create intent to go to search activity.
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                // Add the search string as an extra.
                intent.putExtra(SEARCH_MESSAGE, term);
                // Open search activity.
                startActivity(intent);
                return false;
            }

            /**
             * When the query is changed (by typing).
             * Not enabled.
             */
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Run method based on which menu item was selected.
     *
     * @param item menu item selected
     * @return if action was successful.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // When help icon pressed, run help function.
            case R.id.help:
                helpPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when help button is pressed.
     */
    void helpPressed() {
        // Create intent to go to user guide activity.
        Intent intent = new Intent(this, UserGuideActivity.class);
        // Open user guide.
        this.startActivity(intent);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Called to instantiate fragment for given page.
         * @param position of tab to instantiate.
         * @return fragment object.
         */
        @Override
        public Fragment getItem(int position) {
            // Return fragement based on tab position.
            switch (position) {
                case 0:
                    return new AllRecipesFragment();
                case 1:
                    return new CategoryFragment();
                case 2:
                    return new FavRecipesFragment();
            }
            return null;
        }

        /**
         * Number of tabs to show.
         * @return number of fragments.
         */
        @Override
        public int getCount() {
            return 3;
        }
    }
}
