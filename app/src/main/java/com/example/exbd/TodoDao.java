package com.example.exbd;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TodoDao {
    @Insert
    void inserir(Todo todo);

    @Update
    void atualizar(Todo todo);

    @Delete
    void deletar(Todo todo);

    @Query("SELECT * FROM Todo")
    LiveData<List<Todo>> listar();

    @Query("SELECT * FROM Todo WHERE id = :id LIMIT 1")
    Todo SelectId(int id);

}
