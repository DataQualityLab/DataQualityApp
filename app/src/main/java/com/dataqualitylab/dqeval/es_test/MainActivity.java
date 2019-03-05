package com.dataqualitylab.dqeval.es_test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView tvResult;
    Button btnCalc;
    EditText eBlk, eDan, eunkn, ePerm, eOS, ePatch, eModel, eBtldr, eRoot, eDevmn, eLock, eTrend, eCmprsn, eAccr, eConsist, eNoise, eResol, eFreshness;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = (TextView)findViewById(R.id.tResult);
        btnCalc = (Button) findViewById(R.id.btnCalculate);

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
        eConsist = (EditText)findViewById(R.id.eConsist);
        eNoise = (EditText)findViewById(R.id.eNoise);
        eResol = (EditText)findViewById(R.id.eResol);
        eFreshness = (EditText)findViewById(R.id.eFresh);


        eBlk.setText("0");
        eDan.setText("0");
        eunkn.setText("0");
        ePerm.setText("0.01");
        eOS.setText("26");
        ePatch.setText("2");
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

        calculate();

        btnCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
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


}
