package cn.liusiqian.reflectdemo.model;

import android.util.Log;

public class SubModel extends BaseModel {
  static {
    TAG = "SubModel";
  }

  public static final String STATIC_FINAL_CONST = "STATIC_FINAL_CONST";
  private final String NON_STATIC_FINAL_CONST = "NON_STATIC_FINAL_CONST";

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

  private String subVariableParamMethod(int split, String... args) {
    if (args == null || args.length == 0) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < args.length - 1; i++) {
      sb.append(args[i]).append("_").append(split).append("_");
    }
    sb.append(args[args.length - 1]);

    return sb.toString();
  }

  public String getNonStaticFinalConst() {
    return NON_STATIC_FINAL_CONST;
  }

  @Override
  public void overrideMethod() {
    Log.d(TAG, "overrideMethod in SubModel");
  }

  @Override
  protected void absMethod() {
    Log.d(TAG, "SubModel call absMethod");
  }
}
