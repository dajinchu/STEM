package com.gmail.dajinchu.stem;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Da-Jin on 1/20/2016.
 */
public class ProgressPreviewAdapter extends RecyclerView.Adapter<ProgressPreviewAdapter.ViewHolder>{
    private ArrayList<Habit> habits;

    public ProgressPreviewAdapter(ArrayList<Habit> allHabits) {
        habits = allHabits;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.progress_preview_holder,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Habit habit = habits.get(position);
        holder.bind(habit);
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        CircleDisplay percentCompletion;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.progress_preview_title);
            percentCompletion = (CircleDisplay) v.findViewById(R.id.circle);
            percentCompletion.setTouchEnabled(false);
            percentCompletion.setAnimDuration(500);
            percentCompletion.setUnit("%");
            percentCompletion.setFormatDigits(0);
        }

        public void bind(Habit habit) {
            title.setText(habit.getName());
            percentCompletion.showValue(habit.successfulCompletions()/(float)habit.getCompletions().size()*100f,100f,true);
        }
    }
}
