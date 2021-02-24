package com.example.android.morsecodehelper;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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


public class PhraseActivity extends Fragment implements View.OnClickListener {

    private CameraManager mCameraManager;
    private String mCameraId;
    private EditText mTextInput;
    private TextView mViewMorseCode;
    private Button confirmButton;
    private MediaPlayer mp;
    boolean busy;
    private float speed = (float) 1.0;
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
        String morseCode = "";

        CharacterIterator it = new StringCharacterIterator(text);
        while (it.current() != CharacterIterator.DONE) {
            String letterCode = MorseCodes.get(it.current()).getCode();
            morseCode = morseCode + letterCode + " ";
            it.next();
        }

        return morseCode;
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

    private class MorseCodeExecutor implements Runnable{
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
                else if (sequence.charAt(i) == '/'){
                    try { TimeUnit.MILLISECONDS.sleep((long)(500*speed)); }
                    catch (InterruptedException e) { e.printStackTrace(); }
                }
                else if (sequence.charAt(i) == ' '){
                    try { TimeUnit.MILLISECONDS.sleep((long)(500*speed)); }
                    catch (InterruptedException e) { e.printStackTrace(); }
                }
            }
            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    confirmButton.setText(R.string.confirm_button);
                    busy = false;
                }
            });
        }
    }
}