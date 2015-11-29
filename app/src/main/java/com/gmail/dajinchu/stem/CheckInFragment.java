package com.gmail.dajinchu.stem;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Da-Jin on 11/25/2015.
 */
public class CheckInFragment extends Fragment {
    private CheckInAdapter adapter;
    ArrayList<String> habitList = new ArrayList<String>();

    public static final int NEW_HABIT_REQUEST_CODE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_in,container,false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.habit_check_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CheckInAdapter(habitList);
        recyclerView.setAdapter(adapter);

        getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);
        ((Toolbar)getActivity().findViewById(R.id.my_toolbar)).setNavigationIcon(null);
        getActivity().findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
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
                newFragment.setTargetFragment(CheckInFragment.this, NEW_HABIT_REQUEST_CODE);
                newFragment.show(ft, "dialog");
            }
        });
        loadHabits();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case NEW_HABIT_REQUEST_CODE:
                if(resultCode == Activity.RESULT_OK){
                    loadHabits();
                }
        }
    }

    private void loadHabits() {
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                SQLiteDatabase db = MainActivity.dbHelper.getReadableDatabase();

                // Define a projection that specifies which columns from the database
                // you will actually use after this query.
                String[] projection = {HabitContract.HabitEntry.COLUMN_NAME};
                // How you want the results sorted in the resulting Cursor
                String sortOrder = HabitContract.HabitEntry.COLUMN_NAME+" DESC";

                Cursor c = db.query(HabitContract.HabitEntry.TABLE_NAME,
                        projection,
                        null,null,null,null,sortOrder);
                c.moveToFirst();
                Log.d("Checkin", "looking for data" + c.getCount());
                habitList.clear();
                for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
                    habitList.add(c.getString(c.getColumnIndex(HabitContract.HabitEntry.COLUMN_NAME)));
                }
                c.close();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                adapter.notifyDataSetChanged();
            }
    }.execute();
    }
}