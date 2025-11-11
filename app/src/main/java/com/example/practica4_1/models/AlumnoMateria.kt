package com.example.practica4_1.models

class AlumnoMateria(
    val id: String = java.util.UUID.randomUUID().toString(),
    val idAlumno:  Int,
    val idMateria: String
)