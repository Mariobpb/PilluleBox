package com.example.pillulebox;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pillulebox.Fragments.QuestionnaireFragment;
import com.example.pillulebox.adapters.DispenserAdapter;

import Models.Answer;
import Models.Dispenser;

public class ContextActivity extends AppCompatActivity {
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
            // La respuesta define un contexto final
            finishQuestionnaire(answer.getContextId());
        } else {
            // Continuar con la siguiente pregunta
            questionnaireFragment.showQuestion(answer.getNextQuestionId());
        }
    }

    public void finishQuestionnaire(int context) {
        Dispenser selectedDispenser = DispenserAdapter.getSelectedDispenser(this);
        if (selectedDispenser != null) {
            selectedDispenser.setContextDispenser(context);
            // Aquí deberías agregar la lógica para guardar el contexto en tu base de datos
        }
        finish();
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