package com.example.practica4_1.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.practica4_1.R
import com.example.practica4_1.models.Materia

class MateriaListaAdapter(
    private var materias: List<Materia>,
    private val onEliminar: (Materia) -> Unit,
    private val onEditar: (Materia) -> Unit
) : RecyclerView.Adapter<MateriaListaAdapter.MateriaViewHolder>() {

    class MateriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val texto: TextView = itemView.findViewById(R.id.tvMateriaItem)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditarMateria)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminarMateria)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MateriaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_materia_lista, parent, false)
        return MateriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MateriaViewHolder, position: Int) {
        val materia = materias[position]
        holder.texto.text = materia.nombreMateria

        holder.btnEditar.setOnClickListener {
            onEditar(materia)
        }

        holder.btnEliminar.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Está seguro de eliminar la materia ${materia.nombreMateria}?")
                .setPositiveButton("Sí") { _, _ ->
                    onEliminar(materia)
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun getItemCount(): Int = materias.size

    fun update(nuevas: List<Materia>) {
        materias = nuevas
        notifyDataSetChanged()
    }
}
