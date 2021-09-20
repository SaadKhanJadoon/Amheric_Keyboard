package com.amherickeyboard.voicetyping.translator.service;

import android.content.Intent;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.amherickeyboard.voicetyping.translator.activity.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MyService extends JobIntentService {

    @Override
    protected void onHandleWork(@NonNull @NotNull Intent intent) {
        String packageLocal = getPackageName();
        boolean isInputDeviceEnabled = false;
        while (!isInputDeviceEnabled) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            List<InputMethodInfo> list = inputMethodManager.getEnabledInputMethodList();

            // check if our keyboard is enabled as input method
            for (InputMethodInfo inputMethod : list) {
                String packageName = inputMethod.getPackageName();
                if (packageName.equals(packageLocal)) {
                    isInputDeviceEnabled = true;
                }
            }
        }
        // open activity
        Intent newIntent = new Intent(this, MainActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(newIntent);
        MainActivity.Companion.finishActivity();
    }
}