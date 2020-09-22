package cn.liusiqian.reflectdemo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import cn.liusiqian.reflectdemo.model.AbsModel;
import cn.liusiqian.reflectdemo.model.AnnotationModel;
import cn.liusiqian.reflectdemo.model.BaseModel;
import cn.liusiqian.reflectdemo.model.MyAnnotation;
import cn.liusiqian.reflectdemo.model.SubModel;
import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
//    testGetMethods();
//    callStaticAndAbstract();
//    callVariableParam();
//    setValueForFinalField();
//    callSuperClassMethodWithSubClassInstance();
//    getAnnotationValue();
//    getParameterizedType();
//    callConstructorWithUnsafe();
//    findClassInAnotherLoader();
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

  /**
   * 如何获取类注解/方法注解的运行时值？
   */
  private void getAnnotationValue() {
    Class<AnnotationModel> cls = AnnotationModel.class;
    if (cls.isAnnotationPresent(MyAnnotation.class)) {
      MyAnnotation annotation = cls.getAnnotation(MyAnnotation.class);
      if (annotation != null) {
        String value = annotation.value();
        log("class annotation value:" + value);
      }
    }

    try {
      Field typeField = cls.getDeclaredField("type");
      if (typeField.isAnnotationPresent(MyAnnotation.class)) {
        MyAnnotation annotation = typeField.getAnnotation(MyAnnotation.class);
        if (annotation != null) {
          log("field type annotation value:" + annotation.value());
        }
      }
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
  }


  /**
   * 如何获得泛型参数/泛型返回值的运行时值？
   */
  private void getParameterizedType() {
    try {
      Field field = SubModel.class.getField("valueT");
      log("GenericType:" + field.getGenericType());

      //从父类继承来的对象用getGenericSuperclass()
      //从接口实现来的对象用getGenericInterfaces()
      Type type = SubModel.class.getGenericSuperclass();
      if (type instanceof ParameterizedType) {
        Type[] types = ((ParameterizedType) type).getActualTypeArguments();

        for (Type t : types) {
          log("type:" + t);
        }
      }
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }


    //这里的list2是继承ArrayList<Integer>的子类，其父类是ArrayList<Integer>，只有父类/父接口拥有泛型参数才可以获得对应的泛型参数
    List<Integer> list = new ArrayList<>();
    List<Integer> list2 = new ArrayList<Integer>() {};
    getListParameterizedType(list);
    getListParameterizedType(list2);
  }

  private void getListParameterizedType(List<Integer> list2) {
    Type type = list2.getClass().getGenericSuperclass();
    if (type instanceof ParameterizedType) {
      Type[] types = ((ParameterizedType) type).getActualTypeArguments();

      for (Type t : types) {
        log("type:" + t);
      }
    }
  }

  /**
   * 没有默认的构造方法时，如何通过Class对象创建这个类的实例?
   * 例子：Gson.fromJson()内部实现
   */
  private void callConstructorWithUnsafe() {
    try {
      Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
      Field field = unsafeClass.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      final Object unsafe = field.get(null);
      final Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
      Object model = allocateInstance.invoke(unsafe, AnnotationModel.class);
      if (model instanceof AnnotationModel) {
        log(model.toString());
      }
    } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  /**
   * Class.forName()能否找到同一个进程中不在同一个ClassLoader中的类？
   */
  private void findClassInAnotherLoader() {
    String dexPath = FileUtils.copyFiles(getApplicationContext(), "remote.dex");
    String cacheDir = FileUtils.getCacheDir(getApplicationContext()).getAbsolutePath();
    log("dexPath:" + dexPath + "\ncacheDir:" + cacheDir);

    DexClassLoader loader = new DexClassLoader(dexPath, cacheDir, null, getClassLoader());
    try {
      Class cls = loader.loadClass("cn.liusiqian.reflectdemo.model.RemoteDexModel");
      if (cls != null) {
        Constructor constructor = cls.getConstructor();
        Object object = constructor.newInstance();
        Method method = cls.getDeclaredMethod("testMethod");
        method.invoke(object);
      }
    } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
      e.printStackTrace();
    }

    //尝试用反射寻找 cn.liusiqian.reflectdemo.model.RemoteDexModel
    try {
      Class searchCls = Class.forName("cn.liusiqian.reflectdemo.model.RemoteDexModel");
      if (searchCls != null) {
        Constructor constructor = searchCls.getConstructor();
        Object object = constructor.newInstance();
        Method method = searchCls.getDeclaredMethod("testMethod");
        method.invoke(object);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void log(String message) {
    Log.d("TestTag", message);
  }
}
