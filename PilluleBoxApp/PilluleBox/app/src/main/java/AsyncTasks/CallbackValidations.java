package AsyncTasks;

public interface CallbackValidations {
    void onCodeSent(boolean success);
    void onCodeValidated(boolean success);
    void onFieldsValidated(boolean success);
    void onTokenValidated(boolean success);
}