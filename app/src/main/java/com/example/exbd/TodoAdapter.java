package com.example.exbd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    private List<Todo> todos = new ArrayList<>();
    private OnItemClickListener listener;
    private Context context;


    public interface OnItemClickListener {
        void onItemClick(Todo todo);
    }

    public TodoAdapter(Context context) {
        this.context = context;
    }

    public void setProdutos(List<Todo> todos) {
        this.todos.clear();
        this.todos.addAll(todos);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TodoViewHolder holder, int position) {
        Todo todo = todos.get(position);
        holder.textDescription.setText(todo.getDescription());

        try {
            String[] dateParams = todo.getDate().split("/");
            int dayOfMonth = Integer.parseInt(dateParams[0]);
            int month = Integer.parseInt(dateParams[1]);
            int year = Integer.parseInt(dateParams[2]);

            holder.textDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        }
        catch(Exception ex) {
            Toast.makeText(context, "Alguma coisa deu errado! Tente deletar esta tarefa e criar novamente.", Toast.LENGTH_SHORT).show();
        }

        String checkText = todo.getDone() ? "Finalizado" : "Pendente";
        holder.checkDone.setText(checkText);
        if (todo.getDone()) {
            holder.checkDone.setTextColor(0xFF2196F3);
        }
        else {
            holder.checkDone.setTextColor(0xFFFF9800);
        }

        // Clique no item para editar
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(todo);
            }
        });

        // Clique no botão de deletar
        holder.btnDelete.setOnClickListener(v -> {
            // Exibir um diálogo de confirmação
            new AlertDialog.Builder(context)
                    .setTitle("Excluir Tarefa")
                    .setMessage("Tem certeza que deseja excluir esta tarefa?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        if (context instanceof MainActivity) {
                            ((MainActivity) context).deletarProduto(todo);
                        }
                    })
                    .setNegativeButton("Não", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return todos != null ? todos.size() : 0;
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        TextView textDescription, textDate, checkDone;
        ImageButton btnDelete;

        public TodoViewHolder(View itemView) {
            super(itemView);
            textDescription = itemView.findViewById(R.id.textDescription);
            textDate = itemView.findViewById(R.id.textDate);
            checkDone = itemView.findViewById(R.id.checkDone);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
