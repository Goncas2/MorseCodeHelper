package com.example.android.morsecodehelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class PhraseActivity extends Fragment implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private CameraManager mCameraManager;
    private String mCameraId;
    private EditText mTextInput;
    private TextView mViewMorseCode;
    private Button confirmButton;
    private MediaPlayer mp;
    private boolean busy;
    private double speed = 1.0;
    private boolean audio_enabled;
    private boolean light_enabled;
    private MorseCodeExecutor runnable;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        setupPreferences();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_phrase, container, false);

        confirmButton = view.findViewById(R.id.button_confirm);
        mTextInput = view.findViewById(R.id.textInput);
        confirmButton.setOnClickListener(this);
        mViewMorseCode = view.findViewById(R.id.tv_morse_code_result);

        mp = MediaPlayer.create(getContext(), R.raw._440);

        return view;
    }

    private void setupPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        audio_enabled = sharedPreferences.getBoolean("use_audio", true);
        light_enabled = sharedPreferences.getBoolean("use_light", true);
        speed = sharedPreferences.getInt("speed_setting", 100) / 100.0;

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }


    @Override
    public void onClick(View v) {
        if(busy){
            confirmButton.setText(R.string.confirm_button);
            runnable.stopThread();
            return;
        }

        busy = true;
        String insertedText = mTextInput.getText().toString().toUpperCase();

        if(!checkStringIsMorseComplaint(insertedText)) {
            Toast.makeText(getActivity(), R.string.invalid_input, Toast.LENGTH_LONG).show();
            return;
        }

        String resultMorseCode = textToMorseCode(insertedText);
        mViewMorseCode.setText(resultMorseCode);

        runnable = new MorseCodeExecutor(resultMorseCode);
        Thread thread = new Thread(runnable);
        thread.start();

        confirmButton.setText(R.string.cancel_button);
    }

    private boolean checkStringIsMorseComplaint(String string){
        return string.matches("[A-Z0-9 ]+");
    }

    private String textToMorseCode(String text) {
        StringBuilder morseCode = new StringBuilder();

        CharacterIterator it = new StringCharacterIterator(text);
        String letterCode;
        while (it.current() != CharacterIterator.DONE) {
            if(MorseCodes.get(it.current()) != null) {
                letterCode = MorseCodes.get(it.current()).getCode();
                morseCode.append(letterCode).append(" ");
            }
            it.next();
        }

        return morseCode.toString();
    }

    private void turnOnLight(long time, boolean use_audio, boolean use_light, double speed) {
        try {
            if (use_light) {
                mCameraManager.setTorchMode(mCameraId, true);
            }
            if (use_audio) {
                mp.start();
            }

            TimeUnit.MILLISECONDS.sleep((long) (time/speed));

            if (use_light) {
                mCameraManager.setTorchMode(mCameraId, false);
            }
            if (use_audio) {
                mp.stop();
                mp = MediaPlayer.create(getContext(), R.raw._440);
            }

            TimeUnit.MILLISECONDS.sleep((long) (250/speed));

        } catch (CameraAccessException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals("use_audio")) {
            audio_enabled = sharedPreferences.getBoolean(key, true);
        }

        if (key.equals("use_light")) {
            light_enabled = sharedPreferences.getBoolean(key, true);
        }

        if (key.equals("speed_setting")) {
            speed = sharedPreferences.getInt(key, 100) / 100.0;
        }
    }

    private class MorseCodeExecutor implements Runnable {
        private volatile boolean running = true;
        private String sequence;

        MorseCodeExecutor(String s){
            sequence = s;
        }

        public void stopThread() {
            running = false;
        }

        @Override
        public void run() {
            for (int i=0; i < sequence.length(); i++){
                if(!running){
                    busy = false;
                    return;
                }
                if(sequence.charAt(i) == '·'){
                    turnOnLight(250, audio_enabled, light_enabled, speed);
                }
                else if (sequence.charAt(i) == '-'){
                    turnOnLight(750, audio_enabled, light_enabled, speed);
                }
                else if (sequence.charAt(i) == '/'){
                    try { TimeUnit.MILLISECONDS.sleep((long)(500*speed)); }
                    catch (InterruptedException e) { e.printStackTrace(); }
                }
                else if (sequence.charAt(i) == ' '){
                    try { TimeUnit.MILLISECONDS.sleep((long)(500*speed)); }
                    catch (InterruptedException e) { e.printStackTrace(); }
                }
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    confirmButton.setText(R.string.confirm_button);
                    busy = false;
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}