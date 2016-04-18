package com.gmail.dajinchu.stem.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.dajinchu.stem.models.FilteringRoutineListener;
import com.gmail.dajinchu.stem.R;
import com.gmail.dajinchu.stem.models.Routine;

import java.util.ArrayList;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

/**
 * Created by Da-Jin on 1/16/2016.
 */
public class ViewProgressFragment extends Fragment {

    private ArrayList<Routine> routinesWithCompletions;
    private ProgressPreviewAdapter adapter;
    private FilteringRoutineListener routineListener;
    private RecyclerView recycler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_progress, container, false);

        recycler = (RecyclerView) view.findViewById(R.id.progress_preview_list);

        routinesWithCompletions = new ArrayList<>();
        adapter = new ProgressPreviewAdapter(routinesWithCompletions);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !routinesWithCompletions.isEmpty()) {
            new MaterialShowcaseView.Builder(getActivity())
                    .setTarget(recycler)
                    .setContentText(R.string.showcase_progress)
                    .setDismissText(R.string.showcase_dismiss)
                    .withRectangleShape()
                    .setDelay(100)
                    .singleUse("progresslist")
                    .show();
        }
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
