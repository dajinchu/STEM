package com.gmail.dajinchu.stem.view.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gmail.dajinchu.stem.models.Routine;
import com.gmail.dajinchu.stem.R;

import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.Arrays;

/**
 * Created by Da-Jin on 2/8/2016.
 */
public class BackupAlarmDialog extends DialogFragment {

    private int initIndex;
    private OnSetAlarmListener listener;
    private BackupAlarmAdapter adapter;

    public interface OnSetAlarmListener {
        void onMinutesPicked(String minutes);
    }

    public static BackupAlarmDialog newInstance(OnSetAlarmListener listener, String initialMinute){
        BackupAlarmDialog instance = new BackupAlarmDialog();
        instance.initIndex = Arrays.asList(Routine.possibleBackupChoices()).indexOf(initialMinute);
        instance.listener = listener;
        return instance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .title(R.string.backup_alarm_dialog_title)
                .negativeText(R.string.dismiss)
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        listener.onMinutesPicked(Routine.possibleBackupChoices()[adapter.getSelectedPos()]);
                    }
                });

        LayoutInflater i = getActivity().getLayoutInflater();

        View v = i.inflate(R.layout.backup_alarm_dialog, null);
        RecyclerView recycler = (RecyclerView)v.findViewById(R.id.back_up_alarm_recycler_options);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setHasFixedSize(true);
        adapter = new BackupAlarmAdapter(Routine.possibleBackupChoices(),initIndex);
        recycler.setAdapter(adapter);

        builder.customView(v, true);
        return builder.show();
    }
}
