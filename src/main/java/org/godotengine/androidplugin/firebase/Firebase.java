/**
 * Copyright 2020 Yalcin Ata. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.godotengine.androidplugin.firebase;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;

import com.google.firebase.FirebaseApp;

import org.godotengine.godot.Dictionary;
import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class Firebase extends GodotPlugin {

    private static Activity activity = null;
    private static Godot godot = null;
    private static JSONObject firebaseConfig = new JSONObject();
    private FirebaseApp firebaseApp = null;
    private FrameLayout layout = null;

    public static final String TAG = "Firebase";

    public Firebase(Godot godot) {
        super(godot);
        this.godot = godot;
        activity = godot.getActivity();
    }

    public static JSONObject getConfig() {
        return firebaseConfig;
    }

    @Override
    public String getPluginName() {
        return "Firebase";
    }

    @Override
    public List<String> getPluginMethods() {
        List<String> methods = new ArrayList<String>();

        methods.add("init");

        // ===== AdMob
        methods.add("admob_banner_is_loaded");
        methods.add("admob_banner_show");
        methods.add("admob_banner_get_size");
        methods.add("admob_interstitial_show");
        methods.add("admob_rewarded_video_show");
        methods.add("admob_rewarded_video_request_status");

        // ===== Analytics
        methods.add("analytics_send_custom");
        methods.add("analytics_send_events");

        // ===== Authentication
        methods.add("authentication_get_id_token");
        // ===== Google
        methods.add("authentication_google_sign_in");
        methods.add("authentication_google_sign_out");
        methods.add("authentication_google_is_connected");
        methods.add("authentication_google_get_user");

        // ===== Firestore
        methods.add("firestore_load_document");
        methods.add("firestore_add_document");
        methods.add("firestore_set_document_data");

        // ===== Storage
        methods.add("storage_upload");
        methods.add("storage_download");

        // ===== InAppMessaging
        // Nothing to implement, just adding implementation 'com.google.firebase:firebase-inappmessaging-display:19.0.3' to gradle.conf enables it, done!

        // ===== Cloud Messaging
        methods.add("cloudmessaging_subscribe_to_topic");
        methods.add("cloudmessaging_unsubscribe_from_topic");

        return methods;
    }

    public void init(final int script_id) {

        Utils.logDebug("Firebase.init() called");

        godot.runOnUiThread(new Runnable() {
            public void run() {
                //String fileName = "res://assets/godot-firebase-config.json";
                //String data = Utils.readFromFile(fileName, godot.getContext());
                //data = data.replaceAll("\\s+", "");
                //if (data == null || data.isEmpty()) {
                //    Utils.logDebug("assets read data null or empty? " + data);
                //} else {
                //    Utils.logDebug("read data not empty");
                //}
                String data = "{\n" +
                        "\t\"Analytics\": false,\n" +
                        "\t\"AdMob\": false,\n" +
                        "\t\"AdMobMediationUnity\": false,\n" +
                        "\t\"AdMobAdUnits\": {\n" +
                        "\t\t\"TestAds\": false,\n" +
                        "\t\t\"AppId\": \"ca-app-pub-1864389867652805~8916453299\",\n" +
                        "\t\t\"Banner\": false,\n" +
                        "\t\t\"BannerId\": \"ca-app-pub-1864389867652805/3909353408\",\n" +
                        "\t\t\"BannerGravity\": \"TOP\",\n" +
                        "\t\t\"Interstitial\": false,\n" +
                        "\t\t\"InterstitialId\": \"ca-app-pub-ADMOB_INTERSTITIAL_AD_UNIT_ID\",\n" +
                        "\t\t\"RewardedVideo\": false,\n" +
                        "\t\t\"RewardedVideoId\": \"ca-app-pub-ADMOB_REWARDEDVIDEO_AD_UNIT_ID\"\n" +
                        "\t},\n" +
                        "\t\"Authentication\" : true,\n" +
                        "\t\"Firestore\" : false,\n" +
                        "\t\"Storage\" : false,\n" +
                        "\t\"InAppMessaging\" : {\n" +
                        "\t\t\"hint\" : \"This will always be enabled. To remove In-App Messaging edit gradle.conf and remove implementation 'com.google.firebase:firebase-inappmessaging-display:19.0.3'\"\n" +
                        "\t},\n" +
                        "\t\"CloudMessaging\" : false\n" +
                        "}";

                Utils.setScriptInstance(script_id);
                initFirebase(data);
            }
        });
    }

    private void initFirebase(final String data) {
        Utils.logDebug("Firebase initializing");

        JSONObject config = null;
        firebaseApp = FirebaseApp.initializeApp(activity);

        if (data.length() <= 0) {
            Utils.logDebug("Firebase initialized.");
            return;
        }

        try {
            config = new JSONObject(data);
            firebaseConfig = config;
        } catch (JSONException e) {
            Utils.logDebug("JSON Parse error: " + e.toString());
        }

        // ===== AdMob
        if (config.optBoolean("AdMob", false)) {
            Utils.logDebug("AdMob initializing");
            AdMob.getInstance(activity).init(firebaseApp, layout);
        }

        // ===== Analytics
        if (config.optBoolean("Analytics", false)) {
            Utils.logDebug("Analytics initializing");
            Analytics.getInstance(activity).init(firebaseApp);
        }

        // ===== Authentication
        if (config.optBoolean("Authentication", false)) {
            Utils.logDebug("Authentication initializing");
            Authentication.getInstance(activity).init(firebaseApp);
        }

        // ===== Firestore
        if (config.optBoolean("Firestore", false)) {
            Utils.logDebug("Firestore initializing");
            Firestore.getInstance(activity).init(firebaseApp);
        }

        // ===== Storage
        if (config.optBoolean("Storage", false)) {
            Utils.logDebug("Storage initializing");
            Storage.getInstance(activity).init(firebaseApp, godot);
        }

        // ===== InAppMessaging
        // Just adding implementation 'com.google.firebase:firebase-inappmessaging-display:19.0.3' to gradle.conf enables it, done!
        {
            Utils.logDebug("In-App Messaging initialized");
        }

        // ===== Cloud Messaging
        if (config.optBoolean("CloudMessaging", false)) {
            Utils.logDebug("CloudMessaging initializing");
            CloudMessaging.getInstance(activity).init(firebaseApp);
        }

        Utils.logDebug("Firebase initialized");
    }

    // ===== AdMob
    public boolean admob_banner_is_loaded() {
        return AdMob.getInstance(activity).bannerIsLoaded();
    }

    public void admob_banner_show(final boolean show) {
        godot.runOnUiThread(new Runnable() {
            public void run() {
                AdMob.getInstance(activity).bannerShow(show);
            }
        });
    }

    public Dictionary admob_banner_get_size() {
        return AdMob.getInstance(activity).bannerGetSize();
    }

    public void admob_interstitial_show() {
        godot.runOnUiThread(new Runnable() {
            public void run() {
                AdMob.getInstance(activity).interstitialShow();
            }
        });
    }

    public void admob_rewarded_video_show() {
        godot.runOnUiThread(new Runnable() {
            public void run() {
                AdMob.getInstance(activity).rewardedVideoShow();
            }
        });
    }

    public void admob_rewarded_video_request_status() {
        godot.runOnUiThread(new Runnable() {
            public void run() {
                AdMob.getInstance(activity).rewardedVideoRequestStatus();
            }
        });
    }
    // ===== AdMob ====================================================================================================

    // ===== Analytics
    public void analytics_send_custom(final String key, final String value) {
        if (key.length() <= 0 || value.length() <= 0) {
            return;
        }

        godot.runOnUiThread(new Runnable() {
            public void run() {
                Analytics.getInstance(activity).sendCustom(key, value);
            }
        });
    }

    public void analytics_send_events(final String key, final Dictionary data) {
        if (key.length() <= 0 || data.size() <= 0) {
            return;
        }

        godot.runOnUiThread(new Runnable() {
            public void run() {
                Analytics.getInstance(activity).sendEvents(key, data);
            }
        });
    }
    // ===== Analytics ================================================================================================

    // ===== Authentication
    public void authentication_get_id_token() {
        godot.runOnUiThread(new Runnable() {
            public void run() {
                Authentication.getInstance(activity).getIdToken();
            }
        });
    }

    // ----- Google
    public void authentication_google_sign_in() {
        godot.runOnUiThread(new Runnable() {
            public void run() {
                Authentication.getInstance(activity).signIn();
            }
        });
    }

    public void authentication_google_sign_out() {
        godot.runOnUiThread(new Runnable() {
            public void run() {
                Authentication.getInstance(activity).signOut();
            }
        });
    }

    public boolean authentication_google_is_connected() {
        return Authentication.getInstance(activity).isConnected();
    }

    public String authentication_google_get_user() {
        return Authentication.getInstance(activity).getUserDetails();
    }
    // ===== Authentication ===========================================================================================

    // ===== Firestore
    public void firestore_add_document(final String name, final Dictionary data) {
        godot.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Firestore.getInstance(activity).addDocument(name, data);
            }
        });
    }

    public void firestore_load_document(final String name) {
        godot.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Firestore.getInstance(activity).loadDocuments(name, -1);
            }
        });
    }

    public void firestore_set_document_data(final String colName, final String docName, final Dictionary data) {
        godot.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Firestore.getInstance(activity).setDocumentData(colName, docName, data);
            }
        });
    }
    // ===== Firestore ================================================================================================

    // ===== Storage
    public void storage_upload(final String fileName) {
        godot.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Storage.getInstance(activity).upload(fileName);
            }
        });
    }

    public void storage_download(final String fileName) {
        godot.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Storage.getInstance(activity).download(fileName);
            }
        });
    }
    // ===== Storage ==================================================================================================

    // ===== Cloud Messaging
    public void cloudmessaging_subscribe_to_topic(final String topicName) {
        godot.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CloudMessaging.getInstance(activity).subscribeToTopic(topicName);
            }
        });
    }

    public void cloudmessaging_unsubscribe_from_topic(final String topicName) {
        godot.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CloudMessaging.getInstance(activity).unsubscribeFromTopic(topicName);
            }
        });
    }
    // ===== Cloud Messaging ==========================================================================================

    // Forwarded callbacks you can reimplement, as SDKs often need them.
    public void onMainActivityResult(int requestCode, int resultCode, Intent data) {
        Authentication.getInstance(activity).onActivityResult(requestCode, resultCode, data);
    }

    public void onMainRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    }

    public void onMainPause() {
        AdMob.getInstance(activity).onPause();
        Authentication.getInstance(activity).onPause();
    }

    public void onMainResume() {
        AdMob.getInstance(activity).onResume();
        Authentication.getInstance(activity).onResume();
    }

    public void onMainDestroy() {
        AdMob.getInstance(activity).onStop();
        Authentication.getInstance(activity).onStop();
    }

    @Override
    public View onMainCreate(Activity activity) {
        layout = new FrameLayout(activity);
        return layout;
    }

    public void onGLDrawFrame(GL10 gl) {
    }

    public void onGLSurfaceChanged(GL10 gl, int width, int height) {
    } // Singletons will always miss first 'onGLSurfaceChanged' call.
}
