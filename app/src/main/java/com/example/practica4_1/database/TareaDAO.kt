package com.example.practica4_1.database

import android.content.ContentValues
import android.content.Context
import com.example.practica4_1.models.Tarea

class TareaDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun insertar(tarea: Tarea): Long {
        val db = dbHelper.writableDatabase
        val valores = ContentValues()
        valores.put(DatabaseHelper.TAR_ID, tarea.id)
        valores.put(DatabaseHelper.TAR_TITULO, tarea.titulo)
        valores.put(DatabaseHelper.TAR_FECHA_ENT, tarea.fechaEntrega.time)
        valores.put(DatabaseHelper.TAR_ID_MATERIA, tarea.idMateria)
        val id = db.insert(DatabaseHelper.TABLA_TAREA, null, valores)
        db.close()
        return id
    }

    fun obtenerTodos(): List<Tarea> {
        val lista = mutableListOf<Tarea>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLA_TAREA}", null)
        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TAR_ID))
            val titulo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TAR_TITULO))
            val fecha = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.TAR_FECHA_ENT))
            val idMateria = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TAR_ID_MATERIA))
            lista.add(Tarea(id, titulo, java.util.Date(fecha), idMateria))
        }
        cursor.close()
        db.close()
        return lista
    }

    fun obtenerPorId(idTarea: String): Tarea? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLA_TAREA} WHERE ${DatabaseHelper.TAR_ID}=?",
            arrayOf(idTarea)
        )
        var tarea: Tarea? = null
        if (cursor.moveToFirst()) {
            val titulo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TAR_TITULO))
            val fecha = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.TAR_FECHA_ENT))
            val idMateria = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TAR_ID_MATERIA))
            tarea = Tarea(idTarea, titulo, java.util.Date(fecha), idMateria)
        }
        cursor.close()
        db.close()
        return tarea
    }

    fun actualizar(tarea: Tarea): Int {
        val db = dbHelper.writableDatabase
        val valores = ContentValues()
        valores.put(DatabaseHelper.TAR_TITULO, tarea.titulo)
        valores.put(DatabaseHelper.TAR_FECHA_ENT, tarea.fechaEntrega.time)
        valores.put(DatabaseHelper.TAR_ID_MATERIA, tarea.idMateria)
        val filas = db.update(
            DatabaseHelper.TABLA_TAREA,
            valores,
            "${DatabaseHelper.TAR_ID}=?",
            arrayOf(tarea.id)
        )
        db.close()
        return filas
    }

    fun eliminar(idTarea: String): Int {
        val db = dbHelper.writableDatabase
        val filas = db.delete(
            DatabaseHelper.TABLA_TAREA,
            "${DatabaseHelper.TAR_ID}=?",
            arrayOf(idTarea)
        )
        db.close()
        return filas
    }

    fun buscar(texto: String): List<Tarea> {
        val lista = mutableListOf<Tarea>()
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLA_TAREA} " +
                    "WHERE ${DatabaseHelper.TAR_TITULO} LIKE ? " +
                    "ORDER BY ${DatabaseHelper.TAR_FECHA_ENT} ASC",
            arrayOf("%$texto%")
        )

        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TAR_ID))
            val titulo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TAR_TITULO))
            val fecha = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.TAR_FECHA_ENT))
            val idMateria = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TAR_ID_MATERIA))

            lista.add(Tarea(id, titulo, java.util.Date(fecha), idMateria))
        }

        cursor.close()
        db.close()
        return lista
    }
}
