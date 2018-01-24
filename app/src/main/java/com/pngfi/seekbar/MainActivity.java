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
        final RangeSeekBar seekBar= (RangeSeekBar) findViewById(R.id.seekbar);
        final TextView textView= (TextView) findViewById(R.id.tv_value);
        seekBar.setOnSeekBarChangeListener(new RangeSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(RangeSeekBar seekBar, float lesserProgress, float largerProgress, boolean fromUser) {
                textView.setText(lesserProgress+"#####"+largerProgress);
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
