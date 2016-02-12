package com.socialgalaxy.tunnel;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;


public class BASE extends Activity {
    private boolean keyPressed = false;
    private boolean SCREEN_ON = true;
    private boolean mLoaded = false;
    public static String SVOICE_PACKAGE = "vom.vlingo.midas";
    public static String SVOICE_ACTIVITY = "vom.vlingo.midas.gui.ConversationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = 1F;
        getWindow().setAttributes(layout);
        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);
        KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();

    }

    public void clearHome(){
        PackageManager p = getPackageManager();
        ComponentName cN = new ComponentName(this, WEB.class);
        p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
        p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("BASE", String.format("onTouchEvent %d" ,event.getPointerCount()));
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        keyPressed = true;

        return true;

    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {

        keyPressed = true;

        return true;

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        keyPressed = false;

        return true;

    }
    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (blockedKeys.contains(event.getKeyCode())) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

		if (!hasFocus) {
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
//			windowCloseHandler.postDelayed(windowCloserRunnable, 0);
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    @Override
    protected void onPause() {
        super.onPause();

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }

	private void toggleRecents() {
		Intent closeRecents = new Intent(
				"com.android.systemui.recent.action.TOGGLE_RECENTS");
		closeRecents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		ComponentName recents = new ComponentName("com.android.systemui",
				"com.android.systemui.recent.RecentsActivity");
		closeRecents.setComponent(recents);
		this.startActivity(closeRecents);
	}

	private void toggleSVoice(){
		
		Intent closeRecents = new Intent(
				"com.vlingo.client.app.action.APPLICATION_STATE_CHANGED");
		closeRecents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		ComponentName recents = new ComponentName(SVOICE_PACKAGE,
				SVOICE_ACTIVITY);
		closeRecents.setComponent(recents);
		this.startActivity(closeRecents);
	}
	
	private Handler windowCloseHandler = new Handler();
	private Runnable windowCloserRunnable = new Runnable() {
		@Override
		public void run() {
			ActivityManager am = (ActivityManager) getApplicationContext()
					.getSystemService(Context.ACTIVITY_SERVICE);
		
			
			ComponentName cn1 = am.getRunningTasks(2).get(1).baseActivity;
			ComponentName cn2 = am.getRunningTasks(2).get(0).baseActivity;

			if ((cn1 != null)
					&& (cn1.getClassName().equals(
							"com.sec.android.app.easylauncher.Launcher"))) {
				toggleRecents();
			}else if((cn1 != null) && (cn1.getClassName().equals(
					SVOICE_ACTIVITY))){
				toggleSVoice(); 
			}else{				
				Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
				getApplicationContext().sendBroadcast(it); 
			}
		}
	};

    public void startRestartAppCountDown(){
        restartApp.postDelayed(restartAppRunnable, 10000);
    }

    private Handler restartApp = new Handler();
    private Runnable restartAppRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i("restartAndroidApp", "restartAndroidApp");
            Intent i = new Intent();
            i.setClassName(getString(R.string.autoboot_package), getString(R.string.autoboot_activity));
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    };
}
