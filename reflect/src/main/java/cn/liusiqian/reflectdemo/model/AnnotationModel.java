package cn.liusiqian.reflectdemo.model;

@MyAnnotation("classAnnotation")
public class AnnotationModel<T> {

  @MyAnnotation
  private T type;

  public AnnotationModel(T type) {
  }

}
