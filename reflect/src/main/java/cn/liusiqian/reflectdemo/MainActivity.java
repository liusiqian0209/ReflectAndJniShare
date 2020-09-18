package cn.liusiqian.reflectdemo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import cn.liusiqian.reflectdemo.model.AbsModel;
import cn.liusiqian.reflectdemo.model.BaseModel;
import cn.liusiqian.reflectdemo.model.SubModel;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
//    testGetMethods();
//    callStaticAndAbstract();
//    callVariableParam();
//    setValueForFinalField();
    callSuperClassMethodWithSubClassInstance();
  }

  /**
   * getMethod() 与 getDeclaredMethod() 的区别？
   */
  private void testGetMethods() {
    Method[] methods = SubModel.class.getMethods();
    if (methods.length > 0) {
      for (int i = 0; i < methods.length; i++) {
        Method method = methods[i];
        log("SubModel GetMethod index:" + i + " " + method.getName());
      }
    }

    methods = SubModel.class.getDeclaredMethods();
    if (methods.length > 0) {
      for (int i = 0; i < methods.length; i++) {
        Method method = methods[i];
        log("SubModel GetDeclaredMethods index:" + i + " " + method.getName());
      }
    }
  }

  /**
   * 能否通过反射调用类的静态方法？
   * 能否通过反射调用类的抽象方法？
   * 对同一个私有方法的不同Method实例调用invoke时，需要每一个实例都调用setAccessible(true)吗？
   */
  private void callStaticAndAbstract() {
    // call static
    try {
      Method method = BaseModel.class.getDeclaredMethod("staticMethod");
      method.setAccessible(true);
      method.invoke(null);

      method = AbsModel.class.getDeclaredMethod("absStaticMethod");
      method.setAccessible(true);
      method.invoke(null);

      Method methodCopy = AbsModel.class.getDeclaredMethod("absStaticMethod");
      methodCopy.invoke(null);

    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }

    // call abstact
    try {
      Method absMethod = AbsModel.class.getDeclaredMethod("absMethod");
      absMethod.setAccessible(true);
      absMethod.invoke(new BaseModel());
      absMethod.invoke(new SubModel());
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  /**
   * 如何通过反射调用有可变参数的方法？
   */
  private void callVariableParam() {
    Method[] methods = SubModel.class.getDeclaredMethods();
    Method searchMethod = null;
    if (methods.length > 0) {
      for (int i = 0; i < methods.length; i++) {
        searchMethod = methods[i];
        if (TextUtils.equals("subVariableParamMethod", searchMethod.getName())) {
          break;
        }
      }
    }
    if (searchMethod != null) {
      log("found subVariableParamMethod");
      Class retTypeCls = searchMethod.getReturnType();
      Class[] paramTypeCls = searchMethod.getParameterTypes();
      log("return type:" + retTypeCls.getSimpleName());
      for (int i = 0; i < paramTypeCls.length; i++) {
        log("param" + i + " type:" + paramTypeCls[i].getSimpleName());
      }

      // call
      searchMethod.setAccessible(true);
      try {
        // crash!
        // java.lang.IllegalArgumentException: Wrong number of arguments; expected 2, got 4
//        Object result = searchMethod.invoke(new SubModel(), 12, "abc", "def", "ghi");

        // 正确调用方式
        Object result = searchMethod.invoke(new SubModel(), 12, new String[]{"abc", "def", "ghi"});
        if (result != null) {
          log("result:" + result);
        }
      } catch (IllegalAccessException | InvocationTargetException e) {
        e.printStackTrace();
      }

    }
  }

  /**
   * 类中用final声明的变量所对应的Field对象，可以通过调用setAccessible方法，随后为它设置一个新的值吗？
   */
  private void setValueForFinalField() {
    try {
      Field field = SubModel.class.getDeclaredField("STATIC_FINAL_CONST");
      field.setAccessible(true);
      field.set(null, "new static final value");
      Object result = field.get(null);
      log("result after set:" + result);
      log("origin field:" + SubModel.STATIC_FINAL_CONST);

      field = SubModel.class.getDeclaredField("NON_STATIC_FINAL_CONST");
      field.setAccessible(true);
      SubModel model = new SubModel();
      field.set(model, "new non-static final value");
      result = field.get(model);
      log("result after set:" + result);
      log("origin field:" + model.getNonStaticFinalConst());
    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  /**
   * 如果子类覆写了基类的方法A，通过基类的Class对象调用getDeclaredMethod()
   * 得到A对应的Method对象，在调用invoke时传递子类的实例，那么最终调用的是子类的方法A还是基类的方法A？
   */
  private void callSuperClassMethodWithSubClassInstance() {
    try {
      Method method = BaseModel.class.getDeclaredMethod("overrideMethod");
      method.setAccessible(true);
      method.invoke(new SubModel());
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }

  }

  private void log(String message) {
    Log.d("TestTag", message);
  }
}
