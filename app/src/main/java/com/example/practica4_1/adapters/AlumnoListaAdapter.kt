package com.example.practica4_1.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.practica4_1.R
import com.example.practica4_1.models.Alumno

class AlumnoListaAdapter(
    private var alumnos: List<Alumno>,
    private val onEliminar: (Alumno) -> Unit,
    private val onEditar: (Alumno) -> Unit
) : RecyclerView.Adapter<AlumnoListaAdapter.AlumnoViewHolder>() {

    class AlumnoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val texto: TextView = itemView.findViewById(R.id.tvAlumnoItem)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditarAlumno)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminarAlumno)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlumnoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alumno_lista, parent, false)
        return AlumnoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlumnoViewHolder, position: Int) {
        val alumno = alumnos[position]
        holder.texto.text = "${alumno.ci} - ${alumno.nombres} ${alumno.apellidos}"

        holder.btnEditar.setOnClickListener {
            onEditar(alumno)
        }

        holder.btnEliminar.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Está seguro de eliminar al alumno ${alumno.nombres} ${alumno.apellidos}?")
                .setPositiveButton("Sí") { _, _ ->
                    onEliminar(alumno)
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun getItemCount(): Int = alumnos.size

    fun update(nuevos: List<Alumno>) {
        alumnos = nuevos
        notifyDataSetChanged()
    }
}
