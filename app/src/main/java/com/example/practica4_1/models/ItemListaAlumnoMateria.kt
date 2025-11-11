package com.example.practica4_1.models

sealed class ItemListaAlumnoMateria {
    data class AlumnoItem(
        val alumno: Alumno
    ) : ItemListaAlumnoMateria()

    data class MateriaItem(
        val materia: Materia,
        val nombreAlumno: String
    ) : ItemListaAlumnoMateria()

    data class TareaItem(
        val tarea: Tarea,
        val nombreMateria: String,
        val nombreAlumno: String
    ) : ItemListaAlumnoMateria()
}