package cn.liusiqian.jnidemo;

import android.app.Application;

public class MyApplication extends Application {
  private static Application sApp;

  @Override
  public void onCreate() {
    super.onCreate();
    sApp = this;
  }

  public static Application getApp() {
    return sApp;
  }
}
