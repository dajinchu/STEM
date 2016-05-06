package com.gmail.dajinchu.stem.view.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.gmail.dajinchu.stem.R;

/**
 * Created by Da-Jin on 2/7/2016.
 */
public class ImplementationIntentionDialog extends DialogFragment implements TextWatcher {

    private EditText name,cue;
    private OnSetIntentionListener listener;
    private String nameText, cueText;
    private MDButton positive;

    private void initialize(OnSetIntentionListener listener, String name, String cue){
        this.listener = listener;
        this.nameText = name;
        this.cueText = cue;
    }

    public static ImplementationIntentionDialog newInstance(
            OnSetIntentionListener listener,
            String name, String cue){
        ImplementationIntentionDialog instance = new ImplementationIntentionDialog();
        instance.initialize(listener, name, cue);
        return instance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .title(R.string.implementation_intention)
                .negativeText(R.string.dismiss)
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        listener.onImplementationIntentionSet(
                                name.getText().toString(),
                                cue.getText().toString());
                    }
                });
        LayoutInflater i = getActivity().getLayoutInflater();

        View v = i.inflate(R.layout.implementation_intention_dialog, null);

        name = (EditText) v.findViewById(R.id.implementation_name_edit_text);
        cue = (EditText) v.findViewById(R.id.implementation_cue_edit_text);

        //Set up EditText inputType
        name.setInputType(InputType.TYPE_CLASS_TEXT);
        cue.setInputType(InputType.TYPE_CLASS_TEXT);

        //set and build the custom view dialog
        builder.customView(v, true);
        MaterialDialog dialog = builder.show();

        positive = dialog.getActionButton(DialogAction.POSITIVE);

        //Set listeners to make sure fields are filled
        name.addTextChangedListener(this);
        cue.addTextChangedListener(this);

        //Set initial values
        name.setText(nameText);
        cue.setText(cueText);

        return dialog;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(cue.length()==0||name.length()==0){
            positive.setEnabled(false);
        }else{
            positive.setEnabled(true);
        }
    }
    @Override
    public void afterTextChanged(Editable s) {

    }

    public interface OnSetIntentionListener {
        void onImplementationIntentionSet(String name, String cue);
    }
}
