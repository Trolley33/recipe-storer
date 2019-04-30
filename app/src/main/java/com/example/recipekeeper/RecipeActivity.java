package com.example.recipekeeper;

import android.content.DialogInterface;
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
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.List;

public class RecipeActivity extends AppCompatActivity {

    ShareActionProvider myShareActionProvider;
    Intent myShareIntent;
    Recipe selectedRecipe;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void setShareValues() {
        myShareIntent = new Intent(Intent.ACTION_SEND);
        myShareIntent.setType("text/plain");
        myShareIntent.putExtra(Intent.EXTRA_TEXT, getShareText());
        if (myShareActionProvider != null) {
            myShareActionProvider.setShareIntent(myShareIntent);
        }
    }

    public String getShareText() {
        List<Ingredient> ingredients = Ingredient.getIngredientList(selectedRecipe.getID());
        List<Method> steps = Method.getMethodList(selectedRecipe.getID());

        StringBuilder result = new StringBuilder();
        result.append(String.format("Recipe: %s\n\n", selectedRecipe.getName()));

        result.append("Ingredients:\n");
        for (Ingredient ingredient : ingredients) {
            result.append(String.format("• %s - %s\n", ingredient.getDescription(), ingredient.getAmount()));
        }

        result.append("\nMethod:\n");
        for (Method step : steps) {
            result.append(String.format("%d. %s - %.2f mins\n", step.getPosition() + 1, step.getStep(), step.getTime()));
        }

        return result.toString();
    }

    /**
     * Add custom menu items to action bar.
     *
     * @param menu automatically generated menu
     * @return if action was successful.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_menu, menu);
        MenuItem shareItem = menu.findItem(R.id.action_share);
        myShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        setShareValues();

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
            case R.id.edit_name:
                editPressed();
                break;
            case R.id.delete:
                deletePressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void editPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter New Recipe Title");

        final EditText input = new EditText(this);
        input.setText(selectedRecipe.getName());
        builder.setView(input);

        builder.setPositiveButton("Set Name", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString();
                selectedRecipe.setName(newName);
                getSupportActionBar().setTitle(newName);
                setShareValues();
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

    void deletePressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Really Delete Recipe?");
        builder.setMessage("This cannot be undone.");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedRecipe.delete();
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

    void showMessage(String title, String message) {
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
            switch (position) {
                case 0:
                    RecipeOverviewFragment view_fragment = new RecipeOverviewFragment();
                    view_fragment.setSelectedRecipe(selectedRecipe);
                    view_fragment.setMethod(Method.getMethodList(selectedRecipe.getID()));
                    view_fragment.setCategories(selectedRecipe.getCategories());
                    view_fragment.setParent(RecipeActivity.this);
                    return view_fragment;

                case 1:
                    RecipeIngredientsFragment ingredients_fragment = new RecipeIngredientsFragment();
                    ingredients_fragment.setSelectedRecipe(selectedRecipe);
                    ingredients_fragment.setParent(RecipeActivity.this);
                    return ingredients_fragment;

                case 2:
                    RecipeMethodsFragment methods_fragment = new RecipeMethodsFragment();
                    methods_fragment.setSelectedRecipe(selectedRecipe);
                    methods_fragment.setParent(RecipeActivity.this);
                    return methods_fragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
