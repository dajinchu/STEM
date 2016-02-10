package com.gmail.dajinchu.stem.dialogs;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.gmail.dajinchu.stem.R;

/**
 * Created by Da-Jin on 1/2/2016.
 */
public class DayOfWeekAdapter extends RecyclerView.Adapter<DayOfWeekAdapter.ViewHolder> {

    private final String[] dayNames;
    private final boolean[] daysChecked;

    public DayOfWeekAdapter(String[] dayNames, boolean[] daysChecked){
        this.dayNames = dayNames;
        this.daysChecked = daysChecked;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_week_checkbox_holder,parent,false);
        return new ViewHolder(v, daysChecked);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.checkbox.setText(dayNames[position]);
        holder.checkbox.setChecked(daysChecked[position]);
    }

    @Override
    public int getItemCount() {
        return 7;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkbox;
        public ViewHolder(View itemView, final boolean[] daysChecked) {
            super(itemView);
            checkbox = (CheckBox) itemView.findViewById(R.id.day_check_box);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    daysChecked[getAdapterPosition()]=isChecked;
                }
            });
        }
    }
}
