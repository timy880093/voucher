package com.gateweb.voucher.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class ReflectUtils {

  /**
   * 反射 setter，找 obj 中的 propertyName，並設成 value，若找不到 propertyName 則不做事
   *
   * @param obj
   * @param propertyName
   * @param value
   */
  public static void invokeSetter(Object obj, String propertyName, Object value) {
    // 若為基本型別 or null，直接 return
    if (isNullOrBasicType(obj)) {
      return;
    }

    try {
      final PropertyDescriptor pd = new PropertyDescriptor(propertyName, obj.getClass());
      final Method setter = pd.getWriteMethod();
      setter.invoke(obj, value);
    } catch (NullPointerException e) {
      e.printStackTrace();
    } catch (Throwable e) {
      log.error(e.getMessage());
    }
  }

  /**
   * 反射 getter，取 obj 中的 propertyName 值，若找不到 propertyName 則回傳 obj
   *
   * @param obj
   * @param propertyName
   * @return
   */
  // 反射 getter
  public static Object invokeGetter(Object obj, String propertyName) {
    if (obj == null) {
      return null;
    }
    // 若為以下型別，直接 return，因為只有單個值，不含其他屬性
    if (obj instanceof String
        || obj instanceof Integer
        || obj instanceof Float
        || obj instanceof BigDecimal
        || obj instanceof Date
        || obj instanceof LocalDate
        || obj instanceof LocalDateTime
        || obj instanceof Boolean) {
      return obj;
    }
    // 若為物件，透過反射，找物件裡對應的屬性值
    Object result = obj;
    try {
      //            log.debug("invokeGetter , propertyName({}), obj({})", propertyName, obj);
      final PropertyDescriptor pd = new PropertyDescriptor(propertyName, obj.getClass());
      final Method getter = pd.getReadMethod();
      result = getter.invoke(obj);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  public static boolean isNullOrBasicType(Object obj) {
    return obj == null
        || obj instanceof Character
        || obj instanceof Number
        || obj instanceof Date
        || obj instanceof LocalDate
        || obj instanceof LocalDateTime
        || obj instanceof Boolean;
    //        || Boolean.class.equals(obj.getClass()) ;
  }
}
