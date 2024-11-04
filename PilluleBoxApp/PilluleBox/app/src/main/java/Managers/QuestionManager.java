package Managers;

import java.util.HashMap;
import java.util.Map;

import Models.Answer;
import Models.Question;

public class QuestionManager {
    private static Map<Integer, Question> questions;
    private static QuestionManager instance;

    private QuestionManager() {
        questions = new HashMap<>();
        initializeQuestions();
    }

    public static QuestionManager getInstance() {
        if (instance == null) {
            instance = new QuestionManager();
        }
        return instance;
    }

    private void initializeQuestions() {
        Answer answer1A = new Answer(
                "Yo mismo.",
                2, null
        );
        Answer answer1B = new Answer(
                "Otra persona.",
                3, null
        );
        questions.put(1, new Question(1, "¿Quién será la persona que consumirá los medicamentos por parte del dispositivo portador?", answer1A, answer1B));

        Answer answer2A = new Answer(
                "Yo mismo.",
                null, 2
        );
        Answer answer2B = new Answer(
                "Otra persona.",
                4, null
        );
        questions.put(2, new Question(2, "¿Quién será la persona que administrará los horarios de los medicamentos y portará mayormente el dispositivo móvil?", answer2A, answer2B));

        Answer answer3A = new Answer(
                "Sí.",
                null, 1
        );
        Answer answer3B = new Answer(
                "No.",
                5, null
        );
        questions.put(3, new Question(3, "¿La persona a cuidar cuenta con las capacidades de movimiento necesarias para poder consumir por sí mismo los medicamentos?", answer3A, answer3B));

        Answer answer4A = new Answer(
                "Sí.",
                null, 1
        );
        Answer answer4B = new Answer(
                "No.",
                6, null
        );
        questions.put(4, new Question(4, "¿Usted tiene la capacidad de movimiento para consumir los medicamentos y confirmar su consumo de manera física?", answer4A, answer4B));

        Answer answer5A = new Answer(
                "El portador del dispositivo móvil.",
                null, 3
        );
        Answer answer5B = new Answer(
                "Un tercero",
                null, 4
        );
        questions.put(5, new Question(5, "¿Quién será la persona que estará en presencia con la persona la mayoría del tiempo para ayudar con su consumo y confirmación del mismo de manera física?", answer5A, answer5B));

        Answer answer6A = new Answer(
                "El portador del dispositivo móvil.",
                null, 3
        );
        Answer answer6B = new Answer(
                "Un tercero",
                null, 4
        );
        questions.put(6, new Question(6, "¿Quién contará con usted la mayoría del tiempo para ayudarlo con su consumo y asignación de medicamentos?", answer6A, answer6B));
    }

    public Question getQuestion(int id) {
        return questions.get(id);
    }
}
