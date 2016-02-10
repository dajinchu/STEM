package com.gmail.dajinchu.stem.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.TextView;

import com.gmail.dajinchu.stem.model.FilteringRoutineListener;
import com.gmail.dajinchu.stem.R;
import com.gmail.dajinchu.stem.model.Routine;
import com.gmail.dajinchu.stem.model.RoutineComparator;
import com.gmail.dajinchu.stem.model.TimeComparator;
import com.gmail.dajinchu.stem.receivers.TimeToDoReceiver;

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
    List<Routine> routineList = new ArrayList<>();

    private TextView noRoutineText;
    private RecyclerView recyclerView;
    private FilteringRoutineListener routineListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_in, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.routine_check_list);
        noRoutineText = (TextView) view.findViewById(R.id.no_routines);
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.my_toolbar);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new CheckInAdapter(routineList, this);

        mSectionedAdapter = new SimpleSectionedRecyclerViewAdapter(getContext(), R.layout.section, R.id.section_text, adapter);
        recyclerView.setAdapter(mSectionedAdapter);


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
                markRoutineDone(index);
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

        fab.setVisibility(View.VISIBLE);
        toolbar.setNavigationIcon(null);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRoutineFragment(NewRoutineFragment.ID_NEW_ROUTINE);
            }
        });


        routineListener = new FilteringRoutineListener(routineList){
            @Override
            public boolean shouldKeep(Routine routine) {
                return !routine.isCompletedNow()
                        && routine.getDays()[Routine.calendarDayWeekToDisplay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))];
            }
            @Override
            public void onListChanged() {
                sortAndSectionRoutines();
            }
        };

        return view;
    }

    public void openRoutineFragment(int id) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("newroutine");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        Bundle bundle = new Bundle();
        bundle.putInt("routineId", id);

        // Create and show the dialog.
        DialogFragment newFragment = new NewRoutineFragment();
        newFragment.setArguments(bundle);
        newFragment.show(ft, "newroutine");
    }

    private void markRoutineDone(int listIndex) {
        routineList.get(listIndex).addCompletionNow();
    }

    private void sortAndSectionRoutines() {
        Collections.sort(routineList, new RoutineComparator());

        //Section out the recyclerview
        if(routineList.size()>0){
            mSectionedAdapter.setSections(calcSections(Calendar.getInstance()));
            noRoutineText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }else{
            noRoutineText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        mSectionedAdapter.notifyDataSetChanged();
    }

    private SimpleSectionedRecyclerViewAdapter.Section[] calcSections(Calendar now) {
        List<SimpleSectionedRecyclerViewAdapter.Section> sections =
                new ArrayList<>();
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Do Now"));
        for (int i = 0; i < routineList.size(); i++) {
            if (new TimeComparator().compare(routineList.get(i).getTimeToDo(), now) != -1) {
                //routines are in ascending timetodo order, so the first routine happens after now
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

    //Broadcast receiver to intercept broadcast of it being time to do a routine
    BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sortAndSectionRoutines();
            abortBroadcast();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(TimeToDoReceiver.ACTION_TIME_TO_DO);
        filter.setPriority(1);
        getContext().registerReceiver(updateReceiver, filter);

        Routine.subscribe(routineListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(updateReceiver);

        Routine.unsubscribe(routineListener);
    }


}