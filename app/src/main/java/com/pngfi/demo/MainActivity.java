package com.pngfi.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pngfi.rangeseekbar.RangeSeekBar;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG="MainActivity";
    private RangeSeekBar first;
    private RangeSeekBar second;
    private RangeSeekBar third;

    private TextView tvFirst;
    private TextView tvSecond;
    private TextView tvThird;
    private Button btnChange;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        first= (RangeSeekBar) findViewById(R.id.first);
        second= (RangeSeekBar) findViewById(R.id.second);
        third= (RangeSeekBar) findViewById(R.id.third);
        tvFirst= (TextView) findViewById(R.id.tv_first_value);
        tvSecond= (TextView) findViewById(R.id.tv_second_value);
        tvThird= (TextView) findViewById(R.id.tv_third_value);
        btnChange= (Button) findViewById(R.id.btn_change);
        first.setOnSeekBarChangeListener(new RangeSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(RangeSeekBar seekBar, float lesserProgress, float largerProgress, boolean fromUser) {
                tvFirst.setText((int)lesserProgress+" to "+(int)largerProgress);
            }
        });
        second.setOnSeekBarChangeListener(new RangeSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(RangeSeekBar seekBar, float lesserProgress, float largerProgress, boolean fromUser) {
                tvSecond.setText(floatReserveTwoDecimalPlaces(lesserProgress)+" to "+floatReserveTwoDecimalPlaces(largerProgress));
            }
        });
        third.setOnSeekBarChangeListener(new RangeSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(RangeSeekBar seekBar, float lesserProgress, float largerProgress, boolean fromUser) {
                tvThird.setText((int) lesserProgress+" to "+(int)largerProgress);
            }
        });
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random=new Random();
                int larger= random.nextInt(100)+1;
                int lesser=random.nextInt(larger);
                third.setProgress(lesser,larger);
            }
        });

    }

    private String floatReserveTwoDecimalPlaces(float f){
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(f);
    }

}

