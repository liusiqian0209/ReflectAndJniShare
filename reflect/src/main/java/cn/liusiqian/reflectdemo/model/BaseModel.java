package cn.liusiqian.reflectdemo.model;

import android.util.Log;

public class BaseModel<T, H> extends AbsModel {

  static {
    TAG = "BaseModel";
  }

  public T valueT;

  protected H valueH;

  private static void staticMethod() {
    Log.d(TAG, "call static method");
  }

  public void publicMethod() {
    Log.d(TAG, "call public method.");
  }

  protected void protectedMethod() {
    Log.d(TAG, "call protected method.");
  }

  private void privateMethod() {
    Log.d(TAG, "call private method.");
  }

  void packagePrivateMethod() {
    Log.d(TAG, "call package-private method.");
  }

  @Override
  protected void absMethod() {
    Log.d(TAG, "BaseModel call absMethod");
  }

  public void overrideMethod() {
    Log.d(TAG, "overrideMethod in BaseModel");
  }
}
