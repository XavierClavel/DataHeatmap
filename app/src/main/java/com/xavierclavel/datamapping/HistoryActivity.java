package com.xavierclavel.datamapping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout linLayout;
    RelativeLayout.LayoutParams paramsRight;
    ColorStateList orange;
    ColorStateList red;
    ColorStateList green;
    List<MeasurementSummary> measurementSummaries;
    List<ImageButton> buttonList;
    ImageButton buttonDelete;

    boolean isDeleting = false;

    ImageView bottomImageView;
    ImageView previousBottomImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        linLayout = findViewById(R.id.historyLayout);

        paramsRight = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        paramsRight.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

        buttonList = new ArrayList<>();
        buttonDelete = findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(this);

        orange = new ColorStateList(
                new int[][] {new int[] {android.R.attr.state_enabled}},
                new int[]{Color.rgb(241,136,5)});

        red = new ColorStateList(
                new int[][] {new int[] {android.R.attr.state_enabled}},
                new int[]{Color.rgb(184,82,82)});

        green = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_enabled}},
                new int[] {Color.rgb(139,195,74)}
        );

        measurementSummaries = XmlManager.ReadHistory();

        for (MeasurementSummary measurementSummary : measurementSummaries) {
            createLayout(measurementSummary);
        }
        if (previousBottomImageView != null) previousBottomImageView.setVisibility(View.VISIBLE);
        if (bottomImageView != null) {
            bottomImageView.setVisibility(View.INVISIBLE);
            previousBottomImageView = bottomImageView;
            bottomImageView = null;
        }
    }


    void createLayout(MeasurementSummary measurementSummary) {

        LinearLayout.LayoutParams textParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                2f
        );

        LinearLayout.LayoutParams buttonParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                8f
        );

        String date = measurementSummary.date;
        String place = measurementSummary.place;
        String nbPoints = measurementSummary.nbPoints;

        RelativeLayout layout = new RelativeLayout(HistoryActivity.this);
        layout.setPadding(20, 20, 20, 20);

        LinearLayout layout1 = new LinearLayout(this);
        layout1.setOrientation(LinearLayout.HORIZONTAL);

        layout.addView(layout1);

        LinearLayout localLinLayout = new LinearLayout(HistoryActivity.this);
        localLinLayout.setOrientation(LinearLayout.VERTICAL);
        localLinLayout.setLayoutParams(textParam);

        TextView dateDisplay = new TextView(HistoryActivity.this);
        dateDisplay.setText(date);
        dateDisplay.setTypeface(null, Typeface.BOLD);
        dateDisplay.setTextSize(20f);
        localLinLayout.addView(dateDisplay);

        TextView placeDisplay = new TextView(HistoryActivity.this);
        placeDisplay.setText(place);
        localLinLayout.addView(placeDisplay);

        TextView nbPointsDisplay = new TextView(HistoryActivity.this);
        nbPointsDisplay.setText(nbPoints + " measurements");
        localLinLayout.addView(nbPointsDisplay);

        layout1.addView(localLinLayout);

        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setLayoutParams(buttonParam);

        ImageButton displayButton = new ImageButton(HistoryActivity.this);
        Log.d("button id", displayButton.getId() + "");
        displayButton.setBackgroundResource(R.drawable.border_on_click);
        displayButton.setBackgroundTintList(orange);
        displayButton.setImageResource(R.drawable.map);
        displayButton.setImageTintList(orange);
        displayButton.setMinimumWidth(200);
        buttonLayout.setGravity(Gravity.CENTER);

        //layout.addView(displayButton, paramsRight);
        layout1.addView(buttonLayout);
        buttonLayout.addView(displayButton);

        linLayout.addView(layout);

        ImageView separator = new ImageView(this);
        separator.setImageResource(R.drawable.line);
        separator.setPadding(150,0,150,0);
        separator.setImageTintList(orange);
        linLayout.addView(separator);

        bottomImageView = separator;

        buttonList.add(displayButton);
        displayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDeleting) {
                    linLayout.removeView(layout);
                    linLayout.removeView(separator);
                    Log.d("measurement removed", measurementSummary.date);
                    measurementSummaries.remove(measurementSummary);
                    File dir = MainActivity.instance.getFilesDir();
                    File file = new File(dir, measurementSummary.filename);
                    boolean deleted = file.delete();
                    Log.d("file deleted", deleted+"");
                    XmlManager.WriteHistory(measurementSummaries);
                }
                else {
                    List<TimestampedData> timestampedDataList = XmlManager.Read(measurementSummary.filename);
                    Log.d("file opened", measurementSummary.filename);

                    HistoryMapActivity.firstLocation = timestampedDataList.get(0).position;
                    HistoryHeatmapManager.data = timestampedDataList;

                    Intent intent = new Intent(HistoryActivity.this, HistoryMapActivity.class);
                    startActivity(intent);

                }

            }
        });

}


    @Override
    public void onClick(View view) {
        Log.d("view", view.getId() + "");
        if (view.getId() == R.id.buttonDelete) {
            if (isDeleting) {
                isDeleting = false;
                buttonDelete.setImageResource(R.drawable.trash);
                buttonDelete.setBackgroundTintList(red);
                buttonDelete.setImageTintList(red);
                for (ImageButton button : buttonList) {
                    button.setImageResource(R.drawable.map);
                    button.setBackgroundTintList(orange);
                    button.setImageTintList(orange);
                }
            }
            else {
                isDeleting = true;
                buttonDelete.setImageResource(R.drawable.check);
                buttonDelete.setBackgroundTintList(green);
                buttonDelete.setImageTintList(green);
                for (ImageButton button : buttonList) {
                    button.setImageResource(R.drawable.trash);
                    button.setBackgroundTintList(red);
                    button.setImageTintList(red);
                }
            }
        }
    }
}