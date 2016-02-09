package com.gmail.dajinchu.stem;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Da-Jin on 2/8/2016.
 */
public class BackupAlarmAdapter extends RecyclerView.Adapter<BackupAlarmAdapter.ViewHolder>{

    private final String[] choices;
    private int selectedPos = 0;

    public BackupAlarmAdapter(String[] choices, int initialselction){
        this.choices = choices;
        setSelectedPos(initialselction);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.backup_alarm_time_choice, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(choices[position]);
        Log.d("Bacukup",choices[position]);
        if(selectedPos == position){
            holder.itemView.setBackgroundColor(Color.parseColor("#EEEEEE"));
        }else{
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return choices.length;
    }

    public int getSelectedPos(){
        return selectedPos;
    }

    public void setSelectedPos(int pos) {
        selectedPos = pos;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.alarm_choice_text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            notifyItemChanged(selectedPos);
            selectedPos = getLayoutPosition();
            notifyItemChanged(selectedPos);
        }
    }
}
