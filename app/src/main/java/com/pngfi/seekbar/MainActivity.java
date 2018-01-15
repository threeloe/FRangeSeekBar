package com.pngfi.seekbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SeekBar seekBar= (SeekBar) findViewById(R.id.seekbar);
        final TextView textView= (TextView) findViewById(R.id.tv_value);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, float lesserProgress, float largerProgress, boolean fromUser) {
                textView.setText(lesserProgress+"--"+largerProgress);
            }
        });
    }
}
