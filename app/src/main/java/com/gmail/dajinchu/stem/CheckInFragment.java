package com.gmail.dajinchu.stem;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Da-Jin on 11/25/2015.
 */
public class CheckInFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);
        ((Toolbar)getActivity().findViewById(R.id.my_toolbar)).setNavigationIcon(null);
        getActivity().findViewById(R.id.fab).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                DialogFragment newFragment = new NewHabitFragment();
                newFragment.show(ft, "dialog");
            }
        });
        return inflater.inflate(R.layout.fragment_check_in,container,false);
    }
}