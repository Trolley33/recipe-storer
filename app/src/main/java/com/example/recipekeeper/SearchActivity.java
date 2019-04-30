package com.example.recipekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class SearchActivity extends AppCompatActivity {
    private String term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        term = intent.getExtras().get(HomeActivity.SEARCH_MESSAGE).toString();

        setContentView(R.layout.activity_search);
        // Setup action bar
        Toolbar toolbar = findViewById(R.id.action_bar);
        toolbar.setTitle(String.format("Searching for: %s", term));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public String getSearchTerm() {
        return term;
    }

    /**
     * Add custom menu items to action bar.
     *
     * @param menu automatically generated menu
     * @return if action was successful.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
