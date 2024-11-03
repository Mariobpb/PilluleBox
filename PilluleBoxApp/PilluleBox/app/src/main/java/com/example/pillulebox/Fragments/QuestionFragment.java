package com.example.pillulebox.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.pillulebox.R;

import Models.Answer;
import Models.Question;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestionFragment extends Fragment {

    private Question question;
    private OnAnswerSelectedListener listener;
    private TextView questionText;
    private Button answerAButton;
    private Button answerBButton;

    public interface OnAnswerSelectedListener {
        void onAnswerSelected(Answer selectedAnswer);
    }

    public QuestionFragment() {
        // Required empty public constructor
    }


    public static QuestionFragment newInstance(Question question) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putInt("questionId", question.getId());
        fragment.setArguments(args);
        fragment.question = question;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAnswerSelectedListener) {
            listener = (OnAnswerSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " debe implementar OnAnswerSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);

        questionText = view.findViewById(R.id.question_text);
        answerAButton = view.findViewById(R.id.answer_a_button);
        answerBButton = view.findViewById(R.id.answer_b_button);

        questionText.setText(question.getText());
        answerAButton.setText(question.getAnswerA().getText());
        answerBButton.setText(question.getAnswerB().getText());

        answerAButton.setOnClickListener(v -> {
            listener.onAnswerSelected(question.getAnswerA());
        });

        answerBButton.setOnClickListener(v -> {
            listener.onAnswerSelected(question.getAnswerB());
        });

        return view;
    }
}