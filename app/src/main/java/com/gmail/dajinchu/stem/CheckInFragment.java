package com.gmail.dajinchu.stem;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by Da-Jin on 11/25/2015.
 */
public class CheckInFragment extends Fragment {
    private CheckInAdapter adapter;
    SimpleSectionedRecyclerViewAdapter mSectionedAdapter;
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

        mSectionedAdapter = new SimpleSectionedRecyclerViewAdapter(getContext(), R.layout.section,R.id.section_text,adapter);
        recyclerView.setAdapter(mSectionedAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //prevent subheaders from being swiped
                if(mSectionedAdapter.isSectionHeaderPosition(viewHolder.getAdapterPosition()))return;
                int index = mSectionedAdapter.sectionedPositionToPosition(viewHolder.getLayoutPosition());
                markHabitDone(index);
                loadHabits();
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    //prevent subheaders from being swiped
                    if(mSectionedAdapter.isSectionHeaderPosition(viewHolder.getAdapterPosition()))return;
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
                openHabitFragment(NewHabitFragment.ID_NEW_HABIT);
            }
        });
        loadHabits();
        return view;
    }

    public void openHabitFragment(int id){
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("newhabit");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        Bundle bundle = new Bundle();
        bundle.putInt("habitId",id);

        // Create and show the dialog.
        DialogFragment newFragment = new NewHabitFragment();
        newFragment.setArguments(bundle);
        newFragment.setTargetFragment(CheckInFragment.this, NEW_HABIT_REQUEST_CODE);
        newFragment.show(ft, "newhabit");
    }

    private void markHabitDone(int listIndex){
        habitList.get(listIndex).addCompletionNow();
        loadHabits();
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
        habitList.clear();
        Calendar now = Calendar.getInstance();
        for(Habit habit : Habit.listAll(Habit.class)) {
            if(!habit.isCompletedNow()){
                habitList.add(habit);
            }
        }

        Collections.sort(habitList,new HabitComparator());

        //TODO show a "nothing now" thing when there's no habits

        //Section out the recyclerview
        List<SimpleSectionedRecyclerViewAdapter.Section> sections =
                new ArrayList<>();
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0,"Do Now"));
        for(int i = 0; i < habitList.size(); i++){
            if(new TimeComparator().compare(habitList.get(i).getTimeToDo(),now)!=-1){
                //habits are in ascending timetodo order, so the first habit happens after now
                //will be the split between future and past events for this day
                sections.add(new SimpleSectionedRecyclerViewAdapter.Section(i,"Later Today"));
                break;
            }
        }
        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
        mSectionedAdapter.setSections(sections.toArray(dummy));
        mSectionedAdapter.notifyDataSetChanged();
    }


    //Broadcast receiver to intercept broadcast of it being time to do a habit
    BroadcastReceiver updateReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            loadHabits();
            abortBroadcast();
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(TimeToDoReceiver.ACTION_TIME_TO_DO);
        filter.setPriority(1);
        getContext().registerReceiver(updateReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(updateReceiver);
    }
}