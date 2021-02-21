package com.example.android.morsecodehelper;

public enum MorseCodes {
    A ("·-"),
    B ("-···"),
    C ("-·-·"),
    D ("-··"),
    E ("·"),
    F ("··-·"),
    G ("--·"),
    H ("····"),
    I ("··"),
    J ("·---"),
    K ("-·-"),
    L ("·-··"),
    M ("--"),
    N ("-·"),
    O ("---"),
    P ("·--·"),
    Q ("--·-"),
    R ("·-·"),
    S ("···"),
    T ("-"),
    U ("··-"),
    V ("···-"),
    W ("·--"),
    X ("-··-"),
    Y ("-·--"),
    Z ("--··"),
    _1 ("·----"),
    _2 ("··---"),
    _3 ("···--"),
    _4 ("····-"),
    _5 ("·····"),
    _6 ("-····"),
    _7 ("--···"),
    _8 ("---··"),
    _9 ("----·"),
    _0 ("-----"),
    SPACE ("/");

    private final String code;

    MorseCodes(String code) {
        this.code = code;
    }

    public static MorseCodes get(char character){
        if (Character.isLetter(character)){
            return MorseCodes.valueOf(String.valueOf(character));
        }
        else if (Character.isDigit(character)){
            return MorseCodes.valueOf("_" + character);
        }
        else if (character == ' '){
            return MorseCodes.valueOf("SPACE");
        }
        return null;
    }

    public String getCode(){
        return this.code;
    }
}
