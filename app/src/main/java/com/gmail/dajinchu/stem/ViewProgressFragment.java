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

    private ArrayList<Routine> routinesWithCompletions;
    private ProgressPreviewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_progress, container, false);

        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.progress_preview_list);
        recycler.setLayoutManager(new GridLayoutManager(getContext(),2));


        routinesWithCompletions = new ArrayList<>();
        getRoutines();
        adapter = new ProgressPreviewAdapter(routinesWithCompletions);


        recycler.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        //TODO getRoutines here instead of in Create?? same applies to CheckIn
        super.onResume();
        Routine.subscribe(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Routine.unsubscribe(this);
    }

    public void getRoutines(){
        routinesWithCompletions.clear();
        for(Routine h:Routine.listAll(Routine.class)){
            if(h.getCompletions().size()>0){
                routinesWithCompletions.add(h);
            }
        }
    }

    @Override
    public void update(SubscribableSugarRecord record) {
        Routine routine = (Routine) record;
        Iterator<Routine> iterator = routinesWithCompletions.iterator();
        while(iterator.hasNext()){
            if(iterator.next().getId().equals(routine.getId())){
                iterator.remove();
            }
        }
        if(routine.getCompletions().size()>0) {
            routinesWithCompletions.add(routine);
        }
        adapter.notifyDataSetChanged();
    }
}
