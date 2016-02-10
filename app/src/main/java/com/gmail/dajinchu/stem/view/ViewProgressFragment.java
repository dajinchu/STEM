package com.gmail.dajinchu.stem.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.dajinchu.stem.model.FilteringRoutineListener;
import com.gmail.dajinchu.stem.R;
import com.gmail.dajinchu.stem.model.Routine;

import java.util.ArrayList;

/**
 * Created by Da-Jin on 1/16/2016.
 */
public class ViewProgressFragment extends Fragment {

    private ArrayList<Routine> routinesWithCompletions;
    private ProgressPreviewAdapter adapter;
    private FilteringRoutineListener routineListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_progress, container, false);

        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.progress_preview_list);

        routinesWithCompletions = new ArrayList<>();
        adapter = new ProgressPreviewAdapter(routinesWithCompletions);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        routineListener = new FilteringRoutineListener(routinesWithCompletions){
            @Override
            public boolean shouldKeep(Routine routine) {
                return routine.getCompletions().size()>0;
            }
            @Override
            public void onListChanged() {
                adapter.notifyDataSetChanged();
            }
        };
        Routine.subscribe(routineListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        Routine.unsubscribe(routineListener);
    }

}
