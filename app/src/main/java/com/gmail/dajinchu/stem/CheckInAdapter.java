package com.gmail.dajinchu.stem;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Da-Jin on 11/28/2015.
 */
public class CheckInAdapter extends RecyclerView.Adapter<CheckInAdapter.ViewHolder>{

    private static CheckInFragment host;
    private static ArrayList<Habit> dataset;//TODO this should not be static
    private DateFormat format = DateFormat.getTimeInstance();

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name,frequency;
        public ViewHolder(View v){
            super(v);
            name = (TextView) v.findViewById(R.id.habit_item_name);
            frequency = (TextView) v.findViewById(R.id.habit_item_frequency);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //TODO with all the actions and onclicks of this recyclerview spread out, MVP would really come in handy
            host.openHabitFragment(dataset.get(getAdapterPosition()).getId());
        }
    }

    public CheckInAdapter(ArrayList<Habit> mDataset, CheckInFragment host){
        dataset = mDataset;
        CheckInAdapter.host = host;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CheckInAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.habit_checkbox, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //v.setBackgroundResource(R.mipmap.ic_launcher);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Habit habit = dataset.get(position);
        holder.name.setText(habit.name);
        holder.frequency.setText(format.format(new Date(habit.timeToDo)));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
