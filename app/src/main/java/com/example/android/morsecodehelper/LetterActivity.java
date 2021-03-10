package com.example.android.morsecodehelper;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LetterActivity extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    double speed = 1.0;
    private CameraManager mCameraManager;
    private String mCameraId;
    private MediaPlayer mp;
    private boolean busy;
    private boolean audio_enabled;
    private boolean light_enabled;


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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        GridView gridview = (GridView) view.findViewById(R.id.grid_view);
        gridview.setAdapter(new ButtonAdapter(getActivity()));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                onClickHandler(gridview, position);
            }
        });

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

    private void onClickHandler(GridView view, int position){
        char character = (char)view.getAdapter().getItem(position);
        String code = MorseCodes.get(character).getCode();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        if (!busy) {
            executor.execute(() -> {
                busy = true;
                doMorseCode(code, audio_enabled, light_enabled, speed);
                handler.post(() -> {
                    busy = false;
                });
            });
        }
    }


    private void doMorseCode(String sequence, boolean use_audio, boolean use_light, double speed) {
        int time = 0;
        for (int i=0; i < sequence.length(); i++){

            if(sequence.charAt(i) == '·')
                time = 250;
            else if (sequence.charAt(i) == '-')
                time = 750;

            doBeep(time, use_audio, use_light, speed);

        }
    }

    private void doBeep(long time, boolean use_audio, boolean use_light, double speed) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}