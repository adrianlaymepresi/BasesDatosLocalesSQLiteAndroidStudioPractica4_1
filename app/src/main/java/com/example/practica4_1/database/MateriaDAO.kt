package com.example.practica4_1.database

import android.content.ContentValues
import android.content.Context
import com.example.practica4_1.models.Materia

class MateriaDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun insertar(materia: Materia): Long {
        val db = dbHelper.writableDatabase
        val valores = ContentValues()
        valores.put(DatabaseHelper.MAT_ID, materia.id)
        valores.put(DatabaseHelper.MAT_NOMBRE, materia.nombreMateria)
        val id = db.insert(DatabaseHelper.TABLA_MATERIA, null, valores)
        db.close()
        return id
    }

    fun obtenerTodos(): List<Materia> {
        val lista = mutableListOf<Materia>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLA_MATERIA} ORDER BY ${DatabaseHelper.MAT_NOMBRE} ASC",
            null
        )
        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.MAT_ID))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.MAT_NOMBRE))
            lista.add(Materia(id, nombre))
        }
        cursor.close()
        db.close()
        return lista
    }

    fun obtenerPorId(idMateria: String): Materia? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLA_MATERIA} WHERE ${DatabaseHelper.MAT_ID}=?",
            arrayOf(idMateria)
        )
        var materia: Materia? = null
        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.MAT_NOMBRE))
            materia = Materia(idMateria, nombre)
        }
        cursor.close()
        db.close()
        return materia
    }

    fun actualizar(materia: Materia): Int {
        val db = dbHelper.writableDatabase
        val valores = ContentValues()
        valores.put(DatabaseHelper.MAT_NOMBRE, materia.nombreMateria)
        val filas = db.update(
            DatabaseHelper.TABLA_MATERIA,
            valores,
            "${DatabaseHelper.MAT_ID}=?",
            arrayOf(materia.id)
        )
        db.close()
        return filas
    }

    fun eliminar(idMateria: String): Int {
        val db = dbHelper.writableDatabase
        val filas = db.delete(
            DatabaseHelper.TABLA_MATERIA,
            "${DatabaseHelper.MAT_ID}=?",
            arrayOf(idMateria)
        )
        db.close()
        return filas
    }

    fun buscar(texto: String): List<Materia> {
        val lista = mutableListOf<Materia>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLA_MATERIA} " +
                    "WHERE ${DatabaseHelper.MAT_NOMBRE} LIKE ? " +
                    "ORDER BY ${DatabaseHelper.MAT_NOMBRE} ASC",
            arrayOf("%$texto%")
        )
        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.MAT_ID))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.MAT_NOMBRE))
            lista.add(Materia(id, nombre))
        }
        cursor.close()
        db.close()
        return lista
    }
}
