/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.castremotedisplay;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.support.v7.media.MediaRouter.RouteInfo;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CastRemoteDisplayActivity extends ActionBarActivity {

    public final static String TAG = "CastRDisplayActivity";
    private final Map<String, StateMap> statesMap = new HashMap<String, StateMap>()
    {{
            put(StateMap.selectEntertainment, new StateMap(StateMap.selectActivities, StateMap.selectHealth, StateMap.selectActivities, StateMap.selectHealth, StateMap.expandEntertainment));
            put(StateMap.selectHealth, new StateMap(StateMap.selectEntertainment, StateMap.selectActivities, StateMap.selectEntertainment, StateMap.selectActivities, StateMap.expandHealth));
            put(StateMap.selectActivities, new StateMap(StateMap.selectHealth, StateMap.selectEntertainment, StateMap.selectHealth, StateMap.selectEntertainment, StateMap.expandActivities));
            put(StateMap.expandEntertainment, new StateMap(StateMap.selectEntertainmentItem1, StateMap.selectEntertainmentItem1, StateMap.selectEntertainmentItem1, StateMap.selectEntertainmentItem1, null));
            put(StateMap.selectEntertainmentItem1, new StateMap(StateMap.selectEntertainmentItem2, StateMap.selectEntertainmentItem2, StateMap.selectEntertainmentItem2, StateMap.selectEntertainmentItem2, StateMap.engageEntertainmentItem1));
            put(StateMap.selectEntertainmentItem2, new StateMap(StateMap.selectEntertainmentItem1, StateMap.selectEntertainmentItem1, StateMap.selectEntertainmentItem1, StateMap.selectEntertainmentItem1, StateMap.engageEntertainmentItem2));
            put(StateMap.expandHealth, new StateMap(StateMap.selectHealthItem1, StateMap.selectHealthItem1, StateMap.selectHealthItem1, StateMap.selectHealthItem1, null));
            put(StateMap.selectHealthItem1, new StateMap(StateMap.selectHealthItem5, StateMap.selectHealthItem2, StateMap.selectHealthItem5, StateMap.selectHealthItem2, null));
            put(StateMap.selectHealthItem2, new StateMap(StateMap.selectHealthItem1, StateMap.selectHealthItem3, StateMap.selectHealthItem1, StateMap.selectHealthItem3, null));
            put(StateMap.selectHealthItem3, new StateMap(StateMap.selectHealthItem2, StateMap.selectHealthItem4, StateMap.selectHealthItem2, StateMap.selectHealthItem4, null));
            put(StateMap.selectHealthItem4, new StateMap(StateMap.selectHealthItem3, StateMap.selectHealthItem5, StateMap.selectHealthItem3, StateMap.selectHealthItem5, null));
            put(StateMap.selectHealthItem5, new StateMap(StateMap.selectHealthItem4, StateMap.selectHealthItem1, StateMap.selectHealthItem4, StateMap.selectHealthItem1, null));
            put(StateMap.expandActivities, new StateMap(StateMap.selectActivitiesItem1, StateMap.selectActivitiesItem1, StateMap.selectActivitiesItem1, StateMap.selectActivitiesItem1, null));
            put(StateMap.selectActivitiesItem1, new StateMap(StateMap.selectActivitiesItem4, StateMap.selectActivitiesItem2, StateMap.selectActivitiesItem4, StateMap.selectActivitiesItem2, null));
            put(StateMap.selectActivitiesItem2, new StateMap(StateMap.selectActivitiesItem1, StateMap.selectActivitiesItem3, StateMap.selectActivitiesItem1, StateMap.selectActivitiesItem3, null));
            put(StateMap.selectActivitiesItem3, new StateMap(StateMap.selectActivitiesItem2, StateMap.selectActivitiesItem4, StateMap.selectActivitiesItem2, StateMap.selectActivitiesItem4, null));
            put(StateMap.selectActivitiesItem4, new StateMap(StateMap.selectActivitiesItem3, StateMap.selectActivitiesItem1, StateMap.selectActivitiesItem3, StateMap.selectActivitiesItem1, null));
        }};

    // Second screen
    private Toolbar mToolbar;

    // MediaRouter
    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;

    private GoogleApiClient mApiClient;
    private HelloWorldChannel mHelloWorldChannel;
    private CastDevice mCastDevice;
    private Cast.Listener mCastListener;
    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks;
    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener;
    private String mLastState;

    /**
     * Initialization of the Activity after it is first created. Must at least
     * call {@link android.app.Activity#setContentView setContentView()} to
     * describe what is to be displayed in the screen.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.second_screen_layout);
        setupActionBar();

        // Local UI
        Button button = (Button) findViewById(R.id.up_button);
        button.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  sendMessageBasedOnChosenState(StateMap.StatesEnum.UpState);
              }
          });

        button = (Button) findViewById(R.id.down_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageBasedOnChosenState(StateMap.StatesEnum.DownState);
            }
        });

        button = (Button) findViewById(R.id.left_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageBasedOnChosenState(StateMap.StatesEnum.LeftState);
            }
        });

        button = (Button) findViewById(R.id.right_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageBasedOnChosenState(StateMap.StatesEnum.RightState);
            }
        });

        button = (Button) findViewById(R.id.click_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLastState!=null && mLastState.startsWith("select-")) {
                    sendMessageBasedOnChosenState(StateMap.StatesEnum.ClickState);
                }
            }
        });

        button = (Button) findViewById(R.id.back_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLastState!=null)
                    if (mLastState.startsWith("select-"))
                    {
                        if (mLastState.contains("-item-"))
                        {
                            String lastExpandedState = "expand-"+mLastState.split("-")[1];
                            sendMessage(lastExpandedState);
                        }
                        else
                            sendMessage("select");
                    }
                    else
                    if (mLastState.startsWith("expand-"))
                    {
                        String lastExpandedState = mLastState.split("-")[1];
                        sendMessage("expand");
                        sendMessage("select-"+lastExpandedState);
                    }
            }
        });

        mMediaRouter = MediaRouter.getInstance(getApplicationContext());
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(
                        CastMediaControlIntent.categoryForCast(getString(R.string.app_id)))
                .build();

        CastDevice castDevice = CastDevice
                .getFromBundle(mMediaRouter.getSelectedRoute().getExtras());
        mCastDevice = castDevice;
        launchReceiver();

        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    private void sendMessageBasedOnChosenState(StateMap.StatesEnum chosenState) {
        if (mLastState == null)
            sendMessage(getString(R.string.message_bus_initial_state));
        else
            sendMessage(statesMap.get(mLastState).getState(chosenState));
    }

    /**
     * Start the receiver app
     */
    private void launchReceiver() {
        try {
            mCastListener = new Cast.Listener() {
                @Override
                public void onApplicationDisconnected(int errorCode) {
                    Log.d(TAG, "application has stopped");
                }
            };
            // Connect to Google Play services
            mConnectionCallbacks = new ConnectionCallbacks();
            mConnectionFailedListener = new ConnectionFailedListener();

            Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions
                    .builder(mCastDevice, mCastListener);

            mApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Cast.API, apiOptionsBuilder.build())
                    .addConnectionCallbacks(mConnectionCallbacks)
                    .addOnConnectionFailedListener(mConnectionFailedListener)
                    .build();

            mApiClient.connect();
        } catch (Exception e) {
            Log.e(TAG, "Failed launchReceiver", e);
        }
    }

    private void setupActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
    }

    /**
     * Create the toolbar menu with the cast button.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider =
                (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);
        // Return true to show the menu.
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectFromCastAndReceiver();
    }

    private void disconnectFromCastAndReceiver() {
        if (mApiClient != null)
            mApiClient.disconnect();

        mCastDevice = null;
        mMediaRouter.removeCallback(mMediaRouterCallback);
    }

    private void initError() {
        Toast toast = Toast.makeText(
                getApplicationContext(), R.string.init_error, Toast.LENGTH_SHORT);
        mMediaRouter.selectRoute(mMediaRouter.getDefaultRoute());
        toast.show();
    }

    private final MediaRouter.Callback mMediaRouterCallback =
            new MediaRouter.Callback() {
                @Override
                public void onRouteSelected(MediaRouter router, RouteInfo info) {
                    // Should not happen since this activity will be closed if there
                    // is no selected route
                }

                @Override
                public void onRouteUnselected(MediaRouter router, RouteInfo info) {
                    disconnectFromCastAndReceiver();
                    Intent intent = new Intent(CastRemoteDisplayActivity.this,
                            MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            };

    /**
     * Send a text message to the receiver
     *
     * @param message
     */
    private void sendMessage(String message) {
        if (mApiClient != null && mHelloWorldChannel != null) {
            try {
                Cast.CastApi.sendMessage(mApiClient,
                        getString(R.string.message_bus_namespace), message)
                        .setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status result) {
                                if (!result.isSuccess()) {
                                    Log.e(TAG, "Sending message failed");
                                }
                            }
                        });
            } catch (Exception e) {
                Log.e(TAG, "Exception while sending message", e);
            }
        } else {
            Toast.makeText(CastRemoteDisplayActivity.this, message, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * Custom message channel
     */
    class HelloWorldChannel implements Cast.MessageReceivedCallback {

        /**
         * @return custom namespace
         */
        public String getNamespace() {
            return getString(R.string.message_bus_namespace);
        }

        /*
         * Receive message from the receiver app
         */
        @Override
        public void onMessageReceived(CastDevice castDevice, String namespace,
                                      String message) {
            Log.d(TAG, "onMessageReceived: " + message);
            if (message.indexOf("-")!=-1)
                mLastState = message;
            else
                mLastState = null;
        }
    }

    /**
     * Google Play services callbacks
     */
    private class ConnectionCallbacks implements
            GoogleApiClient.ConnectionCallbacks {

        @Override
        public void onConnected(Bundle connectionHint) {
            Log.d(TAG, "onConnected");

            if (mApiClient == null) {
                // We got disconnected while this runnable was pending
                // execution.
                return;
            }

            // Launch the receiver app
            Cast.CastApi.launchApplication(mApiClient, getString(R.string.app_id), false)
                    .setResultCallback(
                            new ResultCallback<Cast.ApplicationConnectionResult>() {
                                @Override
                                public void onResult(
                                        Cast.ApplicationConnectionResult result) {
                                    Status status = result.getStatus();
                                    Log.d(TAG,
                                            "ApplicationConnectionResultCallback.onResult:"
                                                    + status.getStatusCode());
                                    if (status.isSuccess()) {
                                        ApplicationMetadata applicationMetadata = result
                                                .getApplicationMetadata();
                                        String mSessionId = result.getSessionId();
                                        String applicationStatus = result
                                                .getApplicationStatus();
                                        boolean wasLaunched = result.getWasLaunched();
                                        Log.d(TAG, "application name: "
                                                + applicationMetadata.getName()
                                                + ", status: " + applicationStatus
                                                + ", sessionId: " + mSessionId
                                                + ", wasLaunched: " + wasLaunched);

                                        // Create the custom message
                                        // channel
                                        mHelloWorldChannel = new HelloWorldChannel();
                                        try {
                                            Cast.CastApi.setMessageReceivedCallbacks(
                                                    mApiClient,
                                                    mHelloWorldChannel.getNamespace(),
                                                    mHelloWorldChannel);
                                        } catch (IOException e) {
                                            Log.e(TAG, "Exception while creating channel",
                                                    e);
                                        }

                                        // set the initial instructions
                                        // on the receiver
//                                        sendMessage(getString(R.string.message_bus_initial_instructions));
                                    } else {
                                        Log.e(TAG, "application could not launch");
                                    }
                                }
                            });
        }

        @Override
        public void onConnectionSuspended(int cause) {
            Log.d(TAG, "onConnectionSuspended");
        }
    }

    /**
     * Google Play services callbacks
     */
    private class ConnectionFailedListener implements
            GoogleApiClient.OnConnectionFailedListener {

        @Override
        public void onConnectionFailed(ConnectionResult result) {
            Log.e(TAG, "onConnectionFailed ");
        }
    }
}
