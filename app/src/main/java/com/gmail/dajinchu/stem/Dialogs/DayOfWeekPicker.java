package com.gmail.dajinchu.stem.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.gmail.dajinchu.stem.R;

import org.solovyev.android.views.llm.LinearLayoutManager;

/**
 * Created by Da-Jin on 1/2/2016.
 */
public class DayOfWeekPicker extends DialogFragment {

    String[] days = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
    private boolean[] daysChecked = new boolean[7];
    private OnDaysOfWeekPickedListener listener;

    public interface OnDaysOfWeekPickedListener {
        void onDaysOfWeekPicked(boolean[] daysPicked);
    }
    public DayOfWeekPicker(){
    }
    private void initialize(OnDaysOfWeekPickedListener listener, boolean[] daysPicked){
        this.listener = listener;
        this.daysChecked = daysPicked;
    }

    public static DayOfWeekPicker newInstance(OnDaysOfWeekPickedListener listener, boolean[] daysPicked){
        DayOfWeekPicker instance = new DayOfWeekPicker();
        instance.initialize(listener, daysPicked);
        return instance;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialogWrapper.Builder builder = new AlertDialogWrapper.Builder(getActivity())
                .setTitle(R.string.repeatHint)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.onDaysOfWeekPicked(daysChecked);
                                dialog.dismiss();
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                );

        LayoutInflater i = getActivity().getLayoutInflater();

        View v = i.inflate(R.layout.fragment_day_week_picker,null);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.days_of_week);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DayOfWeekAdapter adapter = new DayOfWeekAdapter(days, daysChecked);

        recyclerView.setAdapter(adapter);

        builder.setView(v);
        return builder.create();
    }
}
