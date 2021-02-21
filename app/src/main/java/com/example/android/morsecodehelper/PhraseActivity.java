package com.example.android.morsecodehelper;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class PhraseActivity extends Fragment implements View.OnClickListener {

    private CameraManager mCameraManager;
    private String mCameraId;
    private EditText mTextInput;
    private TextView mViewMorseCode;
    boolean busy;
    private float speed = (float) 1.0;


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

        Button confirmButton = view.findViewById(R.id.button_confirm);
        mTextInput = view.findViewById(R.id.textInput);
        confirmButton.setOnClickListener(this);
        mViewMorseCode = view.findViewById(R.id.tv_morse_code_result);

        return view;
    }


    @Override
    public void onClick(View v) {
        String insertedText = mTextInput.getText().toString().toUpperCase();

        if(!checkStringIsMorseComplaint(insertedText)) {
            Toast.makeText(getActivity(), R.string.invalid_input, Toast.LENGTH_LONG).show();
            return;
        }

        String resultedMorseCode = textToMorseCode(insertedText);
        mViewMorseCode.setText(resultedMorseCode);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        if (!busy) {
            executor.execute(() -> {
                busy = true;
                doMorseCode(resultedMorseCode);
                handler.post(() -> {
                    busy = false;
                });
            });
        }
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

    private void doMorseCode(String sequence) {
        for (int i=0; i < sequence.length(); i++){
            if(sequence.charAt(i) == '·'){
                turnOnLight(250);
            }
            else if (sequence.charAt(i) == '-'){
                turnOnLight(750);
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