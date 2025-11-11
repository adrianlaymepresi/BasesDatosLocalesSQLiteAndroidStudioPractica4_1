package com.example.practica4_1.database

import android.content.ContentValues
import android.content.Context
import com.example.practica4_1.models.Alumno

class AlumnoDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun insertar(alumno: Alumno): Long {
        val db = dbHelper.writableDatabase
        val valores = ContentValues()
        valores.put(DatabaseHelper.ALU_CI, alumno.ci)
        valores.put(DatabaseHelper.ALU_NOMBRES, alumno.nombres)
        valores.put(DatabaseHelper.ALU_APELLIDOS, alumno.apellidos)
        valores.put(DatabaseHelper.ALU_FECHA_NAC, alumno.fechaNacimiento.time)
        val id = db.insert(DatabaseHelper.TABLA_ALUMNO, null, valores)
        db.close()
        return id
    }

    fun obtenerTodos(): List<Alumno> {
        val lista = mutableListOf<Alumno>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLA_ALUMNO}", null)

        while (cursor.moveToNext()) {
            val ci = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.ALU_CI))
            val nombres = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.ALU_NOMBRES))
            val apellidos = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.ALU_APELLIDOS))
            val fecha = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.ALU_FECHA_NAC))
            lista.add(Alumno(ci, nombres, apellidos, java.util.Date(fecha)))
        }

        cursor.close()
        db.close()
        return lista
    }

    fun obtenerPorId(ciAlumno: Int): Alumno? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLA_ALUMNO} WHERE ${DatabaseHelper.ALU_CI}=?",
            arrayOf(ciAlumno.toString())
        )

        var alumno: Alumno? = null
        if (cursor.moveToFirst()) {
            val nombres = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.ALU_NOMBRES))
            val apellidos = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.ALU_APELLIDOS))
            val fecha = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.ALU_FECHA_NAC))
            alumno = Alumno(ciAlumno, nombres, apellidos, java.util.Date(fecha))
        }

        cursor.close()
        db.close()
        return alumno
    }

    fun actualizar(alumno: Alumno): Int {
        val db = dbHelper.writableDatabase
        val valores = ContentValues()
        valores.put(DatabaseHelper.ALU_NOMBRES, alumno.nombres)
        valores.put(DatabaseHelper.ALU_APELLIDOS, alumno.apellidos)
        valores.put(DatabaseHelper.ALU_FECHA_NAC, alumno.fechaNacimiento.time)

        val filas = db.update(
            DatabaseHelper.TABLA_ALUMNO,
            valores,
            "${DatabaseHelper.ALU_CI}=?",
            arrayOf(alumno.ci.toString())
        )

        db.close()
        return filas
    }

    fun eliminar(ciAlumno: Int): Int {
        val db = dbHelper.writableDatabase
        val filas = db.delete(
            DatabaseHelper.TABLA_ALUMNO,
            "${DatabaseHelper.ALU_CI}=?",
            arrayOf(ciAlumno.toString())
        )
        db.close()
        return filas
    }

    fun buscar(texto: String): List<Alumno> {
        val lista = mutableListOf<Alumno>()
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT * FROM ${DatabaseHelper.TABLA_ALUMNO}
            WHERE ${DatabaseHelper.ALU_CI} LIKE ?
               OR ${DatabaseHelper.ALU_NOMBRES} LIKE ?
               OR ${DatabaseHelper.ALU_APELLIDOS} LIKE ?
            """.trimIndent(),
            arrayOf("%$texto%", "%$texto%", "%$texto%")
        )

        while (cursor.moveToNext()) {
            val ci = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.ALU_CI))
            val nombres = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.ALU_NOMBRES))
            val apellidos = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.ALU_APELLIDOS))
            val fecha = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.ALU_FECHA_NAC))
            lista.add(Alumno(ci, nombres, apellidos, java.util.Date(fecha)))
        }

        cursor.close()
        db.close()
        return lista
    }
}
