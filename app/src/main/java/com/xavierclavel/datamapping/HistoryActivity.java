package com.xavierclavel.datamapping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HistoryActivity extends AppCompatActivity {

    LinearLayout linLayout;
    RelativeLayout.LayoutParams paramsTopRight;
    ColorStateList orange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        linLayout = findViewById(R.id.historyLayout);

        paramsTopRight = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsTopRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        paramsTopRight.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

        orange = new ColorStateList(
                new int[][] {new int[] {android.R.attr.state_enabled}},
                new int[]{Color.rgb(241,136,5)});

        createLayout("13/10/2022 18:23", "Toulouse", 15);
        createLayout("13/10/2022 18:47", "Toulouse", 23);

    }

    void createLayout(String date, String place,int nbPoints) {
        RelativeLayout layout = new RelativeLayout(this);
        layout.setPadding(20,20,20,20);

        LinearLayout localLinLayout = new LinearLayout(this);
        localLinLayout.setOrientation(LinearLayout.VERTICAL);

        TextView dateDisplay = new TextView(this);
        dateDisplay.setText(date);
        dateDisplay.setTypeface(null,Typeface.BOLD);
        dateDisplay.setTextSize(20f);
        localLinLayout.addView(dateDisplay);

        TextView placeDisplay = new TextView(this);
        placeDisplay.setText(place);
        localLinLayout.addView(placeDisplay);

        TextView nbPointsDisplay = new TextView(this);
        nbPointsDisplay.setText(nbPoints + " measurements");
        localLinLayout.addView(nbPointsDisplay);

        layout.addView(localLinLayout);

        Button displayButton = new Button(this);
        displayButton.setText("Display");
        displayButton.setBackgroundTintList(orange);
        displayButton.setTextColor(Color.rgb(0,0,0));
        layout.addView(displayButton, paramsTopRight);

        linLayout.addView(layout);
    }
}