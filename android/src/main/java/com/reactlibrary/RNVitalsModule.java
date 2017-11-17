package com.reactlibrary;

import android.os.Environment;
import android.os.StatFs;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.app.ActivityManager;
import android.content.Context;

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

public class RNVitalsModule extends ReactContextBaseJavaModule implements ComponentCallbacks {

  public static final String MODULE_NAME = "RNVitals";
  public static final String LOW_MEMORY = "LOW_MEMORY";

  public RNVitalsModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  public String getName() {
    return MODULE_NAME;
  }

  @Override
  public @Nullable Map<String, Object> getConstants() {
    HashMap<String, Object> constants = new HashMap<String, Object>();
    constants.put("LOW_MEMORY", LOW_MEMORY);
    return constants;
  }

  private WritableMap getMemoryInfo() {
    ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
    ActivityManager activityManager = (ActivityManager) getReactApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
    activityManager.getMemoryInfo(mi);

    WritableMap info = Arguments.createMap();
    info.putDouble("total", (double) mi.totalMem);
    info.putDouble("free", (double) mi.availMem);
    double usedMemory = (double) (mi.totalMem - mi.availMem);
    info.putDouble("used", usedMemory);
    return info;
  }

  @Override
  public void onLowMemory() {
    ReactApplicationContext context = getReactApplicationContext();
    if (context.hasActiveCatalystInstance()) {
      context
        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
        .emit(LOW_MEMORY, getMemoryInfo());
    }
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

    if (android.os.Build.VERSION.SDK_INT >= 18) {
      totalSpace = stat.getTotalBytes();
      freeSpace = stat.getFreeBytes();
    } else {
      long blockSize = stat.getBlockSize();
      totalSpace = blockSize * stat.getBlockCount();
      freeSpace = blockSize * stat.getAvailableBlocks();
    }

    WritableMap info = Arguments.createMap();
    info.putDouble("total", (double) totalSpace);
    info.putDouble("free", (double) freeSpace);
    double usedSpace = (double) (mi.totalSpace - mi.freeSpace);
    info.putDouble("used", usedSpace);

    promise.resolve(info);
  }

  @ReactMethod
  public void getMemory(Promise promise) {
    promise.resolve(getMemoryInfo());
  }
}
