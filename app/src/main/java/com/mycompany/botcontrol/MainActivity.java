package com.mycompany.botcontrol;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.net.Uri;
import android.widget.MediaController;
import android.widget.VideoView;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    static private ImageButton upLeftButton=null, upRightButton=null, downLeftButton=null, downRightButton=null,
            manualButton = null, buttonLeft = null, buttonRight = null, buttonUp = null, buttonDown = null;
    static private SeekBar speedControl = null;
    static private EditText editText1 = null;
    static private ImageButton speakButton  = null;
    static private vertical_slider speedController;
    static private TextView  voiceEngine = null;

    private final int REQ_CODE_SPEECH_INPUT = 100;
    static private String serverIP =  "0.0.0.0";

    static private Socket socketToSend = null;
    clientSocket cSocket = new clientSocket();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        VideoView vidView = (VideoView)findViewById(R.id.VideoStream);

        initWidgets();
        initListeners();

    }

    private void applySettings(String messageReceived) {

    }


    private void initListeners()
    {
        seekBarHandler();
        addButtonUpListener();
        addButtonDownListener();
        addButtonLeftListener();
        addButtonRightListener();
        addSpeakButtonListener();
        addManualButtonListener();
        addButtonUpLeftListener();
        addButtonUpRightListener();
        addButtonDownRightListener();
        addButtonDownLeftListener();
    }

    private void addSpeakButtonListener() {

        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakButton.setVisibility(View.GONE);
                buttonUp.setVisibility(View.VISIBLE);
                buttonDown.setVisibility(View.VISIBLE);
                buttonRight.setVisibility(View.VISIBLE);
                buttonLeft.setVisibility(View.VISIBLE);
                upLeftButton.setVisibility(View.VISIBLE);
                downLeftButton.setVisibility(View.VISIBLE);
                upRightButton.setVisibility(View.VISIBLE);
                downRightButton.setVisibility(View.VISIBLE);
                manualButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void addManualButtonListener() {
        manualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                manualButton.setVisibility(View.GONE);
                buttonUp.setVisibility(View.GONE);
                buttonRight.setVisibility(View.GONE);
                buttonLeft.setVisibility(View.GONE);
                buttonDown.setVisibility(View.GONE);
                upLeftButton.setVisibility(View.GONE);
                downLeftButton.setVisibility(View.GONE);
                upRightButton.setVisibility(View.GONE);
                downRightButton.setVisibility(View.GONE);
                speakButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String lData = result.get(0).toUpperCase();
                    voiceEngine.setText(lData+ " ("+lData.length()+")");

                    if(lData.equals("UP"))
                    {
                        actionHandler(0, true, 0);
                    }
                    else if(lData.equals("DOWN"))
                    {
                        actionHandler(1, true, 0);
                    }
                    else if(lData.equals("LEFT"))
                    {
                        actionHandler(2, true, 0);
                    }
                    else if(lData.equals("RIGHT"))
                    {
                        actionHandler(3, true, 0);
                    }
                }
                break;
            }

        }
    }



    private void initWidgets()
    {
        editText1 = (EditText) findViewById(R.id.editText1);
        speedControl = (SeekBar) findViewById(R.id.SpeedControl);
        buttonDown = (ImageButton) findViewById(R.id.ButtonDown);
        buttonUp = (ImageButton) findViewById(R.id.ButtonUp);
        buttonLeft = (ImageButton) findViewById(R.id.ButtonLeft);
        buttonRight = (ImageButton) findViewById(R.id.ButtonRight);
        upLeftButton = (ImageButton) findViewById(R.id.ButtonUpLeft);
        upRightButton = (ImageButton) findViewById(R.id.ButtonUpRight);
        downLeftButton = (ImageButton) findViewById(R.id.ButtonDownLeft);
        downRightButton = (ImageButton) findViewById(R.id.ButtonDownRight);
        speakButton = (ImageButton) findViewById(R.id.SpeakButton);
        manualButton = (ImageButton) findViewById(R.id.ManualButton);
        voiceEngine =  (TextView) findViewById(R.id.voiceEngine);
        speedController = (vertical_slider) findViewById(R.id.SpeedControlNew);
        speakButton.setVisibility(View.GONE);
    }

    public void actionHandler(final Integer sID, final boolean sState, final Integer sSeek )
    {
        jsonManager buildRequest = new jsonManager();
        dataObject switchDataObject = new dataObject("SET", sID, sState, sSeek);

        String messageToSend = buildRequest.convertToJson(switchDataObject);
        serverIP = String.valueOf(editText1.getText());
        socketToSend = cSocket.initSocket(serverIP);
        if (socketToSend != null) {
            String messageReceived = cSocket.exchange_data(socketToSend, messageToSend);
            applySettings(messageReceived);
        }
        else
        {
            Toast.makeText(MainActivity.this, "Server :"+serverIP+" Connection Error",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void seekBarHandler() {
        speedController.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressValue = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressValue = progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                actionHandler(8, true,progressValue );
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });

        speedControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressValue = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                actionHandler(4, true,progressValue );
//                Toast.makeText(MainActivity.this, "Covered: " + progressValue + "/" + seekBar.getMax(),
//                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addButtonUpListener() {
        buttonUp.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent ev){
                switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        actionHandler(0,true,0);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        actionHandler(0,false,0);
                        break;
                    case MotionEvent.ACTION_MOVE:
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void addButtonDownListener() {

        buttonDown.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        actionHandler(1, true, 0);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        actionHandler(1, false, 0);
                        break;
                    case MotionEvent.ACTION_MOVE:
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void addButtonLeftListener() {

        buttonLeft.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        actionHandler(2, true, 0);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        actionHandler(2, false, 0);
                        break;
                    case MotionEvent.ACTION_MOVE:
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void addButtonRightListener() {

        buttonRight.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        actionHandler(3, true, 0);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        actionHandler(3, false, 0);
                        break;
                    case MotionEvent.ACTION_MOVE:
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void addButtonUpLeftListener() {
        upLeftButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        actionHandler(4, true, 0);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        actionHandler(4, false, 0);
                        break;
                    case MotionEvent.ACTION_MOVE:
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void addButtonUpRightListener() {
        upRightButton.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent ev){
                switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        actionHandler(5,true,0);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        actionHandler(5,false,0);
                        break;
                    case MotionEvent.ACTION_MOVE:
                    default:
                        break;
                }
                return true;
            }
        });}

    private void addButtonDownLeftListener() {
        downLeftButton.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent ev){
                switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        actionHandler(6,true,0);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        actionHandler(6,false,0);
                        break;
                    case MotionEvent.ACTION_MOVE:
                    default:
                        break;
                }
                return true;
            }
        });

    }

    private void addButtonDownRightListener() {
        downRightButton.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent ev){
                switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        actionHandler(7,true,0);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        actionHandler(7,false,0);
                        break;
                    case MotionEvent.ACTION_MOVE:
                    default:
                        break;
                }
                return true;
            }
        });

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
}
