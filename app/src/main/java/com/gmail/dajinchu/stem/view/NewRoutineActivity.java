package com.gmail.dajinchu.stem.view;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.gmail.dajinchu.stem.R;
import com.gmail.dajinchu.stem.models.Routine;
import com.gmail.dajinchu.stem.view.dialogs.DayOfWeekAdapter;
import com.gmail.dajinchu.stem.view.dialogs.MiniTimePicker;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.solovyev.android.views.llm.LinearLayoutManager;

import java.lang.reflect.Field;
import java.util.Calendar;

/**
 * Created by Da-Jin on 4/27/2016.
 */
public class NewRoutineActivity extends Activity {

    public interface ReadyNextListener {
        public void ready();
        public void notReady();
    }

    public static abstract class StepFragment extends Fragment{
        protected Routine routine;
        private ReadyNextListener listener;
        public StepFragment initialize(Routine r, ReadyNextListener ready){
            routine = r;
            listener = ready;
            return this;
        }
        protected void ready(){
            listener.ready();
        }
        protected void notReady(){
            listener.notReady();
        }
        public abstract void save();
    }

    public static class HabitInfoFragment extends StepFragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_habit_info, container, false);
            final ExpandableRelativeLayout more = (ExpandableRelativeLayout) view.findViewById(R.id.habit_loop);
            view.findViewById(R.id.learn_more_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setVisibility(View.INVISIBLE);
                    more.expand();
                }
            });

            ready();
            return view;
        }

        @Override
        public void save() {

        }
    }
    public static class InputRoutine extends StepFragment {
        private EditText name;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_input_routine, container, false);
            name = (EditText) view.findViewById(R.id.routine_input);
            name.setInputType(InputType.TYPE_CLASS_TEXT);
            name.setText(routine.getName());
            name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.length()>0){
                        ready();
                    }else{
                        notReady();
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            notReady();
            return view;
        }
        @Override
        public void save() {
            routine.setName(name.getText().toString());
        }
    }
    public static class InputCue extends StepFragment{
        private EditText cue;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_input_cue, container, false);
            cue = ((EditText)view.findViewById(R.id.cue_input));
            cue.setInputType(InputType.TYPE_CLASS_TEXT);
            cue.setText(routine.getCue());
            cue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.length()>0){
                        ready();
                    }else{
                        notReady();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            notReady();
            return view;
        }

        @Override
        public void save() {
            routine.setCue(cue.getText().toString());
        }
    }
    public static class ViewImplementation extends StepFragment{
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_view_implementation, container, false);
            ((TextView)view.findViewById(R.id.view_intention)).setText("I will "+ routine.getName()+" after "+routine.getCue());

            ready();
            return view;
        }

        @Override
        public void save() {

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

            daysChecked = routine.getDays();
            DayOfWeekAdapter adapter =  new DayOfWeekAdapter(getResources().getStringArray(R.array.days), daysChecked);

            recyclerView.setAdapter(adapter);

            ready();
            return v;
        }

        @Override
        public void save() {
            routine.setDays(daysChecked);
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
            Calendar timeToDo = routine.getTimeToDo();
            picker = MiniTimePicker.newInstance(this, timeToDo.get(Calendar.HOUR_OF_DAY), timeToDo.get(Calendar.MINUTE), false);
            picker.setOnTimeSetListener(this);
            ft.add(R.id.picker_frame, picker);
            ft.commit();

            ready();
            /*
            view.findViewById(R.id.choose_time_container).setVisibility(View.GONE);

            RadialPickerLayout mTimePicker = (RadialPickerLayout) view.findViewById(R.id.radial_pick);
            mTimePicker.setOnValueSelectedListener(picker);
//            mTimePicker.setOnKeyListener(keyboardListener);
            mTimePicker.initialize(getActivity(), picker, new Timepoint(0), false);*/
            return view;
        }
        @Override
        public void onDetach() {
            super.onDetach();

            try {
                Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
                childFragmentManager.setAccessible(true);
                childFragmentManager.set(this, null);

            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
            this.hour = hourOfDay;
            this.minute = minute;
        }

        @Override
        public void save() {
            picker.notifyOnDateListener();
            Calendar timeToDo = routine.getTimeToDo();
            timeToDo.set(Calendar.HOUR_OF_DAY, hour);
            timeToDo.set(Calendar.MINUTE, minute);
            routine.setTimeToDo(timeToDo);
        }
    }
    public static class BackupAlarm extends StepFragment implements TimePickerDialog.OnTimeSetListener {
        private MiniTimePicker picker;
        private int hour, minute;
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_guided_backup_alarm,container,false);
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            Calendar backupTime = routine.getBackupTime();
            picker = MiniTimePicker.newInstance(this, backupTime.get(Calendar.HOUR_OF_DAY), backupTime.get(Calendar.MINUTE), false);
            picker.setOnTimeSetListener(this);
            ft.add(R.id.picker_frame_backup, picker);
            ft.commit();

            ready();
            return v;
        }
        @Override
        public void onDetach() {
            super.onDetach();

            try {
                Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
                childFragmentManager.setAccessible(true);
                childFragmentManager.set(this, null);

            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
            this.hour = hourOfDay;
            this.minute = minute;
        }

        @Override
        public void save() {
            picker.notifyOnDateListener();
            Calendar backup = routine.getBackupTime();
            backup.set(Calendar.HOUR_OF_DAY, hour);
            backup.set(Calendar.MINUTE, minute);
            routine.setBackupTime(backup);
        }
    }


    int currFragment = 0;
    Routine routine = new Routine();
    StepFragment[] steps = new StepFragment[]{
            new HabitInfoFragment(),
            new InputRoutine(),
            new InputCue(),
            new ViewImplementation(),
            new ChooseDayOfWeek(),
            new ChooseNotifTime(),
            new BackupAlarm()
    };
    private FragmentManager fm;
    private View prev, next;
    private ImageView nextImg;
    private TextView nextText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            currFragment=savedInstanceState.getInt("curr");
        }
        for(StepFragment frag : steps){
            frag.initialize(routine, new ReadyNextListener() {
                @Override
                public void ready() {
                    nextText.setTextColor(Color.argb(255,255,255,255));
                    nextImg.setImageAlpha(255);
                    next.setClickable(true);
                }

                @Override
                public void notReady() {
                    nextText.setTextColor(Color.argb((int) (.4*255),255,255,255));
                    nextImg.setImageAlpha((int) (.4*255));
                    next.setClickable(false);
                }
            });
        }

        setContentView(R.layout.activity_new_routine);

        Toolbar toolbar = ((Toolbar) findViewById(R.id.new_routine_toolbar));
        next = findViewById(R.id.new_routine_next_button);
        prev = findViewById(R.id.new_routine_prev_button);
        nextText = (TextView) findViewById(R.id.new_next);
        nextImg = (ImageView) findViewById(R.id.new_next_img);

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
                steps[currFragment].save();
                currFragment++;
                if(currFragment==steps.length){
                    routine.save();
                    finish();
                    return;
                }
                updateFragment();
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                steps[currFragment].save();
                currFragment--;
                updateFragment();
            }
        });

        fm = getFragmentManager();
        updateFragment();


    }

    private void updateFragment(){
        if(currFragment==0){
            prev.setVisibility(View.INVISIBLE);
        }else{
            prev.setVisibility(View.VISIBLE);
        }
        if(currFragment==steps.length-1){
            nextImg.setVisibility(View.GONE);
            nextText.setText(R.string.finish);
        }else{
            nextImg.setVisibility(View.VISIBLE);
            nextText.setText(R.string.next);
        }
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
