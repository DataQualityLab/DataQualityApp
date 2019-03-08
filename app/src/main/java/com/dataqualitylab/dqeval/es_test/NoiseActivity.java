package com.dataqualitylab.dqeval.es_test;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

public class NoiseActivity extends AppCompatActivity {

    final static String NOISE = "NOISE";

    private float last_x=0, last_y=0, last_z=0;
    private float glast_x=0, glast_y=0, glast_z=0;
    private  double rmsNoise = 0;
    private  double grmsNoise = 0;
    long numAccMeasures = 0;
    long numGyroMeasures = 0;

    double NOISE_TRESH = 0.05;
    double GNOISE_TRESH = 0.1;

    SensorEventListener sensEvent;

    private SensorManager sensorManager;
    private Sensor accSensor, gyrSensor;

    float accNoise = 0;
    float gyrNoise = 0;

    Switch swAcc, swGyro;
    Button btnStart, btnSave, btnCancel;
    TextView tvAccValue, tvGyrValue;
    RadioButton rbAccX, rbAccY, rbAccZ, rbAccAll, rbGyroX, rbGyroY, rbGyroZ, rbGyroAll;
    ProgressBar pgAcc, pgGyro;

    int ACC_MEASURES = 500;
    int GYR_MEASURES = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noise);

        btnStart = (Button) findViewById(R.id.btnStart);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        swAcc = (Switch)findViewById(R.id.swAccUse);
        swGyro = (Switch)findViewById(R.id.swGyroUse);

        tvAccValue = (TextView)findViewById(R.id.tvAccVal);
        tvGyrValue = (TextView)findViewById(R.id.tvGyroVal);

        pgAcc = (ProgressBar)findViewById(R.id.pgDoneAcc);
        pgGyro = (ProgressBar)findViewById(R.id.pgDoneGyro);

        rbAccX = (RadioButton)findViewById(R.id.rbAccX);
        rbAccY = (RadioButton)findViewById(R.id.rbAccY);
        rbAccZ = (RadioButton)findViewById(R.id.rbAccZ);
        rbAccAll = (RadioButton)findViewById(R.id.rbAccAll);

        rbGyroX = (RadioButton)findViewById(R.id.rbGyroX);
        rbGyroY = (RadioButton)findViewById(R.id.rbGyroY);
        rbGyroZ = (RadioButton)findViewById(R.id.rbGyroZ);
        rbGyroAll = (RadioButton)findViewById(R.id.rbGyroAll);


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (accSensor==null){
            swAcc.setChecked(false);
            swAcc.setEnabled(false);
        }
        else{
            swAcc.setChecked(true);
            swAcc.setEnabled(true);
        }


        gyrSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyrSensor==null){
            swGyro.setChecked(false);
            swGyro.setEnabled(false);
        }
        else{
            swGyro.setChecked(true);
            swGyro.setEnabled(true);
        }

        pgGyro.setMax(GYR_MEASURES);
        pgAcc.setMax(ACC_MEASURES);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                measureNoise();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                double resultValue = 0;
                int num = 0;
                if (swAcc.isChecked())
                {
                    resultValue = resultValue+accNoise;
                    num = num +1;
                }

                if (swGyro.isChecked())
                {
                    resultValue = resultValue+gyrNoise;
                    num = num +1;
                }

                resultValue = resultValue/num;

                result.putExtra(NOISE,resultValue);
                setResult(RESULT_OK,result);
                finish();
            }
        });



}


    void measureNoise()
    {


        last_x=0;
        last_y=0;
        last_z=0;
        glast_x=0;
        glast_y=0;
        glast_z=0;
        numAccMeasures=0;
        numGyroMeasures=0;

        SensorEventListener sensEvent = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                Sensor mySensor = event.sensor;

                if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                    last_x = last_x + event.values[0]*event.values[0];
                    last_y = last_y + event.values[1]*event.values[1];
                    last_z = last_z + event.values[2]*event.values[2];

                    numAccMeasures = numAccMeasures+1;
                    pgAcc.setProgress((int)numAccMeasures);



                    if (numAccMeasures  > ACC_MEASURES)
                    {
                        sensorManager.unregisterListener(this,accSensor);
                        double rmsX = Math.sqrt(last_x/numAccMeasures);
                        double rmsY = Math.sqrt(last_y/numAccMeasures);
                        double rmsZ = Math.sqrt(last_z/numAccMeasures);

//                        rmsNoise = (rmsX+rmsY+rmsZ) / 3;
//                        rmsNoise = rmsY;

                        if(rbAccAll.isChecked())
                        {
                            rmsNoise = (rmsX+rmsY+rmsZ) / 3;
                        }
                        else if(rbAccX.isChecked()){
                            rmsNoise = rmsX;
                        }else if(rbAccY.isChecked()){
                            rmsNoise = rmsY;
                        }else if(rbAccZ.isChecked()){
                            rmsNoise = rmsZ;
                        }

                        double rmsNoisePercent = 1 - rmsNoise/NOISE_TRESH;
                        if(rmsNoisePercent>1)
                        {
                            rmsNoisePercent = 1;
                        }
                        else if(rmsNoisePercent<0)
                        {
                            rmsNoisePercent = 0;
                        }

                        accNoise = (float)rmsNoisePercent;
                        tvAccValue.setText(String.format( "%.2f", rmsNoisePercent ));
//                        eNoise.setText(String.format( "%.2f", rmsNoise ));
                    }


                }

                if (mySensor.getType() == Sensor.TYPE_GYROSCOPE) {
                    glast_x = glast_x + event.values[0]*event.values[0];
                    glast_y = glast_y + event.values[1]*event.values[1];
                    glast_z = glast_z + event.values[2]*event.values[2];

                    numGyroMeasures = numGyroMeasures+1;
                    pgGyro.setProgress((int)numGyroMeasures);



                    if (numGyroMeasures  > GYR_MEASURES)
                    {
                        sensorManager.unregisterListener(this,gyrSensor);
                        double rmsX = Math.sqrt(glast_x/numGyroMeasures);
                        double rmsY = Math.sqrt(glast_y/numGyroMeasures);
                        double rmsZ = Math.sqrt(glast_z/numGyroMeasures);

//                        rmsNoise = (rmsX+rmsY+rmsZ) / 3;
//                        rmsNoise = rmsY;

                        if(rbGyroAll.isChecked())
                        {
                            grmsNoise = (rmsX+rmsY+rmsZ) / 3;
                        }
                        else if(rbGyroX.isChecked()){
                            grmsNoise = rmsX;
                        }else if(rbGyroY.isChecked()){
                            grmsNoise = rmsY;
                        }else if(rbGyroZ.isChecked()){
                            grmsNoise = rmsZ;
                        }

                        double rmsNoisePercent = 1 - grmsNoise/GNOISE_TRESH;
                        if(rmsNoisePercent>1)
                        {
                            rmsNoisePercent = 1;
                        }
                        else if(rmsNoisePercent<0)
                        {
                            rmsNoisePercent = 0;
                        }

                        gyrNoise = (float)rmsNoisePercent;
                        tvGyrValue.setText(String.format( "%.2f", rmsNoisePercent ));
//                        eNoise.setText(String.format( "%.2f", rmsNoise ));
                    }


                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

//        startUpdate = System.currentTimeMillis();
        if(swAcc.isChecked())
            sensorManager.registerListener(sensEvent, accSensor , SensorManager.SENSOR_DELAY_FASTEST);
        if(swGyro.isChecked())
            sensorManager.registerListener(sensEvent, gyrSensor , SensorManager.SENSOR_DELAY_FASTEST);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensEvent);
    }

}
