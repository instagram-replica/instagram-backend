package services.users.validation;

public class ValidationResult {
    public ValidationResultType type;
    public String message;

    public ValidationResult(ValidationResultType type) {
        this.type = type;
    }

    public ValidationResult(ValidationResultType type, String message) {
        this.type = type;
        this.message = message;
    }
}
