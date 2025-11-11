package com.example.practica4_1.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NOMBRE, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NOMBRE = "tareas.db"
        private const val DATABASE_VERSION = 1

        const val TABLA_ALUMNO = "Alumno"
        const val TABLA_MATERIA = "Materia"
        const val TABLA_TAREA = "Tarea"
        const val TABLA_ALUMNO_MATERIA = "AlumnoMateria"

        const val ALU_CI = "ci"
        const val ALU_NOMBRES = "nombres"
        const val ALU_APELLIDOS = "apellidos"
        const val ALU_FECHA_NAC = "fechaNacimiento"

        const val MAT_ID = "id"
        const val MAT_NOMBRE = "nombreMateria"

        const val TAR_ID = "id"
        const val TAR_TITULO = "titulo"
        const val TAR_FECHA_ENT = "fechaEntrega"
        const val TAR_ID_MATERIA = "idMateria"

        const val AM_ID = "id"
        const val AM_ID_ALUMNO = "idAlumno"
        const val AM_ID_MATERIA = "idMateria"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val crearTablaAlumno = """
            CREATE TABLE $TABLA_ALUMNO(
                $ALU_CI INTEGER PRIMARY KEY,
                $ALU_NOMBRES TEXT NOT NULL,
                $ALU_APELLIDOS TEXT NOT NULL,
                $ALU_FECHA_NAC INTEGER NOT NULL
            );
        """.trimIndent()

        val crearTablaMateria = """
            CREATE TABLE $TABLA_MATERIA(
                $MAT_ID TEXT PRIMARY KEY,
                $MAT_NOMBRE TEXT NOT NULL
            );
        """.trimIndent()

        val crearTablaTarea = """
            CREATE TABLE $TABLA_TAREA(
                $TAR_ID TEXT PRIMARY KEY,
                $TAR_TITULO TEXT NOT NULL,
                $TAR_FECHA_ENT INTEGER NOT NULL,
                $TAR_ID_MATERIA TEXT NOT NULL,
                FOREIGN KEY($TAR_ID_MATERIA) REFERENCES $TABLA_MATERIA($MAT_ID)
            );
        """.trimIndent()

        val crearTablaAlumnoMateria = """
            CREATE TABLE $TABLA_ALUMNO_MATERIA(
                $AM_ID TEXT PRIMARY KEY,
                $AM_ID_ALUMNO INTEGER NOT NULL,
                $AM_ID_MATERIA TEXT NOT NULL,
                FOREIGN KEY($AM_ID_ALUMNO) REFERENCES $TABLA_ALUMNO($ALU_CI),
                FOREIGN KEY($AM_ID_MATERIA) REFERENCES $TABLA_MATERIA($MAT_ID)
            );
        """.trimIndent()

        db?.execSQL(crearTablaAlumno)
        db?.execSQL(crearTablaMateria)
        db?.execSQL(crearTablaTarea)
        db?.execSQL(crearTablaAlumnoMateria)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLA_ALUMNO_MATERIA")
        db?.execSQL("DROP TABLE IF EXISTS $TABLA_TAREA")
        db?.execSQL("DROP TABLE IF EXISTS $TABLA_MATERIA")
        db?.execSQL("DROP TABLE IF EXISTS $TABLA_ALUMNO")
        onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        if (db != null && !db.isReadOnly) db.execSQL("PRAGMA foreign_keys=ON;")
    }
}
