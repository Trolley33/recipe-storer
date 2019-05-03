package com.example.recipekeeper;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class MethodAdapter extends RecyclerView.Adapter<MethodAdapter.ViewHolder> {
    private List<Method> methods;
    private RecipeMethodsFragment fragment;

    MethodAdapter(List<Method> _methods, RecipeMethodsFragment _fragment) {
        methods = _methods;
        fragment = _fragment;
    }

    /**
     * Called when view holder is created.
     * @param parent
     * @param i
     * @return
     */
    @NonNull
    @Override
    public MethodAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate view with custom layout.
        View methodView = inflater.inflate(R.layout.item_method, parent, false);

        return new ViewHolder(methodView);
    }

    /**
     * When this is bound to it's parent
     */
    @Override
    public void onBindViewHolder(@NonNull final MethodAdapter.ViewHolder viewHolder, int i) {
        // Get step represented by this adapter.
        final Method method = methods.get(i);

        // Retrieve UI elements from parent.
        TextView posTextView = viewHolder.posTextView;
        TextView stepTextView = viewHolder.stepTextView;
        TextView timeTextView = viewHolder.timeTextView;
        Button edit_button = viewHolder.editButton;
        Button delete_button = viewHolder.deleteButton;

        // Set values of text view to match this steps information.
        posTextView.setText(String.format("%d.", i + 1));
        stepTextView.setText(method.getStep());
        timeTextView.setText(String.format("%.2f mins", method.getTime()));

        // Bind onclick of edit button to method.
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMethod(viewHolder.itemView, method);
            }
        });

        // Bind onclick of delete button to method.
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMethod(viewHolder.itemView, method);
            }
        });
    }
    /**
     * Creates popup for editing step information.
     */
    private void editMethod(View view, final Method method) {
        // Open popup dialog.
        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Edit Method");

        // Inflate view with custom layout.
        View v = View.inflate(view.getContext(), R.layout.add_method_popup, null);
        builder.setView(v);

        // Get text input fields from view.
        final EditText step_input = v.findViewById(R.id.step_edit);
        final EditText time_input = v.findViewById(R.id.time_edit);

        // Set values to pre-existing ones.
        step_input.setText(method.getStep());
        time_input.setText(String.format("%.2f", method.getTime()));

        // Bind 'save' button to update information about step, and refresh the parent fragment.
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                method.setStep(step_input.getText().toString());
                // Double parsing safe here as time_input is a numerical input.
                method.setTime(Double.parseDouble(time_input.getText().toString()));
                fragment.refreshMethods(null);
            }
        });

        // Bind 'cancel' button to cancel dialog.
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * Creates popup for confirming deletion..
     */
    private void deleteMethod(View view, final Method method) {
        // Open popup dialog.
        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Delete Method");
        builder.setMessage("This cannot be undone.");

        // Bind 'delete' button to remove step from database, and refresh parent fragment.
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                method.delete();
                fragment.refreshMethods(null);
            }
        });
        // Bind 'cancel' button to a cancel dialog.
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * Number of items this adapter will display.
     * @return size of step list.
     */
    @Override
    public int getItemCount() {
        return methods.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layout;
        TextView posTextView;
        TextView stepTextView;
        TextView timeTextView;
        Button editButton;
        Button deleteButton;
        Context context;

        ViewHolder(final View itemView) {

            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            posTextView = itemView.findViewById(R.id.method_position);
            stepTextView = itemView.findViewById(R.id.method_step);
            timeTextView = itemView.findViewById(R.id.method_time);
            editButton = itemView.findViewById(R.id.method_edit_button);
            deleteButton = itemView.findViewById(R.id.method_delete_button);
            context = itemView.getContext();
        }
    }
}

