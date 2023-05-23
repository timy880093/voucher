package com.gateweb.voucher.model.validate.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.gateweb.voucher.model.validate.constraint.IsoDateValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * <p>Validate Buyer or Seller is Valid TaxIdNumber, maybe is '0000000000'</p>
 */
@Constraint(validatedBy = IsoDateValidator.class) //具體的實現類
@Target( { FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE }) // 註釋可以使用的地方
@Retention(RUNTIME) // 運行時使用
@Documented
public @interface IsoDate {
    String message() default "{}"; //提示信息,可以寫死,可以填寫國際化的key


    //下面這兩個屬性必須添加
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


    @Target({ TYPE, ANNOTATION_TYPE })
    @Retention(RUNTIME)
    @Documented
    @interface List {
        IsoDate[] value();
    }
}