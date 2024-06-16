package AsyncTasks;

public interface SendCodeCallback {
    void onCodeSent(boolean success);
    void onCodeValidated(boolean success);
    void onFieldsValidated(boolean success);
}