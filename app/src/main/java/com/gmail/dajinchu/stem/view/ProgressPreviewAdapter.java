package com.gmail.dajinchu.stem.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.dajinchu.stem.R;
import com.gmail.dajinchu.stem.models.Routine;

import java.util.ArrayList;

/**
 * Created by Da-Jin on 1/20/2016.
 */
public class ProgressPreviewAdapter extends RecyclerView.Adapter<ProgressPreviewAdapter.ViewHolder>{
    private ArrayList<Routine> routines;

    public ProgressPreviewAdapter(ArrayList<Routine> allRoutines) {
        routines = allRoutines;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.progress_preview_holder,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Routine routine = routines.get(position);
        holder.bind(routine);
    }

    @Override
    public int getItemCount() {
        return routines.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, completionText;
        CircleDisplay percentCompletion;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.progress_preview_title);
            percentCompletion = (CircleDisplay) v.findViewById(R.id.circle);
            completionText = (TextView) v.findViewById(R.id.progress_preview_percent_text);
            percentCompletion.setTouchEnabled(false);
            percentCompletion.setAnimDuration(200);
            percentCompletion.setDrawText(false);
        }

        public void bind(Routine routine) {
            int percent = routine.successfulCompletions()*100/routine.getCompletions().size();
            title.setText(routine.getName());
            completionText.setText(percent+"% successful");
            percentCompletion.showValue(percent,100f,true);
        }
    }
}
