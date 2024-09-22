package com.example.exbd;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.exbd.databinding.ActivityAddTodoBinding;

import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddTodoActivity extends AppCompatActivity {

    private AppDatabase db;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        ActivityAddTodoBinding binding = ActivityAddTodoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar a base de dados
        db = AppDatabase.getDatabase(getApplicationContext());


        // Inicializar o ExecutorService
        executorService = Executors.newSingleThreadExecutor();


       binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String description = binding.editDescription.getText().toString();

                int year = binding.editDate.getYear();
                int month = binding.editDate.getMonth();
                int dayOfMonth = binding.editDate.getDayOfMonth();

                final String date = dayOfMonth + "/" + month + "/" + year;
                final boolean valor = binding.checkDone.isChecked();

                // Executar a inserção em background
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        db.produtoDao().inserir(new Todo(description, date, valor));

                        // Retornar à thread principal para atualizar a UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddTodoActivity.this, "Tarefa adicionada!", Toast.LENGTH_SHORT).show();
                                finish(); // Volta para a MainActivity
                            }
                        });
                    }
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