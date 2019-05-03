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

    /**
     * When activity is created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set content to custom layout.
        setContentView(R.layout.activity_recipe_viewer);

        // Retrieve selected recipe ID from intent.
        Intent intent = getIntent();
        int id = Integer.parseInt(intent.getExtras().get(HomeActivity.RECIPE_ID_MESSAGE).toString());

        // Get Recipe object from selected ID.
        selectedRecipe = Recipe.getFromID(id);

        // Setup custom action bar.
        Toolbar toolbar = findViewById(R.id.action_bar);
        toolbar.setTitle(selectedRecipe.getName());
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Get viewpager object, and bind pager adapter.
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Get tablayout and add listeners.
        TabLayout tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * Sets value of share string to custom text.
     */
    public void setShareValues() {
        myShareIntent = new Intent(Intent.ACTION_SEND);
        myShareIntent.setType("text/plain");
        myShareIntent.putExtra(Intent.EXTRA_TEXT, getShareText());
        if (myShareActionProvider != null) {
            myShareActionProvider.setShareIntent(myShareIntent);
        }
    }

    /**
     * Generates formatted string for sharing this recipe.
     * @return shareable recipe string.
     */
    public String getShareText() {
        // Get the ingredient and method lists.
        List<Ingredient> ingredients = Ingredient.getIngredientList(selectedRecipe.getID());
        List<Method> steps = Method.getMethodList(selectedRecipe.getID());

        // Format with recipe title first.
        StringBuilder result = new StringBuilder();
        result.append(String.format("Recipe: %s\n\n", selectedRecipe.getName()));

        // Ingredient header.
        result.append("Ingredients:\n");
        // Loop over each ingredient and print as list of bullet points.
        for (Ingredient ingredient : ingredients) {
            result.append(String.format("• %s - %s\n", ingredient.getDescription(), ingredient.getAmount()));
        }

        // Method header.
        result.append("\nMethod:\n");
        // Loop over each step and print as list of numbers points.
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
        // Set share action provider from menu.
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
            // If edit button pressed, run edit method.
            case R.id.edit_name:
                editPressed();
                break;
            // If delete button pressed, run delete method.
            case R.id.delete:
                deletePressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates popup for entering new recipe name.
     */
    void editPressed() {
        // Open popup dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter New Recipe Title");

        // Text input field, set to current recipe's name.
        final EditText input = new EditText(this);
        input.setText(selectedRecipe.getName());
        builder.setView(input);

        // Bind 'done' button to update recipe's name, and update toolbar text.
        builder.setPositiveButton("Set Name", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString();
                selectedRecipe.setName(newName);
                getSupportActionBar().setTitle(newName);
                // Update share content.
                setShareValues();
            }
        });

        // Bind the cancel button to cancel the dialog.
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * Creates popup for confirming deletion.
     */
    void deletePressed() {
        // Open popup dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Really Delete Recipe?");
        builder.setMessage("This cannot be undone.");

        // Bind 'delete' button to remove recipe from database, and switch back to the main activity.
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedRecipe.delete();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

        // Bind the cancel button to cancel the dialog.
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    // Create new fragment (which has to have an empty constructor).
                    RecipeOverviewFragment view_fragment = new RecipeOverviewFragment();
                    // Set recipe specific variables for this fragment.
                    view_fragment.setSelectedRecipe(selectedRecipe);
                    view_fragment.setMethod(Method.getMethodList(selectedRecipe.getID()));
                    view_fragment.setCategories(selectedRecipe.getCategories());
                    view_fragment.setParent(RecipeActivity.this);
                    return view_fragment;

                case 1:
                    // Create new fragment (which has to have an empty constructor).
                    RecipeIngredientsFragment ingredients_fragment = new RecipeIngredientsFragment();
                    // Set recipe specific variables for this fragment.
                    ingredients_fragment.setSelectedRecipe(selectedRecipe);
                    ingredients_fragment.setParent(RecipeActivity.this);
                    return ingredients_fragment;

                case 2:
                    // Create new fragment (which has to have an empty constructor).
                    RecipeMethodsFragment methods_fragment = new RecipeMethodsFragment();
                    // Set recipe specific variables for this fragment.
                    methods_fragment.setSelectedRecipe(selectedRecipe);
                    methods_fragment.setParent(RecipeActivity.this);
                    return methods_fragment;
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
