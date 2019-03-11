package com.filipetrovic.auxilium.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.filipetrovic.auxilium.R;
import com.filipetrovic.auxilium.TunerView.Button;

public class AlertDialog extends DialogFragment implements
        android.view.View.OnClickListener {

    private Context c;
    private Dialog d;
    private Button buttonPositive, buttonNegative;
    private TextView dialogTitle, dialogContent;
    private OnDialogResultListener mListener;
    private OnDialogCreatedListener dialogCreatedListener;

    public AlertDialog() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_alert, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonPositive = view.findViewById(R.id.btn_positive);
        buttonNegative = view.findViewById(R.id.btn_negative);
        dialogTitle = view.findViewById(R.id.dialog_title);
        dialogContent = view.findViewById(R.id.dialog_content);

        /* Root */
        view.findViewById(R.id.dialog_backdrop).setOnClickListener(this);

        /* Click gets passed through to dialog backdrop, */
        /* so the dialog box touch event needs to be overridden */
        view.findViewById(R.id.dialog_box).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        /* Buttons */
        buttonPositive.setOnClickListener(this);
        buttonNegative.setOnClickListener(this);

        if(dialogCreatedListener != null) {
            dialogCreatedListener.onCreate();
        }
    }

    public Button getPositiveButton() {
        return buttonPositive;
    }

    public Button getNegativeButton() {
        return buttonNegative;
    }

    public TextView getDialogTitle() {
        return dialogTitle;
    }

    public TextView getDialogContent() {
        return dialogContent;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_positive:
                if(mListener != null) mListener.onPositiveResult();
                getDialog().dismiss();
                break;
            case R.id.btn_negative:
                if(mListener != null) mListener.onNegativeResult();
                getDialog().dismiss();
                break;
            case R.id.dialog_backdrop:
                if(mListener != null) mListener.onNegativeResult();
                getDialog().dismiss();
                break;
            default:
                break;
        }
    }

    public void setDialogCreatedListener(OnDialogCreatedListener l) {
        dialogCreatedListener = l;
    }

    public interface OnDialogCreatedListener {
        void onCreate();
    }


    public void setDialogResultListener(OnDialogResultListener mListener) {
        this.mListener = mListener;
    }

    public interface OnDialogResultListener {
        void onPositiveResult();
        void onNegativeResult();
    }
}