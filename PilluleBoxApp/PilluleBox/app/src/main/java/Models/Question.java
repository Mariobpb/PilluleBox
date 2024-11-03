package Models;

public class Question {
    private int id;
    private String text;
    private Answer answerA;
    private Answer answerB;

    public Question(int id, String text, Answer answerA, Answer answerB) {
        this.id = id;
        this.text = text;
        this.answerA = answerA;
        this.answerB = answerB;
    }

    public int getId() { return id; }
    public String getText() { return text; }
    public Answer getAnswerA() { return answerA; }
    public Answer getAnswerB() { return answerB; }
}
