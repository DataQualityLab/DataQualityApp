package com.igorkh.trustcheck.securitychecklibrary;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.safetynet.HarmfulAppsData;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.List;

/**
 * Created by Igor Khokhlov on 11/14/2017.
 */

public class SecurityCheckLib {

    private static final int LAST_VERSION = 28;

    private static final String TAG = "SEC_CHECK_LIB";

    private Activity activity;

    private SecurityLibResult resultCallback;

    private Boolean booleanResult = false;
    private int integerresult = 0;
    private CheckResults fullResult;

    private static volatile SecurityCheckLib sSoleInstance = new SecurityCheckLib();

    private SecurityCheckLib(){}

    public static SecurityCheckLib getInstance() {
        return sSoleInstance;
    }

    public void setActivity(Activity activity){
        this.activity=activity;
    }

    private byte[] generateNonce(){
        SecureRandom random = new SecureRandom();
        byte nonce[] = random.generateSeed(20);
        return nonce;
    }

    private boolean isPatternSet(Context context)
    {
        ContentResolver cr = context.getContentResolver();
        try
        {
            int lockPatternEnable = Settings.Secure.getInt(cr, Settings.Secure.LOCK_PATTERN_ENABLED);
            return lockPatternEnable == 1;
        }
        catch (Settings.SettingNotFoundException e)
        {
            //Log.e("app",e.getMessage());
            return false;
        }
    }

    private boolean isPassOrPinSet(Context context)
    {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE); //api 16+
        return keyguardManager.isKeyguardSecure();
    }

    public boolean isScreenLock(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return isDeviceLocked(activity);
        } else {
            return isPatternSet(activity) || isPassOrPinSet(activity);
        }

        //boolean result = isPatternSet(activity) || isPassOrPinSet(activity);
        //return result;
    }

    @TargetApi(23)
    private boolean isDeviceLocked(Context context) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE); //api 23+
        return keyguardManager.isDeviceSecure();
    }

    public boolean isDeveloperMenu()
    {
        int adb = Settings.Secure.getInt(activity.getContentResolver(),
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED , 0);

        boolean result = ((adb > 0) ? true : false);

        return result;
    }

    public boolean isUnknownSources(){
        boolean isNonPlayAppAllowed = false;

        if (Build.VERSION.SDK_INT >=26)
            return false;


        try {
            isNonPlayAppAllowed = Settings.Secure.getInt(activity.getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS) == 1;
//            isNonPlayAppAllowed = Settings.Secure.getInt(activity.getContentResolver(), Settings.Global.INSTALL_NON_MARKET_APPS) == 1;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return isNonPlayAppAllowed;
    }

    private void checkVerapp(final SecurityLibResult results)
    {
        OnCompleteListener completeListener = new OnCompleteListener<SafetyNetApi.VerifyAppsUserResponse>() {
            @Override
            public void onComplete(Task<SafetyNetApi.VerifyAppsUserResponse> task) {
                if (task.isSuccessful()) {
                    SafetyNetApi.VerifyAppsUserResponse result = task.getResult();
                    if (result.isVerifyAppsEnabled()) {
                        Log.d(TAG, "The Verify Apps feature is enabled.");
                        verifyApps(results);
                    } else {
                        Log.d(TAG, "The Verify Apps feature is disabled.");
                        enableVerApp(results);
                    }
                } else {
                    String error = "A general error occurred during play protect verification.";
                    Log.e(TAG, error);
                    results.onErrorOccured(error);
                }
            }
        };

        SafetyNet.getClient(activity).isVerifyAppsEnabled().addOnCompleteListener(completeListener);
    }

    private void enableVerApp(final SecurityLibResult results)
    {

        OnCompleteListener completeListener = new OnCompleteListener<SafetyNetApi.VerifyAppsUserResponse>() {
            @Override
            public void onComplete(Task<SafetyNetApi.VerifyAppsUserResponse> task) {
                if (task.isSuccessful()) {
                    SafetyNetApi.VerifyAppsUserResponse result = task.getResult();
                    if (result.isVerifyAppsEnabled()) {
                        Log.d(TAG, "The user gave consent " +
                                "to enable the Verify Apps feature.");
                        verifyApps(results);
                    } else {
                        String error = "The user didn't give consent " +
                                "to enable the Verify Apps feature.";
                        Log.d(TAG, error);
                        results.onErrorOccured(error);
                    }
                } else {
                    String error = "A general error occurred during play protect enabling.";
                    Log.e(TAG, error);
                    results.onErrorOccured(error);
                }
            }
        };
        
        SafetyNet.getClient(activity).enableVerifyApps().addOnCompleteListener(completeListener);
    }

    private void verifyApps(final SecurityLibResult results)
    {
        OnCompleteListener completeListener = new OnCompleteListener<SafetyNetApi.HarmfulAppsResponse>(){
            @Override
            public void onComplete(Task<SafetyNetApi.HarmfulAppsResponse> task) {
                Log.d(TAG, "Received listHarmfulApps() result");

                if (task.isSuccessful()) {
                    SafetyNetApi.HarmfulAppsResponse result = task.getResult();
                    long scanTimeMs = result.getLastScanTimeMs();

                    List<HarmfulAppsData> appList = result.getHarmfulAppsList();
                    boolean checkresult = false;
                    if (appList.isEmpty()) {
                        Log.d(TAG, "There are no known " +
                                "potentially harmful apps installed.");
                        checkresult = false;
                        booleanResult = false;
                    } else {
                        Log.e(TAG,
                                "Potentially harmful apps are installed!");

                        checkresult = true;
                        booleanResult = true;
                    }

                    results.onBooleanCheckResult(checkresult);

                } else {
                    String error = "An error occurred. " +
                            "Call isVerifyAppsEnabled() to ensure " +
                            "that the user has consented.";
                    Log.d(TAG, error);
                    results.onErrorOccured(error);
                }
            }
        };
        
        SafetyNet.getClient(activity).listHarmfulApps().addOnCompleteListener(completeListener);
    }

    public void isHarmfullApp(SecurityLibResult results){
        checkVerapp(results);
    }

    private JSONObject decodeJWStoJSON(String jwsString) {

        byte[] json = Base64.decode(jwsString.split("[.]")[1],Base64.DEFAULT);
        String text = new String(json, StandardCharsets.UTF_8);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;

    }

    public void attestDevice(String apiKey, final SecurityLibResult results){

        byte[] nonce = generateNonce();
        SafetyNet.getClient(activity).attest(nonce, apiKey)
                .addOnSuccessListener(activity,
                        new OnSuccessListener<SafetyNetApi.AttestationResponse>() {
                            @Override
                            public void onSuccess(SafetyNetApi.AttestationResponse response) {

                                String resultstring = response.getJwsResult();
                                JSONObject resultsjson = decodeJWStoJSON(resultstring);

                                CheckResults fullResultLocal = new CheckResults();

                                try {
                                    boolean resultbooleanlocal = resultsjson.getBoolean("ctsProfileMatch");
                                    fullResultLocal.setAct(resultbooleanlocal);
                                    //fullResult.setAct(resultbooleanlocal);
                                    resultbooleanlocal = resultsjson.getBoolean("basicIntegrity");
                                    fullResultLocal.setBit(resultbooleanlocal);
                                    //fullResult.setBit(resultbooleanlocal);
                                    results.onResultRecieved(fullResultLocal);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                int t=0;
                            }
                        })
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // An error occurred while communicating with the com.igorkh.trustcheck.securitycheckcloud.service.
                        if (e instanceof ApiException) {
                            // An error with the Google Play services API contains some
                            // additional details.
                            ApiException apiException = (ApiException) e;
                            // You can retrieve the status code using the
                            // apiException.getStatusCode() method.
                        } else {
                            // A different, unknown type of error occurred.
                            Log.d(TAG, "Error: " + e.getMessage());
                        }
                        results.onErrorOccured(e.getMessage());
                    }
                });
    }

    public byte androidVersion(){
        byte osversion = (byte)Build.VERSION.SDK_INT;

        osversion = (byte)(LAST_VERSION - osversion);

        if (osversion == 0)
            osversion = 2;
        else if (osversion > 1)
            osversion = 0;

        return osversion;
    }

    public void comprehensiveCheck(final String apikey, final SecurityLibResult results){

        fullResult = new CheckResults();

        fullResult.setAndroidVersion(androidVersion());
        fullResult.setDevmenu(isDeveloperMenu());
        fullResult.setScreenlock(isScreenLock());
        fullResult.setUnknsrc(isUnknownSources());

        final SecurityLibResult atestResults = new SecurityLibResult() {
            @Override
            public void onResultRecieved(CheckResults result) {
                fullResult.setBit(result.isBit());
                fullResult.setAct(result.isAct());
                results.onResultRecieved(fullResult);

            }

            @Override
            public void onBooleanCheckResult(Boolean result) {

            }

            @Override
            public void onErrorOccured(String error) {
                results.onErrorOccured(error);
            }
        };

        SecurityLibResult phaResults = new SecurityLibResult() {
            @Override
            public void onResultRecieved(CheckResults result) {

            }

            @Override
            public void onBooleanCheckResult(Boolean result) {
                fullResult.setPha(result);
                attestDevice(apikey, atestResults);
            }

            @Override
            public void onErrorOccured(String error) {
                results.onErrorOccured(error);
            }
        };

        isHarmfullApp(phaResults);

    }
}
