package com.example.recipekeeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

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
}
