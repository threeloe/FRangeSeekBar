package com.pngfi.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.pngfi.rangeseekbar.RangeSeekBar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG="MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RangeSeekBar seekBar= (RangeSeekBar) findViewById(R.id.seekbar);
        final TextView textView= (TextView) findViewById(R.id.tv_value);
        seekBar.setOnSeekBarChangeListener(new RangeSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(RangeSeekBar seekBar, float lesserProgress, float largerProgress, boolean fromUser) {
                textView.setText(lesserProgress+"#####"+largerProgress);
                Log.i(TAG,lesserProgress+"#####"+largerProgress);
            }
        });



        findViewById(R.id.btr_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar.setProgress(0.05f,0.71f);
            }
        });

    }
}
