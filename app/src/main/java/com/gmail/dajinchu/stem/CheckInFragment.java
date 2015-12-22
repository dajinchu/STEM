package com.gmail.dajinchu.stem;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Da-Jin on 11/25/2015.
 */
public class CheckInFragment extends Fragment {
    private CheckInAdapter adapter;
    ArrayList<Habit> habitList = new ArrayList<>();


    public static final int NEW_HABIT_REQUEST_CODE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_in, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.habit_check_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new CheckInAdapter(habitList,this);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int index = viewHolder.getLayoutPosition();
                markHabitDone(index);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // Get RecyclerView item from the ViewHolder
                    View itemView = viewHolder.itemView;
                    Paint p = new Paint();
                    p.setColor(Color.parseColor("#4CAF50"));
                    if (dX > 0) {
                        // Draw Rect with varying right side, equal to displacement dX
                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                                (float) itemView.getBottom(), p);
                    } else {
                        // Draw Rect with varying left side, equal to the item's right side plus negative displacement dX
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom(), p);
                    }
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);
        ((Toolbar) getActivity().findViewById(R.id.my_toolbar)).setNavigationIcon(null);
        getActivity().findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHabitFragment("");
            }
        });
        loadHabits();
        return view;
    }

    public void openHabitFragment(String id){
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        Bundle bundle = new Bundle();
        bundle.putString("habitId",id);

        // Create and show the dialog.
        DialogFragment newFragment = new NewHabitFragment();
        newFragment.setArguments(bundle);
        newFragment.setTargetFragment(CheckInFragment.this, NEW_HABIT_REQUEST_CODE);
        newFragment.show(ft, "dialog");
    }

    private void markHabitDone(int listIndex){
        habitList.get(listIndex).addCompletionNow();
        habitList.remove(listIndex);
        adapter.notifyItemRemoved(listIndex);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NEW_HABIT_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    loadHabits();
                }
        }
    }

    private void loadHabits() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                SQLiteDatabase db = MainActivity.dbHelper.getReadableDatabase();

                Calendar now = Calendar.getInstance();

                // Define a projection that specifies which columns from the database
                // you will actually use after this query.
                String[] projection = null;
                //Define selection which filters which rows. SQL WHERE clause
                String selection = HabitContract.HabitEntry.COLUMN_NEXT_INCOMPLETE+"<"+now.getTimeInMillis();
                // How you want the results sorted in the resulting Cursor
                String sortOrder = HabitContract.HabitEntry.COLUMN_NAME + " DESC";



                Cursor c = db.query(HabitContract.HabitEntry.TABLE_NAME,
                        projection,
                        selection, null, null, null, sortOrder);
                c.moveToFirst();
                Log.d("Checkin", "looking for data" + c.getCount());
                habitList.clear();
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    habitList.add(Habit.getHabitFromCursor(c));
                    System.out.println("habit freq "+c.getString(c.getColumnIndex(HabitContract.HabitEntry.COLUMN_FREQUENCY)));
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