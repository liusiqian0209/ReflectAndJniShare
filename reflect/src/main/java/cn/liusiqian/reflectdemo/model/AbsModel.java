package cn.liusiqian.reflectdemo.model;

import android.util.Log;

public abstract class AbsModel {

  protected static String TAG = "AbsModel";

  protected abstract void absMethod();

  private static void absStaticMethod() {
    Log.d(TAG, "call abs static method");
  }
}
