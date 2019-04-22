package com.example.recipekeeper;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * A fragment representing a list of Items.
 * <p/>

 * interface.
 */
public class RecipeOverviewFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private Recipe selectedRecipe;
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private ArrayList<Category> categories;
    private ArrayList<Method> method;

    RecipeActivity parent;

    public RecipeOverviewFragment() {
    }

    public static RecipeOverviewFragment newInstance(int columnCount) {
        RecipeOverviewFragment fragment = new RecipeOverviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public void setCategories(ArrayList<Category> _categories)
    {
        categories = _categories;
    }

    public void setMethod(ArrayList<Method> _method)
    {
        method = _method;
    }

    public void setSelectedRecipe(Recipe recipe)
    {
        selectedRecipe = recipe;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        createNotificationChannels();
        final View view = inflater.inflate(R.layout.fragment_recipe_overview, container, false);
        final Context context = view.getContext();

        // Get time display
        final TextView time_display = view.findViewById(R.id.time);

        // Calculate time for recipe to finish.
        double t = 0;
        for (Method step : method) {
            t += step.getTime();
        }

        time_display.setText(String.format("Time: %.2f (mins)", t));

        // Get category display.
        final TextView categories_content = view.findViewById(R.id.categories_content);
        // No categories -> None
        if (categories.size() == 0) {
            categories_content.setText("None");
        }
        else
        {
            // <= 3 categories -> C1, C2, C3
            if (categories.size() <= 3) {
                StringBuilder text = new StringBuilder();
                for (int i = 0; i < categories.size(); i++) {
                    text.append(categories.get(i).getName());
                    if (i != categories.size() - 1)
                    {
                        text.append(", ");
                    }
                }
                categories_content.setText(text.toString());
            }
            // > 3 categories -> C1, C2, C3, +X more
            else {
                StringBuilder text = new StringBuilder();
                for (int i = 0; i < 3; i++) {
                    text.append(categories.get(i).getName()).append(", ");
                }
                categories_content.setText(String.format("%s +%d more", text.toString(), categories.size() - 3));
            }
        }

        // Overview text section, and overview edit text section.
        final TextView overviewTextView = view.findViewById(R.id.overview_text);
        final EditText overviewEditText = view.findViewById(R.id.overview_edit);

        // Edit button and save button.
        final FloatingActionButton edit_fab = view.findViewById(R.id.edit_fab);
        final FloatingActionButton save_fab = view.findViewById(R.id.save_fab);

        overviewTextView.setText(selectedRecipe.getOverview());
        overviewEditText.setText(selectedRecipe.getOverview());

        overviewTextView.setVisibility(View.VISIBLE);
        overviewEditText.setVisibility(View.INVISIBLE);

        edit_fab.setVisibility(View.VISIBLE);
        save_fab.setVisibility(View.INVISIBLE);

        edit_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Hide text box, show edit box.
                overviewTextView.setVisibility(View.INVISIBLE);
                overviewEditText.setVisibility(View.VISIBLE);

                categories_content.setTypeface(null, Typeface.ITALIC);
                categories_content.setTextColor(Color.CYAN);
                categories_content.setBackgroundResource(R.drawable.ripple_effect);

                categories_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editCategories(v);
                    }
                });

                // Grab focus onto overview edit field.
                if (overviewEditText.requestFocus())
                {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(overviewEditText, InputMethodManager.SHOW_IMPLICIT);
                }
                // Hide edit button, show save button.
                edit_fab.setVisibility(View.INVISIBLE);
                save_fab.setVisibility(View.VISIBLE);
            }
        });

        save_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Hide edit box, show text box.
                overviewTextView.setVisibility(View.VISIBLE);
                overviewEditText.setVisibility(View.INVISIBLE);

                categories_content.setTypeface(null, Typeface.BOLD);
                categories_content.setTextColor(Color.parseColor("#b3ffffff"));
                categories_content.setBackgroundResource(R.color.colorPrimary);

                categories_content.setOnClickListener(null);

                // Hide save button, show edit button.
                edit_fab.setVisibility(View.VISIBLE);
                save_fab.setVisibility(View.INVISIBLE);

                // Get new overview text, and save it to database.
                String overview = overviewEditText.getText().toString();
                overviewTextView.setText(overview);
                selectedRecipe.setOverview(overview);

                setCategories(selectedRecipe.getCategories());
                refreshText(view);
            }
        });
        final double time = t;
        time_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer(time);
            }
        });
        return view;
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel",
                    "Recipe Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription("Recipe Notification Channel");

            NotificationManager manager = getContext().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    void startTimer(double minutes) {
        Intent intent = new Intent(getContext(), TimerService.class);
        intent.putExtra("SECONDS", (int) (minutes*60));
        getContext().startService(intent);
    }

    void editCategories (View root)
    {
        final View r = root;
        final AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());

        setCategories(selectedRecipe.getCategories());

        builder.setTitle("Choose categories");

        ArrayList<Category> allCategories = Category.getCategoryList();

        final String[] cat_strings = new String[allCategories.size()];
        final int[] cat_ids = new int[allCategories.size()];
        final boolean[] cat_bools = new boolean[allCategories.size()];

        for (int i = 0; i < allCategories.size(); i++)
        {
            cat_strings[i] = allCategories.get(i).getName();
            cat_ids[i] = allCategories.get(i).getID();
            for (int j = 0; j < categories.size(); j++)
            {
                // Log.e("Recipe:",
                if (categories.get(j).getID() == allCategories.get(i).getID())
                {
                    cat_bools[i] = true;
                    break;
                }
            }
        }

        builder.setMultiChoiceItems(cat_strings, cat_bools, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

            }
        });

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < cat_strings.length; i++)
                {
                    if (cat_bools[i])
                    {
                        selectedRecipe.addCategory(cat_ids[i]);
                    }
                    else
                    {
                        selectedRecipe.removeCategory(cat_ids[i]);
                    }
                }
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

    void refreshText(View v)
    {
        // Get category display.
        final TextView categories_content = v.findViewById(R.id.categories_content);
        // No categories -> None
        if (categories.size() == 0) {
            categories_content.setText("None");
        }
        else
        {
            // <= 3 categories -> C1, C2, C3
            if (categories.size() <= 3) {
                StringBuilder text = new StringBuilder();
                for (Category c : categories) {
                    text.append(c.getName()).append(", ");
                }
                categories_content.setText(text.toString());
            }
            // > 3 categories -> C1, C2, C3, +X more
            else {
                StringBuilder text = new StringBuilder();
                for (int i = 0; i < 3; i++) {
                    text.append(categories.get(i).getName()).append(", ");
                }
                categories_content.setText(String.format("%s +%d more", text.toString(), categories.size() - 3));
            }
        }
    }

    void setParent(RecipeActivity _parent) {
        parent = _parent;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }
}
