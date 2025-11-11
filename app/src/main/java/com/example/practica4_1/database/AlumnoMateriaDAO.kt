package com.example.practica4_1.database

import android.content.ContentValues
import android.content.Context
import com.example.practica4_1.models.AlumnoMateria

class AlumnoMateriaDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun insertar(am: AlumnoMateria): Long {
        val db = dbHelper.writableDatabase
        val valores = ContentValues()
        valores.put(DatabaseHelper.AM_ID, am.id)
        valores.put(DatabaseHelper.AM_ID_ALUMNO, am.idAlumno)
        valores.put(DatabaseHelper.AM_ID_MATERIA, am.idMateria)
        val id = db.insert(DatabaseHelper.TABLA_ALUMNO_MATERIA, null, valores)
        db.close()
        return id
    }

    fun obtenerTodos(): List<AlumnoMateria> {
        val lista = mutableListOf<AlumnoMateria>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLA_ALUMNO_MATERIA}", null)
        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.AM_ID))
            val idAlumno = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.AM_ID_ALUMNO))
            val idMateria = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.AM_ID_MATERIA))
            lista.add(AlumnoMateria(id, idAlumno, idMateria))
        }
        cursor.close()
        db.close()
        return lista
    }

    fun eliminar(id: String): Int {
        val db = dbHelper.writableDatabase
        val filas = db.delete(
            DatabaseHelper.TABLA_ALUMNO_MATERIA,
            "${DatabaseHelper.AM_ID}=?",
            arrayOf(id)
        )
        db.close()
        return filas
    }

    fun buscar(texto: String): List<AlumnoMateria> {
        val lista = mutableListOf<AlumnoMateria>()
        val db = dbHelper.readableDatabase
        val like = "%$texto%"

        val query = """
        SELECT am.${DatabaseHelper.AM_ID},
               am.${DatabaseHelper.AM_ID_ALUMNO},
               am.${DatabaseHelper.AM_ID_MATERIA},
               a.${DatabaseHelper.ALU_NOMBRES},
               m.${DatabaseHelper.MAT_NOMBRE}
        FROM ${DatabaseHelper.TABLA_ALUMNO_MATERIA} am
        INNER JOIN ${DatabaseHelper.TABLA_ALUMNO} a
            ON am.${DatabaseHelper.AM_ID_ALUMNO} = a.${DatabaseHelper.ALU_CI}
        INNER JOIN ${DatabaseHelper.TABLA_MATERIA} m
            ON am.${DatabaseHelper.AM_ID_MATERIA} = m.${DatabaseHelper.MAT_ID}
        WHERE a.${DatabaseHelper.ALU_NOMBRES} LIKE ?
           OR m.${DatabaseHelper.MAT_NOMBRE} LIKE ?
        ORDER BY a.${DatabaseHelper.ALU_NOMBRES} ASC
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(like, like))

        while (cursor.moveToNext()) {
            val id = cursor.getString(0)
            val idAlumno = cursor.getInt(1)
            val idMateria = cursor.getString(2)
            lista.add(AlumnoMateria(id, idAlumno, idMateria))
        }

        cursor.close()
        db.close()
        return lista
    }
}
