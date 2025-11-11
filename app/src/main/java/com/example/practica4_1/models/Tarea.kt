package com.example.practica4_1.models

import java.util.Date
import java.util.UUID

class Tarea(
    val id: String = UUID.randomUUID().toString(),
    val titulo: String,
    val fechaEntrega: Date,
    val idMateria: String
)
