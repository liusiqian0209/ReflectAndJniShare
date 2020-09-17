package cn.liusiqian.reflectdemo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.os.Bundle;
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
    callStaticAndAbstract();
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
    } catch (NoSuchMethodException  | IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }

  }

  private void log(String message) {
    Log.d("TestTag", message);
  }
}
