package cn.liusiqian.jnidemo;

import android.util.Log;

public class NativeErrorHandler {
  private static final String TAG = "JniDemoTAG--NativeErrorHandler";

  public static void onNativeError(int signum) {
    Log.e(TAG,
        "onNativeError -- Thread:" + Thread.currentThread().getName() + "  Signal:" + signum);
  }
}
