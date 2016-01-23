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

/**
 * Created by Da-Jin on 1/16/2016.
 */
public class ViewProgressFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_progress, container, false);

        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.progress_preview_list);
        recycler.setLayoutManager(new GridLayoutManager(getContext(),2));


        ArrayList<Habit> habitsWithCompletions = new ArrayList<>();
        for(Habit h:Habit.listAll(Habit.class)){
            if(h.getCompletions().size()>0){
                habitsWithCompletions.add(h);
            }
        }
        ProgressPreviewAdapter adapter = new ProgressPreviewAdapter(habitsWithCompletions);


        recycler.setAdapter(adapter);

        return view;
    }
}
