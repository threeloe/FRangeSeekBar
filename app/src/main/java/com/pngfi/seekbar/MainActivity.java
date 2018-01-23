package com.pngfi.seekbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SeekBar seekBar= (SeekBar) findViewById(R.id.seekbar);
        final TextView textView= (TextView) findViewById(R.id.tv_value);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, float lesserProgress, float largerProgress, boolean fromUser) {
                textView.setText(lesserProgress+"--"+largerProgress);
            }
        });

       RangeSeekBar range= (RangeSeekBar) findViewById(R.id.seekBar);
        range.setOnRangeChangedListener(new RangeSeekBar.OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float min, float max, boolean isFromUser) {
                textView.setText(min+"--"+max);
            }
        });


        findViewById(R.id.btr_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar.setProgress(10,50);
            }
        });

    }
}
