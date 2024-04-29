package com.gateweb.voucher.model.validate.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.gateweb.voucher.model.validate.constraint.InvoiceAndCommonNumValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = InvoiceAndCommonNumValidator.class) // 具體的實現
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented
public @interface InvoiceAndCommonNum {
  String message() default "{}"; // 提示信息,可以寫死,可以填寫國際化的key

  //    String invoiceNumber();
  //    String commonNumber();
  //    String taxType();
  //    String typeCode();
  //    String customsClearanceMark();

  // 下面這兩個屬性必須添加
  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  @Target({TYPE, ANNOTATION_TYPE})
  @Retention(RUNTIME)
  @Documented
  @interface List {
    InvoiceAndCommonNum[] value();
  }
}
