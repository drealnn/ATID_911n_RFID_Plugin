package com.atid.app.myRfid;

import com.atid.lib.dev.ATRfidManager;
import com.atid.lib.dev.ATRfidReader;
import com.atid.lib.dev.event.RfidReaderEventListener;
import com.atid.lib.dev.rfid.type.ActionState;
import com.atid.lib.dev.rfid.type.ConnectionState;
import com.atid.lib.dev.rfid.type.ResultCode;
import com.atid.lib.dev.rfid.type.TagType;
import com.atid.lib.dev.rfid.param.RangeValue;
import com.atid.lib.dev.rfid.exception.ATRfidReaderException;


import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle; 
import android.os.PowerManager;
import android.os.Vibrator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Context;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Rfid extends CordovaPlugin implements RfidReaderEventListener {

private static final String TAG = "MainActivity"; 

protected ATRfidReader mReader;
private PowerManager.WakeLock mWakeLock = null;
private SoundPool mSoundPool;
private int mBeepSuccess;
private int mBeepFail;
private Vibrator mVibrator;

private CallbackContext keyup_callback = null;
private CallbackContext keydown_callback = null;
private CallbackContext onReaderReadTag_callback = null;
private CallbackContext onReaderResult_callback = null;
private View currentView = null;


//Context context=this.cordova.getActivity().getApplicationContext();

@Override
public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
    result.setKeepCallback(true);

    // lifecycle functions //

    if (action.equals("deinitalize")){
        this.deinitalize();
        return true;
    }
    else if (action.equals("wakeup")){
        Log.d(TAG, "+- wakeup scanner");
        
        if(mReader != null)
            ATRfidManager.wakeUp();
        return true;
    }
    else if (action.equals("sleep")){
        Log.d(TAG, "+- sleep scanner");
        if(mReader != null) {
            ATRfidManager.sleep();
        }
        return true;
    }
    else if (action.equals("pause_scanner")){
        Log.d(TAG, "+- pause scanner");
        if (mReader != null)
            mReader.removeEventListener(this);
        return true;
    }
    else if (action.equals("resume_scanner")){
        Log.d(TAG, "+- resume scanner");
        if (mReader != null)
            mReader.setEventListener(this);
        return true;
    }

    // getters and setters // 

    else if (action.equals("getPowerRange")){
        Log.d(TAG, "++Get Power Range");
        callbackContext.success(new JSONObject("{\'min\': \'" + mReader.getPowerRange().getMin() + "\' , \'max\' : \'" + mReader.getPowerRange().getMax() + "\' }"));
        Log.d(TAG, "--Get Power Range");
        
        return true;
    }
    else if (action.equals("getPower")){
        Log.d(TAG, "++Get Power");
        callbackContext.success("" + mReader.getPower());
        Log.d(TAG, "--Get Power");
        
        return true;
    }
    else if (action.equals("getOperationTime")){
        Log.d(TAG, "++Get OperationTime");
        callbackContext.success("" + mReader.getOperationTime());
        Log.d(TAG, "--Get OperationTime");
        
        return true;
    }
    else if (action.equals("setPower")){
        Log.d(TAG, "++set power level");
        try {
            mReader.setPower(args.getInt(0));
            callbackContext.success("successfully set power level");
        } catch (ATRfidReaderException e) {
            Log.e(TAG, String.format(
                    "ERROR. saveOption() - Failed to set power level [%s]",
                    e.getCode()), e);
            callbackContext.error("failed to set power level");
        }
        
        Log.d(TAG, "--set power level");
        
        return true;
    }
    else if (action.equals("setOperationTime")){
        Log.d(TAG, "++set operation time");
        
        try {
            mReader.setOperationTime(args.getInt(0));
            callbackContext.success("successfully set operation time");
        }catch (ATRfidReaderException e) {
            Log.e(TAG, String.format(
                    "ERROR. saveOption() - Failed to set operation Time [%s]",
                    e.getCode()), e);
            callbackContext.error("failed to operation time");
        }

        Log.d(TAG, "--set operation time");
        
        return true;
    }
    else if (action.equals("setInventoryTime")){
        Log.d(TAG, "++setInventoryTime");
        
        try {
            mReader.setInventoryTime(args.getInt(0));
            callbackContext.success("successfully setInventoryTime");
        }catch (ATRfidReaderException e) {
            Log.e(TAG, String.format(
                    "ERROR. saveOption() - Failed to setInventoryTime [%s]",
                    e.getCode()), e);
            callbackContext.error("failed to setInventoryTime");
        }

        Log.d(TAG, "--setInventoryTime");
        
        return true;
    }
    else if (action.equals("setIdleTime")){
        Log.d(TAG, "++ setIdleTime");
        
        try {
            mReader.setIdleTime(mIdleTime);
            callbackContext.success("successfully setIdleTime");
        } catch (ATRfidReaderException e) {
            Log.e(TAG, String.format(
                    "ERROR. saveOption() - Failed to set idle Time [%s]",
                    e.getCode()), e);
            callbackContext.error("failed setIdleTime");
        }

        Log.d(TAG, "-- setIdleTime");
        
        return true;
    }
    
    // Reading and Writing //
    
    else if (action.equals('start_readContinuous'))
    {
        startAction(TagType.Tag6C, true, callbackContext);
    }
    else if (action.equals('start_readSingle'))
    {
        startAction(TagType.Tag6C, false, callbackContext);
    }
    else if (action.equals('stop_read'))
    {
        stopAction(callbackContext);
    }
    


    // Events //
   
    else if(action.equalsIgnoreCase("register_keyDown")){
            this.keydown_callback = callbackContext;
            return true;
    }
    else if(action.equalsIgnoreCase("register_keyUp")){
            this.keyup_callback = callbackContext;
            return true;
    }
    else if(action.equalsIgnoreCase("register_decode")){
            this.getDecode_callback = callbackContext;
            return true;
    }
    else if(action.equalsIgnoreCase("onReaderReadTag")){
            this.onReaderReadTag_callback = callbackContext;
            return true;
    }
    else if(action.equalsIgnoreCase("onReaderResult")){
            this.onReaderResult_callback = callbackContext;
            return true;
    }
    return false;
}

@Override
public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    if ((mReader = ATRfidManager.getInstance()) == null) {
            Log.e(TAG, "Failure to initialize RFID device. Aborting...");
            return;
    }
    
    // Initialize Sound Pool
    //this.mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    //this.mBeepSuccess = this.mSoundPool.load(context, R.raw.success, 1);
    //this.mBeepFail = this.mSoundPool.load(context, R.raw.fail, 1);
    // Initialize Vibrator
    /*this.mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);*/
    
    this.currentView = webView.getView();
    this.currentView.setOnKeyListener(
                new View.OnKeyListener(){
                    @Override
                    public boolean onKey(View view, int keyCode, KeyEvent event){
                        return doKey(view, keyCode, event);
                    }
                }
            );

    Log.i(TAG, "RFID device initialized");
}


private void deinitalize(){
    Log.d(TAG, "+++ onDeinitalize");
        
    ATRfidManager.onDestroy();

    Log.d(TAG, "--- onDeinitalize");
}

protected void startAction(TagType tagType, boolean isContinuous, CallBackContext callbackContext) {

    ResultCode res;

    if (isContinuous) {
        // Multi Reading
        switch (tagType) {
        case Tag6C:
            if ((res = mReader.inventory6cTag()) != ResultCode.NoError) {
                Log.e(TAG,
                        String.format(
                                "ERROR. startAction() - Failed to start inventory 6C tag [%s]",
                                res));

                 callbackContext.error(String.format(
                                "ERROR. startAction() - Failed to start inventory 6C tag [%s]",
                                res));
                return;
            }
            break;
        case Tag6B:
            if ((res = mReader.inventory6bTag()) != ResultCode.NoError) {
                Log.e(TAG,
                        String.format(
                                "ERROR. startAction() - Failed to start inventory 6B tag [%s]",
                                res));
               
                return;
            }
            break;
        }
    } else {
        // Single Reading
        switch (tagType) {
        case Tag6C:
            if ((res = mReader.readEpc6cTag()) != ResultCode.NoError) {
                Log.e(TAG,
                        String.format(
                                "ERROR. startAction() - Failed to start read 6C tag [%s]",
                                res));
                callbackContext.error(String.format(
                                "ERROR. startAction() - Failed to start read 6C tag [%s]",
                                res));
                return;
            }
            break;
        case Tag6B:
            if ((res = mReader.readEpc6bTag()) != ResultCode.NoError) {
                Log.e(TAG,
                        String.format(
                                "ERROR. startAction() - Failed to start read 6B tag [%s]",
                                res));
                
                return;
            }
            break;
        }
    }
    callbackContext.success(isContinuous ? "successfully reading continuously" : "successfully read single");
    Log.i(TAG, "INFO. startAction()");
}

protected void stopAction(CallBackContext callbackContext) {

    ResultCode res;

    if ((res = mReader.stop()) != ResultCode.NoError) {
        Log.e(TAG, String.format(
                "ERROR. stopAction() - Failed to stop operation [%s]", res));
        callbackContext.error(String.format(
                "ERROR. stopAction() - Failed to stop operation [%s]", res));
        return;
    }

    callbackContext.success("successfully stopped reading operation");

    Log.i(TAG, "INFO. stopAction()");
}


/*
    ####### onReader event functions 
*/

@Override
public void onReaderActionChanged(ATRfidReader reader, ActionState action) {

    if (action == ActionState.Stop) {
      //  adpTags.shutDown();
    } else {
        //adpTags.start();
    }

    //enableWidgets(true);

    Log.i(TAG, String.format("EVENT. onReaderActionchanged(%s)", action));
}

@Override
public void onReaderReadTag(ATRfidReader reader, String tag, float rssi) {

    //adpTags.addItem(tag, rssi);
    //txtCount.setText(String.format("%d", adpTags.getCount()));
    //playSuccess();

    Log.i(TAG,
            String.format("EVENT. onReaderReadTag([%s], %.2f)", tag, rssi));
    if (this.onReaderReadTag == null)
        return false;
    
    try {
        String str = "{\'tag\':\'" + tag + "\' , \'rssi\': \'" + rssi + "\' }"
        PluginResult result = new PluginResult(PluginResult.Status.OK, new JSONObject(str));
        result.setKeepCallback(true);
        this.onReaderReadTag_callback.sendPluginResult(result);
        return true;
    } catch(Exception e)
    {
        e.printStackTrace();
        PluginResult result = new PluginResult(PluginResult.Status.ERROR, "Error in handling onReaderReadTag event");
        result.setKeepCallback(true);
        this.onReaderReadTag_callback.sendPluginResult(result);
        return false;
    }
}

@Override
public void onReaderResult(ATRfidReader reader, ResultCode code,
        ActionState action, String epc, String data) {
    Log.i(TAG, String.format("EVENT. onReaderResult(%s, %s, [%s], [%s]",
            code, action, epc, data));

    if (this.onReaderResult == null)
        return false;
    
    try {
        String str = String.format("{\'code\': \'%s\', \'action\': \'%s\', \'epc\':\'%s\', \'data\':\'%s\'}", code, action, epc, data);
        PluginResult result = new PluginResult(PluginResult.Status.OK, new JSONObject(str));
        result.setKeepCallback(true);
        this.onReaderResult_callback.sendPluginResult(result);
        return true;
    } catch(Exception e)
    {
        e.printStackTrace();
        PluginResult result = new PluginResult(PluginResult.Status.ERROR, "Error in handling onReaderResult event");
        result.setKeepCallback(true);
        this.onReaderResult_callback.sendPluginResult(result);
        return false;
    }
}

@Override
public void onReaderStateChanged(ATRfidReader reader, ConnectionState state) {
    Log.i(TAG, String.format("EVENT. onReaderStateChanged(%s)", state));
}

public boolean doKey(View v, int keyCode, KeyEvent event) {
    
Log.i(TAG, "triggering key event");
    if (event.getAction() == KeyEvent.ACTION_UP) {
        return KeyUp(keyCode, event);
    }
    else if (event.getAction() == KeyEvent.ACTION_DOWN) {
        return KeyDown(keyCode, event);
    }
    return false;
}

private boolean KeyDown(int keyCode, KeyEvent event){
    //if(keydown_callback == null){
      //  return true;
    //}
    //PluginResult result = new PluginResult(PluginResult.Status.OK, Integer.toString(keyCode));
    //result.setKeepCallback(true);
    //keydown_callback.sendPluginResult(result);
    Log.i(TAG, "key down pressed " + keyCode);

    if (this.keydown_callback == null)
        return false;
    
    try {
        String str = "";
        if (event != null) {
            str = String.valueOf((char) event.getUnicodeChar());
        } else {
            str = String.valueOf(Character.toChars(keyCode)[0]);
        }
        
        PluginResult result = new PluginResult(PluginResult.Status.OK, keyCode + "");
        result.setKeepCallback(true);
        this.keydown_callback.sendPluginResult(result);
        return true;
    } catch(Exception e)
    {
        e.printStackTrace();
        PluginResult result = new PluginResult(PluginResult.Status.ERROR, "Error in handling key event");
        result.setKeepCallback(true);
        this.keydown_callback.sendPluginResult(result);
        return false;
    }
}

private boolean KeyUp(int keyCode, KeyEvent event){
    //if(keyup_callback == null){
      //  return true;
    //}
    //PluginResult result = new PluginResult(PluginResult.Status.OK, Integer.toString(keyCode));
    //result.setKeepCallback(true);
    //keyup_callback.sendPluginResult(result);
    Log.i(TAG, "key up pressed " + keyCode);
    if (this.keyup_callback == null)
        return false;
    
    try {
        String str = "";
        if (event != null) {
            str = String.valueOf((char) event.getUnicodeChar());
        } else {
            str = String.valueOf(Character.toChars(keyCode)[0]);
        }
        
        PluginResult result = new PluginResult(PluginResult.Status.OK, keyCode + "");
        result.setKeepCallback(true);
        this.keyup_callback.sendPluginResult(result);
        return true;
    } catch(Exception e)
    {
        e.printStackTrace();
        PluginResult result = new PluginResult(PluginResult.Status.ERROR, "Error in handling key event");
        result.setKeepCallback(true);
        this.keyup_callback.sendPluginResult(result);
        return false;
    }

}

protected void playSuccess() {
    //mSound.playSuccess();
}

protected void playFail() {
    //mSound.playFail();
}

}

