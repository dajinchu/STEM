package com.gmail.dajinchu.stem.view;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.gmail.dajinchu.stem.dialogs.BackupAlarmDialog;
import com.gmail.dajinchu.stem.dialogs.DayOfWeekPicker;
import com.gmail.dajinchu.stem.dialogs.ImplementationIntentionDialog;
import com.gmail.dajinchu.stem.model.Routine;
import com.gmail.dajinchu.stem.R;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by Da-Jin on 11/25/2015.
 */
public class NewRoutineFragment extends DialogFragment implements
        TimePickerDialog.OnTimeSetListener,
        DayOfWeekPicker.OnDaysOfWeekPickedListener,
        ImplementationIntentionDialog.OnSetIntentionListener, BackupAlarmDialog.OnSetAlarmListener {

    //use ID_NEW_ROUTINE as routine ID to specify creating new routine
    public static final int ID_NEW_ROUTINE = -1;

    private Routine routine;

    private TextView timeTextView;
    private DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT);
    private TextView dayweekTextView;
    private TextView iiTextView;
    private TextView backupTextView;
    private boolean newroutine = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int routineId = ID_NEW_ROUTINE;
        Bundle bundle = this.getArguments();
        if(bundle!=null){
            routineId = bundle.getInt("routineId", ID_NEW_ROUTINE);
        }
        if(routineId != ID_NEW_ROUTINE){
            routine = Routine.findById(Routine.class, routineId);
        }else{
            newroutine = true;
            routine = new Routine();
        }
        //Make it full screen, in the future, we can have smarter options that can make it a
        //  true dialog depending on screen size/orientation
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Get rid of the dialog title, we have our own toolbar stuff.
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //Inflate the fragment early so we can call findViewById on things inside the fragment
        View view = inflater.inflate(R.layout.fragment_new_routine, container, false);

        //Get the fragment toolbar(NOT the activity toolbar)
        Toolbar toolbar = ((Toolbar)view.findViewById(R.id.new_routine_toolbar));
        //add the save menu button to the toolbar
        toolbar.inflateMenu(R.menu.save_menu);
        //set toolbar title using string resource
        toolbar.setTitle(R.string.set_routine);
        //Set up the toolbar's navigation icon and behavior
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        // Set the listener for the menu click, namely the "save" button
        view.findViewById(R.id.new_routine_menu_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        //Name
        iiTextView = (TextView) view.findViewById(R.id.ii_text_view);
        updateIITextView();
        view.findViewById(R.id.ii_layout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openIIDialog();
            }
        });

        //Time of day
        timeTextView = (TextView) view.findViewById(R.id.time_text_view);
        updateTimeTextView();
        view.findViewById(R.id.time_to_do).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar timeToDo = routine.getTimeToDo();
                TimePickerDialog picker = TimePickerDialog.newInstance(NewRoutineFragment.this,
                        timeToDo.get(Calendar.HOUR_OF_DAY),
                        timeToDo.get(Calendar.MINUTE),
                        timeToDo.get(Calendar.SECOND),
                        false);
                picker.show(getActivity().getFragmentManager(),"timepickerdialog");
            }
        });

        //Backup Alarm
        backupTextView = (TextView) view.findViewById(R.id.backup_alarm_text_view);
        updateBackupTextView();
        view.findViewById(R.id.back_up_alarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("backupdialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                BackupAlarmDialog newFragment = BackupAlarmDialog.newInstance(NewRoutineFragment.this,
                        Routine.minuteIntToString(routine.getBackupMinutes()));
                newFragment.show(ft, "backupdialog");
            }
        });

        //Repeat Pattern
        view.findViewById(R.id.repeat_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dayweekdialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                DialogFragment newFragment = DayOfWeekPicker.newInstance(NewRoutineFragment.this,
                        routine.getDays());
                newFragment.show(ft, "dayweekdialog");
            }
        });
        dayweekTextView = (TextView) view.findViewById(R.id.repeat_textview);
        updateDayWeekTextView();


        //Open Intention dialog if it's a new routine
        if(newroutine)openIIDialog();
        return view;
    }

    private void openIIDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("iidialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        //create and show
        ImplementationIntentionDialog ii = ImplementationIntentionDialog.newInstance(NewRoutineFragment.this,
                routine.getName(), routine.getRelativity(), routine.getCue());
        ii.show(ft,"iidialog");
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        Calendar timeToDo= routine.getTimeToDo();
        timeToDo.set(Calendar.HOUR_OF_DAY, hourOfDay);
        timeToDo.set(Calendar.MINUTE,minute);
        routine.setTimeToDo(timeToDo);
        updateTimeTextView();
    }

    @Override
    public void onDaysOfWeekPicked(boolean[] daysPicked) {
        routine.setDays(daysPicked);
        updateDayWeekTextView();
    }

    @Override
    public void onImplementationIntentionSet(String name, String relativity, String cue) {
        Log.d("NewRoutineFragment",name+" "+relativity+" "+cue);
        routine.setName(name);
        routine.setRelativity(relativity);
        routine.setCue(cue);
        updateIITextView();
    }
    @Override
    public void onMinutesPicked(String minutes) {
        routine.setBackupMinutes(Routine.minuteStringToInt(minutes));
        updateBackupTextView();
    }

    private void updateTimeTextView(){
        timeTextView.setText(format.format(routine.getTimeToDo().getTime()));
    }
    private void updateIITextView() {
        iiTextView.setText(routine.getName()+" "+routine.getRelativity()+" "+routine.getCue());
    }
    private void updateDayWeekTextView(){
        String[] shortDayNames={"MON","TUE","WED","THU","FRI","SAT","SUN"};
        StringBuilder sb = new StringBuilder();
        int index=0;
        for(boolean day:routine.getDays()){
            if(day){
                sb.append(shortDayNames[index]);
                sb.append(" ");
            }
            index++;
        }
        dayweekTextView.setText(sb.toString());
    }
    private void updateBackupTextView(){
        backupTextView.setText(Routine.minuteIntToString(routine.getBackupMinutes())+" after reminder");
    }

    @Override
    public void onResume() {
        super.onResume();
        TimePickerDialog picker = (TimePickerDialog)getActivity().getFragmentManager().findFragmentByTag("timepickerdialog");
        if(picker != null){
            picker.setOnTimeSetListener(this);
        }
    }

    public void save() {
        //TODO handle empty name
        routine.save();
        routine.updateNotification(getContext());
        close();
    }

    private void close(){
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

}