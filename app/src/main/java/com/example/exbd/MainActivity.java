package com.example.exbd;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.exbd.databinding.ActivityMainBinding;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements TodoAdapter.OnItemClickListener {

    private AppDatabase db;
    private TodoAdapter todoAdapter;
    private ExecutorService executorService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar a base de dados
        db = AppDatabase.getDatabase(getApplicationContext());

        // Inicializar o ExecutorService
        executorService = Executors.newSingleThreadExecutor();

        // Configurar o RecyclerView e o Adapter
        binding.recyclerViewProdutos.setLayoutManager(new LinearLayoutManager(this));
        todoAdapter = new TodoAdapter(this);
        todoAdapter.setOnItemClickListener(this); // Configurar o listener
        binding.recyclerViewProdutos.setAdapter(todoAdapter);

        // Observar as mudanças na lista de produtos
        db.produtoDao().listar().observe(this, new Observer<List<Todo>>() {
            @Override
            public void onChanged(List<Todo> todos) {
                todoAdapter.setProdutos(todos);
            }
        });


        // Configurar o FloatingActionButton
        binding.fabAdicionar.setOnClickListener(v -> {
            // Navegar para a atividade de adicionar produto
            Intent intent = new Intent(MainActivity.this, AddTodoActivity.class);
            startActivity(intent);
        });

        binding.fabInfo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InfoActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onItemClick(Todo todo) {
        // Navegar para a atividade de edição de produto
        Intent intent = new Intent(MainActivity.this, EditTodoActivity.class);
        intent.putExtra("todo_id", todo.getId());
        startActivity(intent);
    }

    public void deletarProduto(Todo todo) {
        executorService.execute(() -> {
            db.produtoDao().deletar(todo);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}