package cn.liusiqian.reflectdemo.model;

import android.util.Log;

public class SubModel extends BaseModel {
  static {
    TAG = "SubModel";
  }

  private static void subStaticMethod() {
    Log.d(TAG, "call sub static method");
  }

  public void subPublicMethod() {
    Log.d(TAG, "call sub Public Method");
  }

  protected void subProtectedMethod() {
    Log.d(TAG, "call sub protected Method");
  }

  private void subPrivateMethod() {
    Log.d(TAG, "call sub private Method");
  }

  void subPackagePrivateMethod() {
    Log.d(TAG, "call sub package-private Method");
  }

  @Override
  protected void absMethod() {
    Log.d(TAG, "SubModel call absMethod");
  }
}
