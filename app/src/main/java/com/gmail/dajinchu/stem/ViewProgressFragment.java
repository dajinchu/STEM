package com.gmail.dajinchu.stem;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Da-Jin on 1/16/2016.
 */
public class ViewProgressFragment extends Fragment implements Subscriber{

    private ArrayList<Habit> habitsWithCompletions;
    private ProgressPreviewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_progress, container, false);

        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.progress_preview_list);
        recycler.setLayoutManager(new GridLayoutManager(getContext(),2));


        habitsWithCompletions = new ArrayList<>();
        getHabits();
        adapter = new ProgressPreviewAdapter(habitsWithCompletions);


        recycler.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        //TODO getHabits here instead of in Create?? same applies to CheckIn
        super.onResume();
        Habit.subscribe(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Habit.unsubscribe(this);
    }

    public void getHabits(){
        habitsWithCompletions.clear();
        for(Habit h:Habit.listAll(Habit.class)){
            if(h.getCompletions().size()>0){
                habitsWithCompletions.add(h);
            }
        }
    }

    @Override
    public void update(SubscribableSugarRecord record) {
        Habit habit = (Habit) record;
        Iterator<Habit> iterator = habitsWithCompletions.iterator();
        while(iterator.hasNext()){
            if(iterator.next().getId().equals(habit.getId())){
                iterator.remove();
            }
        }
        if(habit.getCompletions().size()>0) {
            habitsWithCompletions.add(habit);
        }
        adapter.notifyDataSetChanged();
    }
}
