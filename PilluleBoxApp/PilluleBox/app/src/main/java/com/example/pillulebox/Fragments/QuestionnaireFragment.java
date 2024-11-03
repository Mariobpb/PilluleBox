package com.example.pillulebox.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pillulebox.ContextActivity;
import com.example.pillulebox.R;

import Managers.QuestionManager;
import Models.Question;

public class QuestionnaireFragment extends Fragment {

    private int currentQuestionId = 1;
    private QuestionManager questionManager;
    private OnContextDeterminedListener contextListener;

    public interface OnContextDeterminedListener {
        void onContextDetermined(int context);
    }

    private void showQuestion(int questionId) {
        Question question = questionManager.getQuestion(questionId);
        if (question != null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.question_container_context, QuestionFragment.newInstance(question))
                    .commit();
        }
    }
    public void handleAnswer(boolean answer, int nextQuestionId, Integer contextIfEnd) {
        if (contextIfEnd != null) {
            ((ContextActivity) requireActivity()).finishQuestionnaire(contextIfEnd);
        } else {
            currentQuestionId = nextQuestionId;
            showQuestion(nextQuestionId);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_questionnaire, container, false);
        questionManager = QuestionManager.getInstance();
        showQuestion(currentQuestionId);
        return view;
    }
}