package com.example.practica4_1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.practica4_1.R
import com.example.practica4_1.models.ItemListaAlumnoMateria

class AdaptadorCombinadoAlumnoMateria(
    private var items: List<ItemListaAlumnoMateria>
) : RecyclerView.Adapter<AdaptadorCombinadoAlumnoMateria.BaseViewHolder<*>>() {

    private val TYPE_ALUMNO = 0
    private val TYPE_MATERIA = 1
    private val TYPE_TAREA = 2

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

    class AlumnoViewHolder(itemView: View) :
        BaseViewHolder<ItemListaAlumnoMateria.AlumnoItem>(itemView) {
        private val nombre: TextView = itemView.findViewById(R.id.tvAlumnoNombre)
        override fun bind(item: ItemListaAlumnoMateria.AlumnoItem) {
            nombre.text = item.alumno.nombres + " " + item.alumno.apellidos
        }
    }

    class MateriaViewHolder(itemView: View) :
        BaseViewHolder<ItemListaAlumnoMateria.MateriaItem>(itemView) {
        private val nombre: TextView = itemView.findViewById(R.id.tvMateriaNombre)
        override fun bind(item: ItemListaAlumnoMateria.MateriaItem) {
            nombre.text = item.materia.nombreMateria
        }
    }

    class TareaViewHolder(itemView: View) :
        BaseViewHolder<ItemListaAlumnoMateria.TareaItem>(itemView) {
        private val titulo: TextView = itemView.findViewById(R.id.tvTareaTitulo)
        override fun bind(item: ItemListaAlumnoMateria.TareaItem) {
            titulo.text = item.tarea.titulo
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ItemListaAlumnoMateria.AlumnoItem -> TYPE_ALUMNO
            is ItemListaAlumnoMateria.MateriaItem -> TYPE_MATERIA
            is ItemListaAlumnoMateria.TareaItem -> TYPE_TAREA
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_ALUMNO -> {
                val view = inflater.inflate(R.layout.item_alumno, parent, false)
                AlumnoViewHolder(view)
            }
            TYPE_MATERIA -> {
                val view = inflater.inflate(R.layout.item_materia, parent, false)
                MateriaViewHolder(view)
            }
            TYPE_TAREA -> {
                val view = inflater.inflate(R.layout.item_tarea, parent, false)
                TareaViewHolder(view)
            }
            else -> throw IllegalArgumentException("Tipo de vista no válido")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val item = items[position]
        when (holder) {
            is AlumnoViewHolder -> holder.bind(item as ItemListaAlumnoMateria.AlumnoItem)
            is MateriaViewHolder -> holder.bind(item as ItemListaAlumnoMateria.MateriaItem)
            is TareaViewHolder -> holder.bind(item as ItemListaAlumnoMateria.TareaItem)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<ItemListaAlumnoMateria>) {
        items = newItems
        notifyDataSetChanged()
    }
}
