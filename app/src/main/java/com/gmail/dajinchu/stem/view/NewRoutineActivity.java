package com.gmail.dajinchu.stem.view;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.gmail.dajinchu.stem.R;
import com.gmail.dajinchu.stem.models.Routine;
import com.gmail.dajinchu.stem.view.dialogs.BackupAlarmAdapter;
import com.gmail.dajinchu.stem.view.dialogs.DayOfWeekAdapter;
import com.gmail.dajinchu.stem.view.dialogs.MiniTimePicker;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.Calendar;

/**
 * Created by Da-Jin on 4/27/2016.
 */
public class NewRoutineActivity extends Activity {

    private FragmentManager fm;

    public static abstract class StepFragment extends Fragment{
        public abstract void save(Routine routine);
    }

    public static class HabitInfoFragment extends StepFragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_habit_info, container, false);
        }

        @Override
        public void save(Routine r) {

        }
    }
    public static class InputRoutine extends StepFragment {
        private EditText name;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_input_routine, container, false);
            name = (EditText) view.findViewById(R.id.routine_input);
            return view;
        }
        @Override
        public void save(Routine r) {
            r.setName(name.getText().toString());
        }
    }
    public static class InputCue extends StepFragment{
        private EditText cue;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_input_cue, container, false);
            cue = ((EditText)view.findViewById(R.id.cue_input));
            return view;
        }

        @Override
        public void save(Routine r) {
            r.setCue(cue.getText().toString());
        }
    }
    public static class ViewImplementation extends StepFragment{
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_view_implementation,container,false);
        }

        @Override
        public void save(Routine r) {

        }
    }
    public static class ChooseDayOfWeek extends StepFragment{
        private boolean[] daysChecked;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_guided_day_week_picker,container,false);

            RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.days_of_week);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            daysChecked = new boolean[7];
            DayOfWeekAdapter adapter =  new DayOfWeekAdapter(getResources().getStringArray(R.array.days), daysChecked);

            recyclerView.setAdapter(adapter);
            return v;
        }

        @Override
        public void save(Routine r) {
            r.setDays(daysChecked);
        }
    }
    public static class ChooseNotifTime extends StepFragment implements TimePickerDialog.OnTimeSetListener {
        private MiniTimePicker picker;
        private int hour, minute;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_choose_notif_time, container, false);
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            picker = MiniTimePicker.newInstance(this, 0, 0, 0, false);
            picker.setOnTimeSetListener(this);
            ft.add(R.id.picker_frame, picker);
            ft.commit();
            /*
            view.findViewById(R.id.choose_time_container).setVisibility(View.GONE);

            RadialPickerLayout mTimePicker = (RadialPickerLayout) view.findViewById(R.id.radial_pick);
            mTimePicker.setOnValueSelectedListener(picker);
//            mTimePicker.setOnKeyListener(keyboardListener);
            mTimePicker.initialize(getActivity(), picker, new Timepoint(0), false);*/
            return view;
        }

        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
            this.hour = hourOfDay;
            this.minute = minute;
        }

        @Override
        public void save(Routine r) {
            picker.notifyOnDateListener();
            Calendar timeToDo = r.getTimeToDo();
            timeToDo.set(Calendar.HOUR_OF_DAY, hour);
            timeToDo.set(Calendar.MINUTE, minute);
            r.setTimeToDo(timeToDo);
        }
    }
    public static class BackupAlarm extends StepFragment {
        private BackupAlarmAdapter adapter;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_guided_backup_alarm,container,false);

            RecyclerView recycler = (RecyclerView)v.findViewById(R.id.back_up_alarm_recycler_options);
            recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
            recycler.setHasFixedSize(true);
            adapter = new BackupAlarmAdapter(Routine.possibleBackupChoices(), 0);
            recycler.setAdapter(adapter);
            return v;
        }

        @Override
        public void save(Routine r) {
            r.setBackupMinutes(Routine.minuteStringToInt(Routine.possibleBackupChoices()[adapter.getSelectedPos()]));
        }
    }


    StepFragment[] steps = new StepFragment[]{
            new HabitInfoFragment(),
            new InputRoutine(),
            new InputCue(),
            new ViewImplementation(),
            new ChooseDayOfWeek(),
            new ChooseNotifTime(),
            new BackupAlarm()
    };

    int currFragment = 0;
    Routine routine = new Routine();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            currFragment=savedInstanceState.getInt("curr");
        }

        setContentView(R.layout.activity_new_routine);

        Toolbar toolbar = ((Toolbar) findViewById(R.id.new_routine_toolbar));
        Button next = (Button) findViewById(R.id.new_routine_next_button);
        View dialogContainer = findViewById(R.id.new_routine_container);

        //set toolbar title using string resource
        toolbar.setTitle(R.string.new_routine);
        //Set up the toolbar's navigation icon and behavior
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //make button advance the fragments
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                steps[currFragment].save(routine);
                currFragment++;
                if(currFragment==steps.length){
                    routine.save();
                    finish();
                    return;
                }
                updateFragment();
            }
        });

        fm = getFragmentManager();
        updateFragment();


    }

    private void updateFragment(){
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.new_routine_container, steps[currFragment]);
        ft.commit();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curr",currFragment);
    }
}
