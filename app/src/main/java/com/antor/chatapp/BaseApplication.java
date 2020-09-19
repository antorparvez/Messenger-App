package com.antor.chatapp;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.antor.chatapp.Model.User;
import com.antor.chatapp.call_module.Helper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class BaseApplication extends Application implements LifecycleObserver {

    private static FirebaseDatabase firebaseDatabase;
    private static DatabaseReference userRef, chatRef, groupsRef, statusRef;
    private static boolean isInBackground = false;
    protected Helper helper;
    protected User userMe;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //  MobileAds.initialize(this, "Statusr-app-id");
        init();
    }

    private void init() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference(Helper.REF_DATA).child(Helper.REF_USER);
        chatRef = firebaseDatabase.getReference(Helper.REF_DATA).child(Helper.REF_CHAT);
        groupsRef = firebaseDatabase.getReference(Helper.REF_DATA).child(Helper.REF_GROUP);
        statusRef = firebaseDatabase.getReference(Helper.REF_DATA).child(Helper.REF_STATUS_NEW);

    }


    public static DatabaseReference getUserRef() {
        if (userRef == null) {
            userRef = firebaseDatabase.getReference(Helper.REF_DATA).child(Helper.REF_USER);
            userRef.keepSynced(true);
        }
        return userRef;
    }

    public static DatabaseReference getChatRef() {
        if (chatRef == null) {
            chatRef = firebaseDatabase.getReference(Helper.REF_DATA).child(Helper.REF_CHAT);
            chatRef.keepSynced(true);
        }
        return chatRef;
    }

    public static DatabaseReference getGroupRef() {
        if (groupsRef == null) {
            groupsRef = firebaseDatabase.getReference(Helper.REF_DATA).child(Helper.REF_GROUP);
            groupsRef.keepSynced(true);
        }
        return groupsRef;
    }

    public static DatabaseReference getStatusRef() {
        if (statusRef == null) {
            statusRef = firebaseDatabase.getReference(Helper.REF_DATA).child(Helper.REF_STATUS_NEW);
            statusRef.keepSynced(true);
        }
        return statusRef;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
        markOnline(false);
        Helper.CURRENT_CHAT_ID = null;
        Log.d("MyApp", "App in background");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {
        markOnline(true);
        Log.d("MyApp", "App in foreground");
    }

    private void markOnline(boolean b) {
        helper = new Helper(this);
        userMe = helper.getLoggedInUser();
        if (userMe != null && userMe.getId() != null) {
            getUserRef().child(userMe.getId()).child("timeStamp").setValue(System.currentTimeMillis());
            getUserRef().child(userMe.getId()).child("online").setValue(b);
        }
    }
}
