package com.couple.kristjanthor.appforcouple;

/**
 * Created by kristjanthorhedinsson on 26/05/16.
 */
public class Messages {

    private static Messages mInstance = null;

    private String mString;
    private String mInput;


    private Messages(){
        mString = "";
        mInput = "";
    }

    public static Messages getInstance(){
        if(mInstance == null)
        {
            mInstance = new Messages();
        }
        return mInstance;
    }

    public String getString(){
        return this.mString;
    }

    public String getInput(){
        return this.mInput;
    }

    public void setString(String value){
        mString = value;
    }

    public void setInput(String value){
        mInput = value;
    }
}