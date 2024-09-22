package com.example.exbd;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.exbd.databinding.ActivityEditTodoBinding;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditTodoActivity extends AppCompatActivity {

    private AppDatabase db;
    private ExecutorService executorService;
    private Todo todo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        ActivityEditTodoBinding binding = ActivityEditTodoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obter a instância Singleton do banco de dados
        db = AppDatabase.getDatabase(getApplicationContext());

        executorService = Executors.newSingleThreadExecutor();

        // Obter o ID do produto a partir da Intent
        int todo_id = getIntent().getIntExtra("todo_id", -1);

        if (todo_id != -1) {
            // Carregar o produto do banco de dados
            executorService.execute(() -> {
                todo = db.produtoDao().SelectId(todo_id);
                runOnUiThread(() -> {
                    if (todo != null) {
                        // Preencher os campos com os dados do produto
                        binding.editDescription.setText(todo.getDescription());

                        try {
                            String[] dateParams = todo.getDate().split("/");
                            int dayOfMonth = Integer.parseInt(dateParams[0]);
                            int month = Integer.parseInt(dateParams[1]);
                            int year = Integer.parseInt(dateParams[2]);

                            binding.editDate.updateDate(year, month, dayOfMonth);
                        }
                        catch(Exception ex) {
                            Toast.makeText(EditTodoActivity.this, "Alguma coisa deu errado! Tente deletar esta tarefa e criar novamente.", Toast.LENGTH_SHORT).show();
                        }

                        binding.checkDone.setChecked(todo.getDone());
                    }
                });
            });
        }

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String description = binding.editDescription.getText().toString();

                int year = binding.editDate.getYear();
                int month = binding.editDate.getMonth();
                int dayOfMonth = binding.editDate.getDayOfMonth();

                final String data = dayOfMonth + "/" +  month + "/" + year;

                final boolean done = binding.checkDone.isChecked();

                // Atualizar o produto
                executorService.execute(() -> {
                    todo.setDescription(description);
                    todo.setDate(data);
                    todo.setDone(done);
                    db.produtoDao().atualizar(todo);

                    // Voltar à thread principal para atualizar a UI
                    runOnUiThread(() -> {
                        Toast.makeText(EditTodoActivity.this, "Tarefa atualizada!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}