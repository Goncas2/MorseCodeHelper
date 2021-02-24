package com.example.android.morsecodehelper;

import androidx.fragment.app.Fragment;

import android.content.Context;
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

public class LetterActivity extends Fragment {

    float speed = (float) 1.0;
    private CameraManager mCameraManager;
    private String mCameraId;
    private MediaPlayer mp;
    private boolean busy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
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

    private void onClickHandler(GridView view, int position){
        char character = (char)view.getAdapter().getItem(position);
        String code = MorseCodes.get(character).getCode();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        if (!busy) {
            executor.execute(() -> {
                busy = true;
                doMorseCode(code);
                handler.post(() -> {
                    busy = false;
                });
            });
        }
    }


    private void doMorseCode(String sequence) {
        for (int i=0; i < sequence.length(); i++){
            if(sequence.charAt(i) == '·'){
                mp.start();
                turnOnLight(250);
                mp.stop();
                mp = MediaPlayer.create(getContext(), R.raw._440);
            }
            else if (sequence.charAt(i) == '-'){
                mp.start();
                turnOnLight(750);
                mp.stop();
                mp = MediaPlayer.create(getContext(), R.raw._440);
            }
        }
    }

    private void turnOnLight(long time) {
        try {
            mCameraManager.setTorchMode(mCameraId, true);

            TimeUnit.MILLISECONDS.sleep((long)(time*speed));

            mCameraManager.setTorchMode(mCameraId, false);

            TimeUnit.MILLISECONDS.sleep((long) (250*speed));

        } catch (CameraAccessException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}