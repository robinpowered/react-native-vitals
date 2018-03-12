package com.robinpowered.react.vitals;

import android.os.Environment;
import android.os.StatFs;
import android.os.Build;
import android.os.Debug;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.app.ActivityManager;
import android.content.Context;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import java.util.Map;
import java.util.HashMap;
import javax.annotation.Nullable;

import java.io.File;

public class RNVitalsModule extends ReactContextBaseJavaModule implements ComponentCallbacks2, LifecycleEventListener {

  public static final String MODULE_NAME = "RNVitals";
  public static final String LOW_MEMORY = "LOW_MEMORY";
  public static final String MEMORY_LEVEL_KEY = "MemoryLevel";

  public static final int MEMORY_MODERATE = TRIM_MEMORY_RUNNING_MODERATE;
  public static final int MEMORY_LOW = TRIM_MEMORY_RUNNING_LOW;
  public static final int MEMORY_CRITICAL = TRIM_MEMORY_RUNNING_CRITICAL;


  public RNVitalsModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  private double toMB(long num) {
    return (double) (num / 1024 / 1024);
  }

  @Override
  public String getName() {
    return MODULE_NAME;
  }

  @Override
  public @Nullable Map<String, Object> getConstants() {
    HashMap<String, Object> memoryLevelConstants = new HashMap<String, Object>();
    memoryLevelConstants.put("CRITICAL", MEMORY_CRITICAL);
    memoryLevelConstants.put("LOW", MEMORY_LOW);
    memoryLevelConstants.put("MODERATE", MEMORY_MODERATE);

    HashMap<String, Object> constants = new HashMap<String, Object>();
    constants.put(LOW_MEMORY, LOW_MEMORY);
    constants.put(MEMORY_LEVEL_KEY, memoryLevelConstants);

    return constants;
  }

  private WritableMap getMemoryInfo() {
    ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
    ActivityManager activityManager = (ActivityManager) getReactApplicationContext()
        .getSystemService(Context.ACTIVITY_SERVICE);
    activityManager.getMemoryInfo(mi);

    Debug.MemoryInfo memInfo = new Debug.MemoryInfo();
    Debug.getMemoryInfo(memInfo);
    long appUsed = memInfo.getTotalPss() * 1024L;

    WritableMap info = Arguments.createMap();
    info.putDouble("systemTotal", toMB(mi.totalMem));
    info.putDouble("appUsed", toMB(appUsed));
    info.putDouble("systemFree", toMB(mi.availMem));
    info.putDouble("systemUsed", toMB(mi.totalMem - mi.availMem));
    return info;
  }

  @Override
  public void onTrimMemory(int level) {
    ReactApplicationContext context = getReactApplicationContext();
    if (context.hasActiveCatalystInstance()) {
      WritableMap memoryInfo = getMemoryInfo();
      memoryInfo.putInt("memoryLevel", level);
      context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(LOW_MEMORY, memoryInfo);
    }
  }

  @Override
  public void onLowMemory() {
    // no-op
  }

  @Override
  public void initialize() {
    getReactApplicationContext().addLifecycleEventListener(this);
    getReactApplicationContext().registerComponentCallbacks(this);
  }

  @Override
  public void onHostResume() {
    getReactApplicationContext().registerComponentCallbacks(this);
  }

  @Override
  public void onHostPause() {
    getReactApplicationContext().unregisterComponentCallbacks(this);
  }

  @Override
  public void onHostDestroy() {
    getReactApplicationContext().unregisterComponentCallbacks(this);
    getReactApplicationContext().removeLifecycleEventListener(this);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    // no-op
  }

  @ReactMethod
  public void getStorage(Promise promise) {
    File path = Environment.getDataDirectory();
    StatFs stat = new StatFs(path.getPath());

    long totalSpace;
    long freeSpace;

    if (Build.VERSION.SDK_INT >= 18) {
      totalSpace = stat.getTotalBytes();
      freeSpace = stat.getFreeBytes();
    } else {
      long blockSize = stat.getBlockSize();
      totalSpace = blockSize * stat.getBlockCount();
      freeSpace = blockSize * stat.getAvailableBlocks();
    }

    WritableMap info = Arguments.createMap();
    info.putDouble("total", toMB(totalSpace));
    info.putDouble("free", toMB(freeSpace));
    long usedSpace = totalSpace - freeSpace;
    info.putDouble("used", toMB(usedSpace));

    promise.resolve(info);
  }

  @ReactMethod
  public void getMemory(Promise promise) {
    promise.resolve(getMemoryInfo());
  }
}
