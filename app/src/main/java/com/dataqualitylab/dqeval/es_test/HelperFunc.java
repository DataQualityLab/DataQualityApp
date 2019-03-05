package com.dataqualitylab.dqeval.es_test;

import android.os.Build;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class HelperFunc {

    public static int getSecPatchAge()
    {
        String patchdate = Build.VERSION.SECURITY_PATCH;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date patchDate = new Date();
        try {
            patchDate = dateFormat.parse(patchdate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Calendar patchCal = Calendar.getInstance();
        Calendar currentCal = Calendar.getInstance();
        patchCal.setTime(patchDate);
        currentCal.setTime(Calendar.getInstance().getTime());

        int yearsInBetween = currentCal.get(Calendar.YEAR) - patchCal.get(Calendar.YEAR);
        int monthsDiff = currentCal.get(Calendar.MONTH) - patchCal.get(Calendar.MONTH);
        long ageInMonths = yearsInBetween*12 + monthsDiff;

        return (int) ageInMonths;
    }
}
