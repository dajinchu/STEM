package com.gmail.dajinchu.stem;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Da-Jin on 11/25/2015.
 */
public class CheckInFragment extends Fragment implements Subscriber{
    private CheckInAdapter adapter;
    SimpleSectionedRecyclerViewAdapter mSectionedAdapter;
    ArrayList<Habit> habitList = new ArrayList<>();


    public static final int NEW_HABIT_REQUEST_CODE = 1;
    private TextView noHabitText;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_in, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.habit_check_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new CheckInAdapter(habitList, this);

        mSectionedAdapter = new SimpleSectionedRecyclerViewAdapter(getContext(), R.layout.section, R.id.section_text, adapter);
        recyclerView.setAdapter(mSectionedAdapter);

        noHabitText = (TextView) view.findViewById(R.id.no_habits);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //prevent subheaders from being swiped
                if (mSectionedAdapter.isSectionHeaderPosition(viewHolder.getAdapterPosition()))
                    return;
                int index = mSectionedAdapter.sectionedPositionToPosition(viewHolder.getLayoutPosition());
                markHabitDone(index);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    //prevent subheaders from being swiped
                    if (mSectionedAdapter.isSectionHeaderPosition(viewHolder.getAdapterPosition()))
                        return;
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
        loadAllFromSugar();
        sortAndSectionHabits();//TODO actually needed cause it's the first load I believe
        return view;
    }

    public void openHabitFragment(int id) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("newhabit");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        Bundle bundle = new Bundle();
        bundle.putInt("habitId", id);

        // Create and show the dialog.
        DialogFragment newFragment = new NewHabitFragment();
        newFragment.setArguments(bundle);
        newFragment.show(ft, "newhabit");
    }

    private void markHabitDone(int listIndex) {
        habitList.get(listIndex).addCompletionNow();
    }

    private void loadAllFromSugar(){
        habitList.clear();
        List<Habit> habits = Habit.listAll(Habit.class);
        Bench.start("filter checkin habits");
        for (Habit habit :habits) {
            if (shouldShowHabit(habit)) {
                habitList.add(habit);
            }
        }
        Bench.end("filter checkin habits");
    }

    private boolean shouldShowHabit(Habit habit){
        boolean completedNow = habit.isCompletedNow();
        return !completedNow
                && habit.getDays()[Habit.calendarDayWeekToDisplay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))];
    }

    private void sortAndSectionHabits() {
        Bench.start("checkin Load Habits");

        Bench.start("checkin sort habits");
        Collections.sort(habitList, new HabitComparator());
        Bench.end("checkin sort habits");


        Bench.start("section recycler");
        //Section out the recyclerview
        if(habitList.size()>0){
            mSectionedAdapter.setSections(calcSections(Calendar.getInstance()));
            noHabitText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }else{
            noHabitText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        mSectionedAdapter.notifyDataSetChanged();
        Bench.end("section recycler");

        Bench.end("checkin Load Habits");
    }

    private SimpleSectionedRecyclerViewAdapter.Section[] calcSections(Calendar now) {
        List<SimpleSectionedRecyclerViewAdapter.Section> sections =
                new ArrayList<>();
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Do Now"));
        for (int i = 0; i < habitList.size(); i++) {
            if (new TimeComparator().compare(habitList.get(i).getTimeToDo(), now) != -1) {
                //habits are in ascending timetodo order, so the first habit happens after now
                //will be the split between future and past events for this day
                sections.add(new SimpleSectionedRecyclerViewAdapter.Section(i, "Later Today"));
                if (i == 0) {
                    //If LaterToday is on the top, that means the Do Now section is empty, so rm header
                    sections.remove(0);
                }
                break;
            }
        }
        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
        return sections.toArray(dummy);
    }

    //Broadcast receiver to intercept broadcast of it being time to do a habit
    BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sortAndSectionHabits();
            abortBroadcast();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(TimeToDoReceiver.ACTION_TIME_TO_DO);
        filter.setPriority(1);
        getContext().registerReceiver(updateReceiver, filter);

        Habit.subscribe(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(updateReceiver);

        Habit.unsubscribe(this);
    }

    @Override
    public void update(SubscribableSugarRecord record) {
        //TODO this won't support remove a record!!
        Log.d("CheckIn","notified, checkinfragment loading habits");
        Habit habit = (Habit) record;
        Iterator<Habit> iterator = habitList.iterator();
        while(iterator.hasNext()){
            if(iterator.next().getId().equals(habit.getId())){
                iterator.remove();
            }
        }
        if (shouldShowHabit(habit)) {
            habitList.add(habit);
        }
        sortAndSectionHabits();
    }
}