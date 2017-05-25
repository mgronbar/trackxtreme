package com.track.trackxtreme.iu;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.internal.util.Predicate;
import com.track.trackxtreme.MainActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;

import static android.R.attr.button;

/**
 * Created by marko on 09/05/2017.
 */

public class ContextButtonGroup extends RelativeLayout {
    private Map<TextView, Predicate<MainActivity>> buttons;
    private Button button;
    private Predicate<MainActivity> buttonPredicate;

    public ContextButtonGroup(Context context, int id, Predicate<MainActivity> buttonPredicate) {
        super(context);
        this.buttonPredicate = buttonPredicate;
        setId(View.generateViewId());
        buttons = new LinkedHashMap<>();
        setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        button = new Button(context);
        button.setId(View.generateViewId());
        button.setText(id);
        addView(button, layoutParams);
        button.setOnTouchListener(new ButtonOnTouchListener());
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPredicate.apply((MainActivity)context);
            }
        });


    }
    public void addButton(int text, Predicate<MainActivity> o) {
        TextView button = new TextView(getContext());
        button.setText(text);

        buttons.put(button,o);

    }
    public void addButton(String text) {
        TextView button = new TextView(getContext());
        button.setText(text);

        //buttons.add(button);

    }

    private void add(TextView button) {
        int childCount = getChildCount();
        View view = getChildAt(childCount - 1);
        button.setId(View.generateViewId());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ABOVE, view.getId());
        button.setPadding(20, 20, 20, 20);
        layoutParams.setMargins(childCount * 50, 20, 20, 20);
        addView(button, layoutParams);

    }

    public void clearButtons(){
        buttons.clear();
    }

    public void setText(int text) {
        button.setText(text);
    }

    private class ButtonOnTouchListener implements OnTouchListener {

        boolean test = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.d("Gestures", event.toString());

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    test = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (test) {
                        for (TextView tv : buttons.keySet()) {
                            add(tv);
                        }
                        test = false;

                    } else {
                        View view = activateView(event);
                        Log.d("CONTEXT", view != null ? view.toString() : "");
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    test = false;

                    for (TextView tv : buttons.keySet()) {
                        if(tv.isSelected()){
                            buttons.get(tv).apply((MainActivity) getContext());
                        }

                        removeView(tv);
                    }




                    break;
            }
//                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                        if (context.getChildCount() < 5) {
//                            final Button button = new Button(MainActivity.this);
//                            button.setText("OK");
//                            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//                            params.setMarginStart();
//                            context.addView(button, params);
//                        }
//                    }
            return false;//mDetector.onTouchEvent(event);

        }
    }

    private View activateView(MotionEvent event) {
        View view = UiTools.findViewAt(ContextButtonGroup.this, (int) event.getRawX(), (int) event.getRawY());
        setButtonHighLight(view);
        return view;
    }

    void setButtonHighLight(View v) {
        for (TextView tv : buttons.keySet()) {
            if (v != tv) {
                tv.setBackgroundColor(Color.LTGRAY);
                tv.setSelected(false);
            } else {
                tv.setBackgroundColor(Color.GRAY);
                tv.setSelected(true);
            }
        }
    }
}
