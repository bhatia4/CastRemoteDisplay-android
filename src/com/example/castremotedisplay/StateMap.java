package com.example.castremotedisplay;

import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by bhatia on 10/25/2015.
 */
public class StateMap {
    public static enum StatesEnum
    {
        LeftState, RightState, UpState, DownState, ClickState;
    }

    public static final String selectEntertainment = "select-entertainment";
    public static final String selectHealth = "select-health";
    public static final String selectActivities = "select-activities";
    public static final String expandEntertainment = "expand-entertainment";
    public static final String expandHealth = "expand-health";
    public static final String expandActivities = "expand-activities";

    private String leftState;
    private String rightState;
    private String upState;
    private String downState;
    private String clickState;

    public StateMap(String leftState, String rightState, String upState, String downState, String clickState) {
        this.leftState = leftState;
        this.rightState = rightState;
        this.upState = upState;
        this.downState = downState;
        this.clickState = clickState;
    }

    public String getLeftState() {
        return leftState;
    }

    public String getRightState() {
        return rightState;
    }

    public String getUpState() {
        return upState;
    }

    public String getDownState() {
        return downState;
    }

    public String getClickState() {
        return clickState;
    }

    public String getState(StatesEnum state) {
        //no paramater
        Class noparams[] = {};
        try {
            Method method = this.getClass().getDeclaredMethod("get" + state.toString(), null);
            return (String) method.invoke(this, null);
        }
        catch (Exception e)
        {
            Log.e(CastRemoteDisplayActivity.TAG, "Failed getState()", e);
        }
        return null;
    }
}
