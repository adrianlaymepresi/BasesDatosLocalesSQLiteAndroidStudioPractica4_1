package com.example.practica4_1.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.practica4_1.R
import com.example.practica4_1.models.AlumnoMateria

class AlumnoMateriaListaAdapter(
    private var items: List<AlumnoMateria>,
    private val nombres: Map<String, String>,
    private val onEliminar: (AlumnoMateria) -> Unit,
    private val onEditar: (AlumnoMateria) -> Unit
) : RecyclerView.Adapter<AlumnoMateriaListaAdapter.ViewHolder>() {

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val alumno: TextView = item.findViewById(R.id.tvItemAlumnoNombre)
        val materia: TextView = item.findViewById(R.id.tvItemMateriaNombre)
        val btnEditar: ImageButton = item.findViewById(R.id.btnEditarAsignacion)
        val btnEliminar: ImageButton = item.findViewById(R.id.btnEliminarAsignacion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alumno_materia, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val am = items[position]

        val nombreAlumno = nombres[am.idAlumno.toString()] ?: "Alumno"
        val nombreMateria = nombres[am.idMateria] ?: "Materia"

        holder.alumno.text = nombreAlumno
        holder.materia.text = nombreMateria

        holder.btnEditar.setOnClickListener {
            onEditar(am)
        }

        holder.btnEliminar.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Está seguro de eliminar la asignación de $nombreAlumno a $nombreMateria?")
                .setPositiveButton("Sí") { _, _ ->
                    onEliminar(am)
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun getItemCount() = items.size

    fun update(newItems: List<AlumnoMateria>) {
        items = newItems
        notifyDataSetChanged()
    }
}
