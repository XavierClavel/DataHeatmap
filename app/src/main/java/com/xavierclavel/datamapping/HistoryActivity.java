package com.xavierclavel.datamapping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout linLayout;
    RelativeLayout.LayoutParams paramsTopRight;
    ColorStateList orange;
    ColorStateList red;
    ColorStateList green;
    List<MeasurementSummary> measurementSummaries;
    List<Button> buttonList;
    Button buttonDelete;

    HashMap<Integer,RelativeLayout> dictionaryButtonToLayout = new HashMap<>();
    HashMap<Integer,MeasurementSummary> dictionaryButtonToObject = new HashMap<>();

    boolean isDeleting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        linLayout = findViewById(R.id.historyLayout);

        paramsTopRight = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsTopRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        paramsTopRight.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

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

        createLayout(new MeasurementSummary("13/10/2022 18:23", "Toulouse", "15", "a0"));
        createLayout(new MeasurementSummary("13/10/2022 18:47", "Toulouse", "23", "a"));

        for (MeasurementSummary measurementSummary : measurementSummaries) {
            createLayout(measurementSummary);
        }

    }


    void createLayout(MeasurementSummary measurementSummary) {
        String date = measurementSummary.date;
        String place = measurementSummary.place;
        String nbPoints = measurementSummary.nbPoints;

        RelativeLayout layout = new RelativeLayout(HistoryActivity.this);
        layout.setPadding(20, 20, 20, 20);

        LinearLayout localLinLayout = new LinearLayout(HistoryActivity.this);
        localLinLayout.setOrientation(LinearLayout.VERTICAL);

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

        layout.addView(localLinLayout);

        Button displayButton = new Button(HistoryActivity.this);
        Log.d("button id", displayButton.getId() + "");
        displayButton.setText("Display");
        displayButton.setBackgroundTintList(orange);
        displayButton.setTextColor(Color.rgb(0,0,0));

        layout.addView(displayButton, paramsTopRight);

        //dictionaryButtonToLayout.put(displayButton.getId(), layout);

        buttonList.add(displayButton);
        displayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDeleting) {
                    linLayout.removeView(layout);
                    measurementSummaries.remove(measurementSummary);
                    File dir = MainActivity.instance.getFilesDir();
                    File file = new File(dir, measurementSummary.filename);
                    boolean deleted = file.delete();
                    Log.d("file deleted", deleted+"");
                    XmlManager.WriteHistory(measurementSummaries);
                    //TODO : delete file
                }
                else {
                    //TODO : display on map
                }

            }
        });


        linLayout.addView(layout);
}


    @Override
    public void onClick(View view) {
        Log.d("view", view.getId() + "");
        if (view.getId() == R.id.buttonDelete) {
            if (isDeleting) {
                isDeleting = false;
                buttonDelete.setText("delete");
                buttonDelete.setBackgroundTintList(red);
                for (Button button : buttonList) {
                    button.setText("display");
                    button.setBackgroundTintList(orange);
                }
            }
            else {
                isDeleting = true;
                buttonDelete.setText("done");
                buttonDelete.setBackgroundTintList(green);
                for (Button button : buttonList) {
                    button.setText("delete");
                    button.setBackgroundTintList(red);
                }
            }
        }
    }
}