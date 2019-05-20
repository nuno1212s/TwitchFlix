package com.twitchflix.applicationclient.utils;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Utils {

    public static void addErrorText(Activity context, ViewGroup view, int id, int resId) {

        TextView errorText = new TextView(context);

        errorText.setId(id);

        errorText.setTextColor(Color.RED);

        errorText.setText(resId);

        errorText.setPadding(0, 50, 0, 0);

        errorText.setGravity(Gravity.CENTER);

        view.addView(errorText);
    }

    public static void addProgressBar(Activity context, ViewGroup view, int id) {

        ProgressBar bar = new ProgressBar(context);

        bar.setId(id);

        bar.setPadding(0, 50, 0, 0);

        view.addView(bar);

    }

    public static void removeViewsFrom(ViewGroup group, int... viewIds) {

        for (int viewId : viewIds) {

            View viewById = group.findViewById(viewId);

            if (viewById != null) {
                group.removeView(viewById);
            }

        }

    }

}
