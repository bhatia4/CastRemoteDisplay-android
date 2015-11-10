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
    public static final String selectEntertainmentItem1 = "select-entertainment-item-1";
    public static final String selectEntertainmentItem2 = "select-entertainment-item-2";
    public static final String engageEntertainmentItem1 = "engage-entertainment-item-1";
    public static final String engageEntertainmentItem2 = "engage-entertainment-item-2";
    public static final String expandHealth = "expand-health";
    public static final String selectHealthItem1 = "select-health-item-1";
    public static final String selectHealthItem2 = "select-health-item-2";
    public static final String selectHealthItem3 = "select-health-item-3";
    public static final String selectHealthItem4 = "select-health-item-4";
    public static final String selectHealthItem5 = "select-health-item-5";

    public static final String expandActivities = "expand-activities";
    public static final String selectActivitiesItem1 = "select-activities-item-1";
    public static final String selectActivitiesItem2 = "select-activities-item-2";
    public static final String selectActivitiesItem3 = "select-activities-item-3";
    public static final String selectActivitiesItem4 = "select-activities-item-4";

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
