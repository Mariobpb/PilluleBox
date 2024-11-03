package Models;

public class Answer {
    private String text;
    private Integer nextQuestionId;
    private Integer contextId;

    public Answer(String text, Integer nextQuestionId, Integer contextId) {
        this.text = text;
        this.nextQuestionId = nextQuestionId;
        this.contextId = contextId;
    }

    public String getText() { return text; }
    public Integer getNextQuestionId() { return nextQuestionId; }
    public Integer getContextId() { return contextId; }
}