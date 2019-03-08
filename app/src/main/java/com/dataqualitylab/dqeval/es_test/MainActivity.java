package com.dataqualitylab.dqeval.es_test;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.hardware.SensorEventListener;

import com.igorkh.trustcheck.securitychecklibrary.CheckResults;
import com.igorkh.trustcheck.securitychecklibrary.SecurityCheckLib;
import com.igorkh.trustcheck.securitychecklibrary.SecurityLibResult;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final int MEASURE_NOISE = 1;  // The request code

    TextView tvResult;
    Button btnCalc, btnNoise;
    EditText eBlk, eDan, eunkn, ePerm, eOS, ePatch, eModel, eBtldr, eRoot, eDevmn, eLock, eTrend, eCmprsn, eAccr, eConsist, eNoise, eResol, eFreshness,
            eminDelay, eSenResol, eMaxRange;
    ProgressBar pg;

    private SensorManager sensorManager;
    private Sensor mSensor;

    private long lastUpdate = 0;
    private long startUpdate = 0;
    private long DURATION = 500;
    private float last_x=0, last_y=0, last_z=0;
    private  double rmsNoise = 0;
    long numMeasures = 0;

    double NOISE_TRESH = 0.05;

    SensorEventListener sensEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = (TextView)findViewById(R.id.tResult);
        btnCalc = (Button) findViewById(R.id.btnCalculate);
        btnNoise = (Button) findViewById(R.id.btnNoise);

        eBlk = (EditText)findViewById(R.id.eBlckLst);
        eDan = (EditText)findViewById(R.id.epDan);
        eunkn = (EditText)findViewById(R.id.eunknSrc);
        ePerm = (EditText)findViewById(R.id.ePerm);
        eOS = (EditText)findViewById(R.id.eOS);
        ePatch = (EditText)findViewById(R.id.ePatch);
        eModel = (EditText)findViewById(R.id.eModel);
        eBtldr = (EditText)findViewById(R.id.eBootldr);
        eRoot = (EditText)findViewById(R.id.eRoot);
        eDevmn = (EditText)findViewById(R.id.eDevMn);
        eLock = (EditText)findViewById(R.id.eLock);
        eTrend = (EditText)findViewById(R.id.eTrend);
        eCmprsn = (EditText)findViewById(R.id.eCmrsn);
        eAccr = (EditText)findViewById(R.id.eAccr);
        eminDelay = (EditText)findViewById(R.id.eDelay);
        eSenResol = (EditText)findViewById(R.id.eSenResol);
        eMaxRange = (EditText)findViewById(R.id.eMaxRange);
        eConsist = (EditText)findViewById(R.id.eConsist);
        eNoise = (EditText)findViewById(R.id.eNoise);
        eResol = (EditText)findViewById(R.id.eResol);
        eFreshness = (EditText)findViewById(R.id.eFresh);
        pg = (ProgressBar)findViewById(R.id.progressBar1);

        pg.setVisibility(View.GONE);

        int patchAge = HelperFunc.getSecPatchAge();
        SecurityCheckLib secLib = SecurityCheckLib.getInstance();

        int osversion = Build.VERSION.SDK_INT;

        eBlk.setText("0");
        eDan.setText("0");
        eunkn.setText("0");
        ePerm.setText("0.01");
//        eOS.setText("26");
        eOS.setText(Integer.toString(osversion));
//        ePatch.setText("2");
        ePatch.setText(Integer.toString(patchAge));
        eModel.setText("5");
        eBtldr.setText("10");
        eRoot.setText("1");
        eDevmn.setText("1");
        eLock.setText("1");
        eTrend.setText("1");
        eCmprsn.setText("0.95");
        eAccr.setText("0.9");
        eConsist.setText("0.9");
        eNoise.setText("0.9");
        eResol.setText("0.9");
        eFreshness.setText("0.9");

        calcCorrectens();

//        hideSoftKeyboard();

        getSecParameters();
        measureNoise();

//        calculate();

        btnCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });

        btnNoise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                measureNoiseNew();
            }
        });




//        tv1 = (TextView)findViewById(R.id.tv1);

//        double input[] = {1,0.1};
//
//        double input[] = {1,0.1,0.2,0.3};
//        double input[] = {3,4,5,6};
//        double input[] = {26.1,2,5};
//        double input[] = {10,0,0,0};
//        double input[] = {5,5};

//        double appIn[] = {0,0,0,0};
//        double devSec[] = {28,0,0};
//        double senSec[] = {10,1,1,1};
//        double cldIn[] = {1,1};
//        double corIn[] = {1,1,1,1};

//        double appIn[] = {1,1,1,1};
//        double devSec[] = {10,12,24};
//        double senSec[] = {0,0,0,0};
//        double cldIn[] = {0,0};
//        double corIn[] = {0,0,0,0};

//        double appIn[] = {0,0,0,0.01};
//        double devSec[] = {26,2,5};
//        double senSec[] = {10,1,1,1};
//        double cldIn[] = {1,0.95};
//        double corIn[] = {0.9,0.9,0.9,0.9};
//
//        double freshness = 0.9;
////
////        //double testout = FrameworkFunc.senSecModule(input);
////
//        long time1= System.currentTimeMillis();
//        double testout = 0;
//
//        for (int i=0; i<1000; i++) {
//
//            testout = FrameworkFunc.dqEvalNN(appIn, devSec, senSec, cldIn, corIn, freshness, true);
//        }
//
//        long time2= System.currentTimeMillis();
//
//        double time = (time2-time1);
//        time = time /1000;
//
////        tv1.setText(Double.toString(testout) + "; Time = " + Double.toString(time));
//        tvResult.setText(Double.toString(time));


    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    void calcCorrectens(){

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = null;

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            int minDelay = mSensor.getMinDelay();
            float maxRange = mSensor.getMaximumRange();
            float senResol = mSensor.getResolution();

            String smaxRange = Float.toString(maxRange);
            String sSensResol = Float.toString(senResol);

            eminDelay.setText(Integer.toString(minDelay));
            eMaxRange.setText(smaxRange);
            eSenResol.setText(sSensResol);
        }


    }

    void measureNoiseNew()
    {
        Intent intent = new Intent(this, NoiseActivity.class);
        startActivityForResult(intent,MEASURE_NOISE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == MEASURE_NOISE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                double result = data.getDoubleExtra(NoiseActivity.NOISE,0);
                eNoise.setText(String.format( "%.2f", result ));
            }
        }
    }

    void measureNoise()
    {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (mSensor==null)
            return;

        last_x=0;
        last_y=0;
        last_z=0;
        numMeasures=0;

        SensorEventListener sensEvent = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                Sensor mySensor = event.sensor;

                if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                    last_x = last_x + event.values[0]*event.values[0];
                    last_y = last_y + event.values[1]*event.values[1];
                    last_z = last_z + event.values[2]*event.values[2];

                    numMeasures = numMeasures+1;



                    if (System.currentTimeMillis() - startUpdate > DURATION)
                    {
                        sensorManager.unregisterListener(this);
                        double rmsX = Math.sqrt(last_x/numMeasures);
                        double rmsY = Math.sqrt(last_y/numMeasures);
                        double rmsZ = Math.sqrt(last_z/numMeasures);

//                        rmsNoise = (rmsX+rmsY+rmsZ) / 3;
                        rmsNoise = rmsY;

                        double rmsNoisePercent = 1 - rmsNoise/NOISE_TRESH;
                        if(rmsNoisePercent>1)
                        {
                            rmsNoisePercent = 1;
                        }
                        else if(rmsNoisePercent<0)
                        {
                            rmsNoisePercent = 0;
                        }

                        eNoise.setText(String.format( "%.2f", rmsNoisePercent ));
//                        eNoise.setText(String.format( "%.2f", rmsNoise ));
                    }


                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        startUpdate = System.currentTimeMillis();
        sensorManager.registerListener(sensEvent, mSensor , SensorManager.SENSOR_DELAY_FASTEST);
    }

    void getSecParameters(){

        final SecurityCheckLib securityLib = SecurityCheckLib.getInstance();
        securityLib.setActivity(this);

        final String API_KEY = getString(R.string.api_key);

        SecurityLibResult compResultListener = new SecurityLibResult() {
            @Override
            public void onResultRecieved(CheckResults result) {
                //label.setText(result.getJSONresult().toString());
                pg.setVisibility(View.GONE);

                eDan.setText(Integer.toString(result.isPha()? 1 : 0));
                eunkn.setText(Integer.toString(result.isUnknsrc()? 1 : 0));

                eBtldr.setText(Integer.toString(result.isAct()? 10 : 0));
                eRoot.setText(Integer.toString(result.isBit()? 1 : 0));
                eDevmn.setText(Integer.toString(result.isDevmenu()? 1 : 0));
                eLock.setText(Integer.toString(result.isScreenlock()? 1 : 0));

                calculate();


            }

            @Override
            public void onBooleanCheckResult(Boolean result) {
                int t=0;
            }

            @Override
            public void onErrorOccured(String error) {
                //label.setText(error);
                pg.setVisibility(View.GONE);
            }
        };

        pg.setVisibility(View.VISIBLE);

        securityLib.comprehensiveCheck(API_KEY,compResultListener);



    }

    void calculate(){

        double appIn[] = {Double.valueOf(eBlk.getText().toString()),Double.valueOf(eDan.getText().toString()),Double.valueOf(eunkn.getText().toString()),Double.valueOf(ePerm.getText().toString())};
        double devSec[] = {Double.valueOf(eOS.getText().toString()),Double.valueOf(ePatch.getText().toString()),Double.valueOf(eModel.getText().toString())};
        double senSec[] = {Double.valueOf(eBtldr.getText().toString()),Double.valueOf(eRoot.getText().toString()),Double.valueOf(eDevmn.getText().toString()),Double.valueOf(eLock.getText().toString())};
        double cldIn[] = {Double.valueOf(eTrend.getText().toString()),Double.valueOf(eCmprsn.getText().toString())};
        double corIn[] = {Double.valueOf(eAccr.getText().toString()),Double.valueOf(eConsist.getText().toString()),Double.valueOf(eNoise.getText().toString()),Double.valueOf(eResol.getText().toString())};

        double freshness = Double.valueOf(eFreshness.getText().toString());

        double result = FrameworkFunc.dqEvalNN(appIn, devSec, senSec, cldIn, corIn, freshness, true);
        tvResult.setText(String.format("%.2f", result));
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensEvent);
    }


}
