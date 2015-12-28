package com.gmail.dajinchu.stem;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by Da-Jin on 11/25/2015.
 */
public class NewHabitFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    //TODO need to start using UUID so that when we edit the name, UUID will collide and know to be replacing instead of making new.

    private EditText nameEditText;
    private TextInputLayout nameTextInputLayout;
    private Habit habit;

    Calendar calTimeToDo;//TODO this should be controller's job
    private TextView timeTextView;
    private DateFormat format = DateFormat.getTimeInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String habitId = "";
        Bundle bundle = this.getArguments();
        if(bundle!=null){
            habitId = bundle.getString("habitId","");
            Log.d("NewHabitFragment","habitId: "+habitId);
        }
        if(!habitId.isEmpty()){
            habit = Habit.getHabitFromId(habitId);
        }else{
            habit = Habit.createNewHabit();
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

        calTimeToDo = Calendar.getInstance();
        calTimeToDo.setTimeInMillis(habit.timeToDo);//TODO controller!

        //For use during saving
        //Name
        nameEditText = (EditText) view.findViewById(R.id.habit_name_edit_text);
        nameEditText.setText(habit.name);

        //Time of day
        timeTextView = (TextView) view.findViewById(R.id.time_to_do);
        timeTextView.setText(format.format(calTimeToDo.getTime()));
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calTimeToDo.setTimeInMillis(habit.timeToDo);
                TimePickerDialog picker = TimePickerDialog.newInstance(NewHabitFragment.this,
                        calTimeToDo.get(Calendar.HOUR_OF_DAY),
                        calTimeToDo.get(Calendar.MINUTE),
                        calTimeToDo.get(Calendar.SECOND),
                        false);
                picker.show(getActivity().getFragmentManager(),"timepickerdialog");
            }
        });

        //Days of week
        CheckBox monday = (CheckBox) view.findViewById(R.id.dayOfWeek);
        monday.setChecked(habit.daysOfTheWeek.equals("M"));
        monday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    habit.daysOfTheWeek="M";
                }else{
                    habit.daysOfTheWeek="";
                }
            }
        });

        nameTextInputLayout = (TextInputLayout)view.findViewById(R.id.habit_name_text_input_layout);
        nameTextInputLayout.setError(null);
        if (nameTextInputLayout.getChildCount() == 2)
            nameTextInputLayout.getChildAt(1).setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        calTimeToDo.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calTimeToDo.set(Calendar.MINUTE,minute);
        habit.timeToDo = calTimeToDo.getTimeInMillis();
        timeTextView.setText(format.format(calTimeToDo.getTime()));
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
                habit.name = nameEditText.getText().toString();
            }
            @Override
            protected Boolean doInBackground(Void... params) {
                Log.d("NewHabitFragment", "saving");
                if(habit.name.isEmpty()){
                    return false;
                }
                habit.save();
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
}