package com.lovejoy777.rroandlayersmanager.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;

import com.lovejoy777.rroandlayersmanager.R;

public class CheckBoxHolder extends FrameLayout {
    public CheckBoxHolder(Context context) {
        super(context);
        setUp();
    }

    public CheckBoxHolder(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp();
    }

    public CheckBoxHolder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUp();
    }

    public CheckBoxHolder(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setUp();
    }

    private void setUp() {

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckBox checkBox = (CheckBox) findViewById(R.id.CheckBox);

                if (checkBox.isEnabled()) {
                    checkBox.performClick();
                }

            }
        });

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                CheckBox checkBox = (CheckBox) findViewById(R.id.CheckBox);

                if (!checkBox.isEnabled()) {
                    checkBox.performClick();
                }

                return true;
            }
        });

    }


    /**
     * This is very important function
     * @param ev very important parameters
     * @return Something very important
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    public interface CheckBoxHolderCallback {
        void onClick(CheckBox checkBox, boolean checked);
    }

}
