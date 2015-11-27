package com.gmail.dajinchu.stem;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by Da-Jin on 11/25/2015.
 */
public class NewHabitFragment extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        toolbar.setTitle(R.string.new_habit);
        //Set up the toolbar's navigation icon and behavior
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        // Set the listener for the menu click, namely the "save" button
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the menu item
                return true;
            }
        });
        //add the save menu button to the toolbar
        toolbar.inflateMenu(R.menu.save_menu);


        Spinner frequencySpinner = (Spinner)view.findViewById(R.id.habit_frequency_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.frequency, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(adapter);
        return view;
    }
}