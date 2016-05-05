package info634.alexnedelcu.com.info634;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SensorRecorder sr;
    int interval = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        Log.i("MainActivity", "initializing main");
        System.out.println("initializing main");

        sr = new SensorRecorder(this);
        sr.setLoggingProcedure(new SensorRecorder.Command() {
            @Override
            public void execute(final String s) {

                // this is needed becase only the UI thread can update the UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    //stuff that updates ui

                        final TextView txtLog = (TextView) findViewById(R.id.txtLog);
                        txtLog.setText(s);

                        txtLog.setMovementMethod(new ScrollingMovementMethod()); // make the box scrollable


                    }
                });
            }
        });

        sr.recordWalkingMetrics();

        implementOnClickActions();

        implementSeekBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void implementOnClickActions() {
        final Button btnRunStart = (Button) findViewById(R.id.btnStartRun);
        final Button btnRunStop = (Button) findViewById(R.id.btnPauseRun);
        final Button btnWalkStart = (Button) findViewById(R.id.btnStartWalk);
        final Button btnWalkStop = (Button) findViewById(R.id.btnPauseWalk);
        final Button btnBikeStart = (Button) findViewById(R.id.btnStartBike);
        final Button btnBikeStop = (Button) findViewById(R.id.btnPauseBike);
        final SeekBar seekBarInterval = (SeekBar) findViewById(R.id.seekInterval);



        btnRunStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sr.startRunning(interval);
                btnRunStop.setEnabled(true);

                btnRunStart.setEnabled(false);
                btnWalkStart.setEnabled(false);
                btnWalkStop.setEnabled(false);
                btnBikeStart.setEnabled(false);
                btnBikeStop.setEnabled(false);
                seekBarInterval.setEnabled(false);
            }
        });

        btnRunStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sr.pauseRunning();

                btnRunStart.setEnabled(true);
                btnWalkStart.setEnabled(true);
                btnBikeStart.setEnabled(true);

                btnWalkStop.setEnabled(false);
                btnRunStop.setEnabled(false);
                btnBikeStop.setEnabled(false);

                seekBarInterval.setEnabled(true);

            }
        });
    }
    public void implementSeekBar() {
        final SeekBar seekBarInterval = (SeekBar) findViewById(R.id.seekInterval);
        final TextView txtInterval = (TextView) findViewById(R.id.txtInterval);

        seekBarInterval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtInterval.setText((200 + progress*100) + "ms");
                interval = (200 + progress*100);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }
}
