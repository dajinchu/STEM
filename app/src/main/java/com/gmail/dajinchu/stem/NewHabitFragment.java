package com.gmail.dajinchu.stem;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by Da-Jin on 11/25/2015.
 */
public class NewHabitFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener, DayOfWeekPicker.OnDaysOfWeekPickedListener {

    //use ID_NEW_HABIT as habit ID to specify creating new habit
    public static final int ID_NEW_HABIT = -1;

    private EditText nameEditText;
    private TextInputLayout nameTextInputLayout;
    private Habit habit;

    private TextView timeTextView;
    private DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT);
    private TextView dayweekTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int habitId = ID_NEW_HABIT;
        Bundle bundle = this.getArguments();
        if(bundle!=null){
            habitId = bundle.getInt("habitId",ID_NEW_HABIT);
            Log.d("NewHabitFragment","habitId: "+habitId);
        }
        if(habitId != ID_NEW_HABIT){
            habit = Habit.findById(Habit.class, habitId);
        }else{
            habit = new Habit();
        }
        //Make it full screen, in the future, we can have smarter options that can make it a
        //  true dialog depending on screen size/orientation
        setStyle(DialogFragment.STYLE_NO_TITLE,R.style.AppTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Get rid of the dialog title, we have our own toolbar stuff.
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //Inflate the fragment early so we can call findViewById on things inside the fragment
        View view = inflater.inflate(R.layout.fragment_new_habit, container, false);

        //Get the fragment toolbar(NOT the activity toolbar)
        Toolbar toolbar = ((Toolbar)view.findViewById(R.id.new_habit_toolbar));
        //add the save menu button to the toolbar
        toolbar.inflateMenu(R.menu.save_menu);
        //set toolbar title using string resource
        toolbar.setTitle(R.string.new_habit);
        //Set up the toolbar's navigation icon and behavior
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close(Activity.RESULT_CANCELED);
            }
        });
        // Set the listener for the menu click, namely the "save" button
        view.findViewById(R.id.new_habit_menu_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        //For use during saving
        //Name
        nameEditText = (EditText) view.findViewById(R.id.habit_name_edit_text);
        nameEditText.setText(habit.getName());

        //Time of day
        timeTextView = (TextView) view.findViewById(R.id.time_text_view);
        updateTimeTextView();
        view.findViewById(R.id.time_to_do).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar timeToDo = habit.getTimeToDo();
                TimePickerDialog picker = TimePickerDialog.newInstance(NewHabitFragment.this,
                        timeToDo.get(Calendar.HOUR_OF_DAY),
                        timeToDo.get(Calendar.MINUTE),
                        timeToDo.get(Calendar.SECOND),
                        false);
                picker.show(getActivity().getFragmentManager(),"timepickerdialog");
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
                DialogFragment newFragment = DayOfWeekPicker.newInstance(NewHabitFragment.this,
                        habit.getDays());
                newFragment.show(ft, "dayweekdialog");
            }
        });
        dayweekTextView = (TextView) view.findViewById(R.id.repeat_textview);
        updateDayWeekTextView();

        nameTextInputLayout = (TextInputLayout)view.findViewById(R.id.habit_name_text_input_layout);
        nameTextInputLayout.setError(null);
        if (nameTextInputLayout.getChildCount() == 2)
            nameTextInputLayout.getChildAt(1).setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        Calendar timeToDo= habit.getTimeToDo();
        timeToDo.set(Calendar.HOUR_OF_DAY, hourOfDay);
        timeToDo.set(Calendar.MINUTE,minute);
        habit.setTimeToDo(timeToDo);
        updateTimeTextView();
    }

    private void updateTimeTextView(){
        timeTextView.setText(format.format(habit.getTimeToDo().getTime()));
    }
    private void updateDayWeekTextView(){
        String[] shortDayNames={"MON","TUE","WED","THU","FRI","SAT","SUN"};
        StringBuilder sb = new StringBuilder();
        int index=0;
        for(boolean day:habit.getDays()){
            if(day){
                sb.append(shortDayNames[index]);
                sb.append(" ");
            }
            index++;
        }
        dayweekTextView.setText(sb.toString());
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
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                habit.setName(nameEditText.getText().toString());
            }
            @Override
            protected Boolean doInBackground(Void... params) {
                Log.d("NewHabitFragment", "saving");
                if(habit.getName().isEmpty()){
                    return false;
                }
                habit.save();
                habit.updateNotification(getContext());
                return true;
            }
            @Override
            protected void onPostExecute(Boolean success) {
                if(!success){
                    if (nameTextInputLayout.getChildCount() == 2) {
                        nameTextInputLayout.getChildAt(1).setVisibility(View.VISIBLE);
                    }
                    nameTextInputLayout.setError("This field is required");
                }else{
                    close(Activity.RESULT_OK);
                }
            }
        }.execute();
    }

    private void close(int resultCode){
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode,null);
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onDaysOfWeekPicked(boolean[] daysPicked) {
        habit.setDays(daysPicked);
        updateDayWeekTextView();
    }
}