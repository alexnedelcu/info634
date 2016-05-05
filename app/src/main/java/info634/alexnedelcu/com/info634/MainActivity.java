package info634.alexnedelcu.com.info634;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    SensorRecorder sr;
    int interval = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Spinner spnAction = (Spinner) findViewById(R.id.spnAction);


        sr = new SensorRecorder(this,  spnAction.getSelectedItem().toString());
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

        sr.recordMetrics();

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
        final Button btnStart = (Button) findViewById(R.id.btnStart);
        final Button btnStop = (Button) findViewById(R.id.btnPause);
        final Button btnSend = (Button) findViewById(R.id.btnSend);
        final Button btnClear = (Button) findViewById(R.id.btnClear);
        final SeekBar seekBarInterval = (SeekBar) findViewById(R.id.seekInterval);
        final Spinner spnAction = (Spinner) findViewById(R.id.spnAction);



        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sr.start(interval);
                btnStop.setEnabled(true);

                btnStart.setEnabled(false);
                btnSend.setEnabled(false);
                seekBarInterval.setEnabled(false);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sr.pause();

                btnStart.setEnabled(true);
                btnSend.setEnabled(true);
                btnStop.setEnabled(false);
                seekBarInterval.setEnabled(true);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setType("plain/text");
                sendIntent.setData(Uri.parse("silberquitr@gmail.com"));
                sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ialexandru.nedelcu@gmail.com"});
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Log " + new Date());
                sendIntent.putExtra(Intent.EXTRA_TEXT, sr.getCSV());
                startActivity(sendIntent);
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sr.clear();
            }
        });

        spnAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sr.setLabel(spnAction.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
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
