package com.example.pillulebox;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.pillulebox.Fragments.ContextResultFragment;
import com.example.pillulebox.Fragments.QuestionFragment;
import com.example.pillulebox.Fragments.QuestionnaireFragment;

import Models.Answer;

public class ContextActivity extends AppCompatActivity implements QuestionFragment.OnAnswerSelectedListener {
    private Toolbar toolbar;
    private QuestionnaireFragment questionnaireFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_context);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        if (savedInstanceState == null) {
            questionnaireFragment = new QuestionnaireFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_context, questionnaireFragment)
                    .commit();
        }
    }

    @Override
    public void onAnswerSelected(Answer answer) {
        if (answer.getContextId() != null) {
            showContextResult(answer.getContextId());
        } else {
            questionnaireFragment.handleAnswer(true, answer.getNextQuestionId(), null);
        }
    }


    private void showContextResult(int contextId) {
        ContextResultFragment resultFragment = ContextResultFragment.newInstance(contextId);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_context, resultFragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}