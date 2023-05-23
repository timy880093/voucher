// package com.gateweb.voucher.component;
//
// import static java.util.stream.Collectors.toList;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Set;
// import javax.validation.ConstraintValidatorContext;
// import javax.validation.ConstraintViolation;
// import javax.validation.Validator;
// import org.apache.commons.lang3.StringUtils;
// import org.springframework.stereotype.Component;
//
// @Component
// public class SimpleValidator<T> {
//
//    private final Validator validator;
//    private final ObjectMapper objectMapper;
//
//    public SimpleValidator(Validator validator, ObjectMapper objectMapper) {
//        this.validator = validator;
//        this.objectMapper = objectMapper;
//    }
//
//    public List<ErrorInfo> validate(T obj) {
//        final Set<ConstraintViolation<T>> violations = validator.validate(obj);
//        final List<ErrorInfo> list = new ArrayList<>();
//        violations.forEach(err -> {
//            err.getInvalidValue();
//        });
//        return ErrorInfo.builder()
//                .field(violations.)
//                .value()
//                .message()
//                .build();
//    }
//
//    private static final String SEPARATOR = "|||";
//
//    /**
//     * 以下方法在 類級約束 且 驗證失敗 才需要加上這段，需多加boolean變數如isValid搭配判斷使用 例如 isValid() 傳入的是，BeanObject(自定義) 和
//     * ConstraintValidatorContext 因為傳入整個 Bean類別，預設不知道是哪個欄位錯誤，若驗證失敗： 1. 需先使用
//     * disableDefaultConstraintViolation將預設值清空 2. 再 Build MessageTemplate，並使用
//     * addPropertyNode添加錯誤欄位名，再 addConstraintViolation()加回驗證器
//     *
//     * @param context
//     * @param field
//     * @param message
//     */
//    public static boolean parseValidatorContext(ConstraintValidatorContext context, String field,
//            String message) {
//        message = StringUtils.isBlank(message) ? context.getDefaultConstraintMessageTemplate()
//                : message;
//        if (StringUtils.isAnyBlank(field, message)) {
//            return true;
//        }
//
//        // reset message
//        context.disableDefaultConstraintViolation();
//        // add new message
//        context.buildConstraintViolationWithTemplate(message)
//                .addPropertyNode(field)
//                .addConstraintViolation();
//        return false;
//    }
//
//    public static boolean exportError(ConstraintValidatorContext context,
//            ErrorInfo validateError) {
//        if (validateError == null) {
//            return true;
//        }
//        final String message = StringUtils.isBlank(validateError.getMessage())
//                ? context.getDefaultConstraintMessageTemplate()
//                : validateError.getMessage();
//
//        // reset message
//        context.disableDefaultConstraintViolation();
//        // add new message
//        context.buildConstraintViolationWithTemplate(message)
//                .addPropertyNode(validateError.getFieldInfo())
//                .addConstraintViolation();
//        return false;
//    }
//
//    public static boolean parseValidatorContext(ConstraintValidatorContext context,
//            SimplifiedFields field, Exception e) {
//        return parseValidatorContext(context, field.field(), e.getMessage());
//    }
//
//    public static boolean parseValidatorContext(ConstraintValidatorContext context,
//            List<SimplifiedFields> fileds,
//            List<ErrorCodes03> messages) {
//        if (messages.isEmpty()) {
//            return true;
//        }
//        final List<String> fieldList = fileds.stream().map(SimplifiedFields::field)
//                .collect(toList());
//        final String fieldString = String.join("|||", fieldList);
//        final List<String> messageList = messages.stream().map(ErrorCodes03::message)
//                .collect(toList());
//        final String messageString = String.join("|||", messageList);
//        return parseValidatorContext(context, fieldString, messageString);
//    }
//
//    public static void parseValidatorContext(ConstraintValidatorContext context,
//            SimplifiedFields field) {
//        parseValidatorContext(context, field.field(), null);
//    }
//
//    //  private void transformErrors(ConstraintViolation cv, List<ErrorDto> errorDtos) {
////    String field = cv.getPropertyPath().toString();
////    String message = cv.getMessage();
////    Object originalValue = cv.getInvalidValue();
////    List<String> fields;
////    List<String> messages;
////    // 若單個驗證器有多個錯誤，將 field && message 拆分，返回 List<ErrorDto>
////    if (field.contains("|||") || message.contains("|||")) {
////      fields = new ArrayList<>(Arrays.asList(StringUtils.split(field, "|||")));
////      messages = new ArrayList<>(Arrays.asList(StringUtils.split(message, "|||")));
////      errorDtos.addAll(ErrorDto.createList(fields, originalValue, messages));
////    } else { // 若只有單個錯誤，則正常返回單個 ErrorDto
////      errorDtos.add(ErrorDto.create(field, originalValue, message));
////    }
////  }
//
//    public static <T> List<ErrorInfo> toValidateError(Set<ConstraintViolation<T>> cvs) {
//        return cvs.stream()
//                .map(SimpleValidator::toValidateError)
//                .flatMap(List::stream)
//                .collect(toList());
//    }
//
//    public static <T> List<ErrorInfo> toValidateError(ConstraintViolation<T> cv) {
//        final List<ErrorInfo> errorDtos = new ArrayList<>();
//        final String field = cv.getPropertyPath().toString();
//        final String message = cv.getMessage();
//        final Object originalValue = cv.getInvalidValue();
//
//        // 若單個驗證器有多個錯誤，將 field && message 拆分，返回 List<ErrorDto>
//        if (field.contains(SEPARATOR) || message.contains(SEPARATOR)) {
//            final List<String> fields = new ArrayList<>(
//                    Arrays.asList(StringUtils.split(field, SEPARATOR)));
//            final List<String> messages = new ArrayList<>(
//                    Arrays.asList(StringUtils.split(message, SEPARATOR)));
//            for (int i = 0, size = fields.size(); i < size; i++) {
//                errorDtos.add(ErrorInfo.create(fields.get(i), originalValue,
// messages.get(i)));
//            }
//        } else { //若只有單個錯誤，則正常返回單個 ErrorDto
//            errorDtos.add(ErrorDto.create(field, originalValue, message));
//        }
//        return errorDtos;
//    }
//
//
// }
