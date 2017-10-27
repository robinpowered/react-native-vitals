
package com.reactlibrary;

import android.os.Environment;
import android.os.StatFs;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

import java.io.File;

public class RNVitalsModule extends ReactContextBaseJavaModule {

  public static final String MODULE_NAME = "RNVitals";

  public RNVitalsModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  public String getName() {
    return MODULE_NAME;
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
    info.putDouble("totalSpace", (double)totalSpace);
    info.putDouble("freeSpace", (double)freeSpace);
    promise.resolve(info);
  }

  @ReactMethod
  public void getMemory(Promise promise) {
    Runtime runtime = Runtime.getRuntime();

    long usedMemInMB=(runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
    long maxHeapSizeInMB=runtime.maxMemory() / 1048576L;

    WritableMap info = Arguments.createMap();
    info.putDouble("totalMemory", (double)maxHeapSizeInMB);
    info.putDouble("freeMemory", (double)usedMemInMB);
    promise.resolve(info);
  }
}
