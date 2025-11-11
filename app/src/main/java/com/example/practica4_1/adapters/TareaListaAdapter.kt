package com.example.practica4_1.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.practica4_1.R
import com.example.practica4_1.models.Tarea

class TareaListaAdapter(
    private var tareas: List<Tarea>,
    private val onEliminar: (Tarea) -> Unit,
    private val onEditar: (Tarea) -> Unit
) : RecyclerView.Adapter<TareaListaAdapter.TareaViewHolder>() {

    class TareaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.tvTareaTitulo)
        val fecha: TextView = itemView.findViewById(R.id.tvTareaFecha)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditarTarea)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminarTarea)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tarea_lista, parent, false)
        return TareaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val t = tareas[position]
        holder.titulo.text = t.titulo
        val formato = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        holder.fecha.text = "Entrega: ${formato.format(t.fechaEntrega)}"

        holder.btnEditar.setOnClickListener {
            onEditar(t)
        }

        holder.btnEliminar.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Está seguro de eliminar la tarea ${t.titulo}?")
                .setPositiveButton("Sí") { _, _ ->
                    onEliminar(t)
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun getItemCount(): Int = tareas.size

    fun update(nuevas: List<Tarea>) {
        tareas = nuevas
        notifyDataSetChanged()
    }
}
