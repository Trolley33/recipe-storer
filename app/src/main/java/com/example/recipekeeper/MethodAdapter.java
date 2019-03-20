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

public class MethodAdapter extends RecyclerView.Adapter<MethodAdapter.ViewHolder>
{
    private List<Method> methods;
    private RecipeMethodsFragment fragment;

    public MethodAdapter(List<Method> _methods, RecipeMethodsFragment _fragment)
    {
        methods = _methods;
        fragment = _fragment;
    }

    @NonNull
    @Override
    public MethodAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View methodView = inflater.inflate(R.layout.item_method, parent, false);

        ViewHolder viewHolder = new ViewHolder(methodView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MethodAdapter.ViewHolder viewHolder, int i) {
        final Method method = methods.get(i);

        TextView posTextView = viewHolder.posTextView;
        posTextView.setText(String.format("%d.", i+1));

        TextView stepTextView = viewHolder.stepTextView;
        stepTextView.setText(method.getStep());

        TextView timeTextView = viewHolder.timeTextView;
        timeTextView.setText(String.format("%.2f mins", method.getTime()));

        ConstraintLayout layout = viewHolder.layout;
        final Context context = viewHolder.context;

        Button edit_button = viewHolder.editButton;
        Button delete_button = viewHolder.deleteButton;

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMethod(viewHolder.itemView, method);
            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMethod(viewHolder.itemView, method);
            }
        });
    }

    public void editMethod(View view, final Method method)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Edit Method");

        View v = View.inflate(view.getContext(), R.layout.add_method_popup, null);

        builder.setView(v);

        final EditText step_input = v.findViewById(R.id.step_edit);
        final EditText time_input = v.findViewById(R.id.time_edit);

        step_input.setText(method.getStep());
        time_input.setText(String.format("%.2f", method.getTime()));

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                method.setStep(step_input.getText().toString());
                method.setTime(Double.parseDouble(time_input.getText().toString()));
                fragment.refreshMethods(null);
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

    public void deleteMethod(View view, final Method method)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Delete Method");

        builder.setMessage("This cannot be undone.");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                method.delete();
                fragment.refreshMethods(null);
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

    @Override
    public int getItemCount() {
        return methods.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layout;

        TextView posTextView;
        TextView stepTextView;
        TextView timeTextView;

        Button editButton;
        Button deleteButton;

        Context context;

        public ViewHolder(final View itemView) {

            super(itemView);
            context = itemView.getContext();

            layout =  itemView.findViewById(R.id.layout);

            posTextView = itemView.findViewById(R.id.method_position);
            stepTextView = itemView.findViewById(R.id.method_step);
            timeTextView = itemView.findViewById(R.id.method_time);

            editButton = itemView.findViewById(R.id.method_edit_button);
            deleteButton = itemView.findViewById(R.id.method_delete_button);
        }
    }
}

