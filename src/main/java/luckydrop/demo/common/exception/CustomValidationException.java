package luckydrop.demo.common.exception;

import java.util.Map;

public class CustomValidationException extends RuntimeException {

    private Map<String, String> errors;

    public CustomValidationException(Map<String, String> errors) {
        super("입력 값이 올바르지 않습니다.");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
