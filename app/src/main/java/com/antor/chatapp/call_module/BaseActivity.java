package com.antor.chatapp.call_module;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.antor.chatapp.BaseApplication;
import com.antor.chatapp.Model.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public abstract class BaseActivity extends AppCompatActivity implements ServiceConnection {
    protected String[] permissionsRecord = {Manifest.permission.VIBRATE, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    protected String[] permissionsContact = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    protected String[] permissionsStorage = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    protected String[] permissionsCamera = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    protected String[] permissionsSinch = {Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.READ_PHONE_STATE};
    protected User userMe, user;
    protected Helper helper;

    protected DatabaseReference usersRef, groupRef, chatRef, statusRef;
    private FirebaseApp secondApp;
    private FirebaseDatabase secondDatabase;
    private SinchService.SinchServiceInterface mSinchServiceInterface;

//    //Group updates receiver(new or updated)
//    private BroadcastReceiver groupReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction() != null && intent.getAction().equals(Helper.BROADCAST_GROUP)) {
//                Group group = intent.getParcelableExtra("data");
//                String what = intent.getStringExtra("what");
//                switch (what) {
//                    case "added":
//                        groupAdded(group);
//                        break;
//                    case "changed":
//                        groupUpdated(group);
//                        break;
//                }
//            }
//        }
//    };
//
//    //User updates receiver(new or updated)
//    private BroadcastReceiver userReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction() != null && intent.getAction().equals(Helper.BROADCAST_USER)) {
//                User user = intent.getParcelableExtra("data");
//                String what = intent.getStringExtra("what");
//                switch (what) {
//                    case "added":
//                        userAdded(user);
//                        break;
//                    case "changed":
//                        userUpdated(user);
//                        Intent local = new Intent("custom-event-name");
//                        local.putExtra("status", user.getStatus());
//                        LocalBroadcastManager.getInstance(BaseActivity.this).sendBroadcast(local);
//                        break;
//                }
//            }
//        }
//    };
//
//    private BroadcastReceiver statusReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction() != null && intent.getAction().equals(Helper.BROADCAST_STATUS)) {
//                Status status = intent.getParcelableExtra("data");
//                String what = intent.getStringExtra("what");
//                switch (what) {
//                    case "added":
//                        statusAdded(status);
//                        break;
//                    case "changed":
//                        statusUpdated(status);
//                        break;
//                }
//            }
//        }
//    };
//
//    private BroadcastReceiver myUsersReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            ArrayList<User> myUsers = intent.getParcelableArrayListExtra("data");
//            if (myUsers != null) {
//                myUsersResult(myUsers);
//            }
//        }
//    };
//
//    private BroadcastReceiver myContactsReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            ArrayList<Contact> myContacts = intent.getParcelableArrayListExtra("data");
//            if (myContacts != null) {
//                myContactsResult(myContacts);
//            }
//        }
//    };




    abstract void onSinchConnected();

    abstract void onSinchDisconnected();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new Helper(this);
        usersRef = BaseApplication.getUserRef();
        groupRef = BaseApplication.getGroupRef();
        chatRef = BaseApplication.getChatRef();
        statusRef = BaseApplication.getStatusRef();
//
//        Intent intent = new Intent(this, FirebaseChatService.class);
//
//
//        try {
//            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startForegroundService(intent);
//            } else {*/
//            startService(intent);
//            //  }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        startService(new Intent(this, FirebaseChatService.class));
        getApplicationContext().bindService(new Intent(this, SinchService.class), this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
//        localBroadcastManager.registerReceiver(userReceiver, new IntentFilter(Helper.BROADCAST_USER));
//        localBroadcastManager.registerReceiver(groupReceiver, new IntentFilter(Helper.BROADCAST_GROUP));
//        localBroadcastManager.registerReceiver(myContactsReceiver, new IntentFilter(Helper.BROADCAST_MY_CONTACTS));
//        localBroadcastManager.registerReceiver(myUsersReceiver, new IntentFilter(Helper.BROADCAST_MY_USERS));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
//        localBroadcastManager.unregisterReceiver(userReceiver);
//        localBroadcastManager.unregisterReceiver(groupReceiver);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
            onSinchConnected();

            if (userMe.getUsername() != null) {
                mSinchServiceInterface.startClient(userMe.getUsername());
            }

        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = null;
            onSinchDisconnected();
        }
    }

    protected SinchService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }

    protected boolean permissionsAvailable(String[] permissions) {
        boolean granted = true;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                granted = false;
                break;
            }
        }
        return granted;
    }
}
