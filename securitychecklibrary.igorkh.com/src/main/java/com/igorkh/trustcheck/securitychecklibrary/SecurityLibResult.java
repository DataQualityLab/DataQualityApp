package com.igorkh.trustcheck.securitychecklibrary;

import android.support.annotation.CheckResult;

/**
 * Created by Igor Khokhlov on 11/14/2017.
 */

public interface SecurityLibResult {
    public void onResultRecieved(CheckResults result);
    public void onBooleanCheckResult(Boolean result);
//    public void onIntCheckResult(int result);
    public void onErrorOccured(String error);
}
