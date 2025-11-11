package com.example.practica4_1

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practica4_1.adapters.MateriaListaAdapter
import com.example.practica4_1.database.MateriaDAO
import com.example.practica4_1.models.Materia
import com.example.practica4_1.utils.PaginationHelper
import androidx.appcompat.app.AlertDialog

class MateriaActivity : AppCompatActivity() {

    private lateinit var dao: MateriaDAO
    private lateinit var adapter: MateriaListaAdapter
    private val paginationHelper = PaginationHelper<Materia>(5)

    private lateinit var btnAnteriorTop: Button
    private lateinit var btnSiguienteTop: Button
    private lateinit var tvPaginaTop: TextView
    private lateinit var btnAnteriorBottom: Button
    private lateinit var btnSiguienteBottom: Button
    private lateinit var tvPaginaBottom: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_materia)

        dao = MateriaDAO(this)

        val etNombre = findViewById<EditText>(R.id.etNombreMateria)
        val etBuscar = findViewById<EditText>(R.id.etBuscarMateria)
        val btnCrear = findViewById<Button>(R.id.btnCrearMateria)
        val btnBuscar = findViewById<Button>(R.id.btnBuscarMateria)

        val recycler = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerMaterias)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = MateriaListaAdapter(
            emptyList(),
            { materia -> eliminarMateria(materia) },
            { materia -> mostrarDialogEditar(materia) }
        )
        recycler.adapter = adapter

        // Inicializar controles de paginación
        val paginationTop = findViewById<android.view.View>(R.id.paginationTop)
        btnAnteriorTop = paginationTop.findViewById(R.id.btnAnterior)
        btnSiguienteTop = paginationTop.findViewById(R.id.btnSiguiente)
        tvPaginaTop = paginationTop.findViewById(R.id.tvPaginaActual)

        val paginationBottom = findViewById<android.view.View>(R.id.paginationBottom)
        btnAnteriorBottom = paginationBottom.findViewById(R.id.btnAnterior)
        btnSiguienteBottom = paginationBottom.findViewById(R.id.btnSiguiente)
        tvPaginaBottom = paginationBottom.findViewById(R.id.tvPaginaActual)

        configurarPaginacion()

        btnCrear.setOnClickListener {
            val nombre = etNombre.text.toString().trim()

            if (nombre.isEmpty()) {
                Toast.makeText(this, "Ingrese un nombre", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val materia = Materia(nombreMateria = nombre)
            dao.insertar(materia)

            etNombre.setText("")
            cargarLista()
        }

        btnBuscar.setOnClickListener {
            val texto = etBuscar.text.toString().trim()
            val lista = dao.buscar(texto)
            actualizarListaConPaginacion(lista)
        }

        cargarLista()
    }

    private fun configurarPaginacion() {
        btnAnteriorTop.setOnClickListener {
            if (paginationHelper.previousPage()) {
                actualizarVista()
            }
        }

        btnSiguienteTop.setOnClickListener {
            if (paginationHelper.nextPage()) {
                actualizarVista()
            }
        }

        btnAnteriorBottom.setOnClickListener {
            if (paginationHelper.previousPage()) {
                actualizarVista()
            }
        }

        btnSiguienteBottom.setOnClickListener {
            if (paginationHelper.nextPage()) {
                actualizarVista()
            }
        }
    }

    private fun actualizarVista() {
        val paginaActual = paginationHelper.getCurrentPageItems()
        adapter.update(paginaActual)

        val textoPagina = "${paginationHelper.getCurrentPage()} / ${paginationHelper.getTotalPages()}"
        tvPaginaTop.text = textoPagina
        tvPaginaBottom.text = textoPagina

        btnAnteriorTop.isEnabled = paginationHelper.hasPreviousPage()
        btnSiguienteTop.isEnabled = paginationHelper.hasNextPage()
        btnAnteriorBottom.isEnabled = paginationHelper.hasPreviousPage()
        btnSiguienteBottom.isEnabled = paginationHelper.hasNextPage()
    }

    private fun actualizarListaConPaginacion(lista: List<Materia>) {
        paginationHelper.setItems(lista)
        actualizarVista()
    }

    private fun cargarLista() {
        val lista = dao.obtenerTodos()
        actualizarListaConPaginacion(lista)
    }

    private fun eliminarMateria(materia: Materia) {
        try {
            val filasAfectadas = dao.eliminar(materia.id)
            if (filasAfectadas > 0) {
                Toast.makeText(this, "Materia eliminada correctamente", Toast.LENGTH_SHORT).show()
                cargarLista()
            } else {
                Toast.makeText(this, "Error: No se pudo eliminar la materia", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDialogEditar(materia: Materia) {
        val etNombre = EditText(this).apply {
            setText(materia.nombreMateria)
            hint = "Nombre de la materia"
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 40)
            addView(etNombre)
        }

        AlertDialog.Builder(this)
            .setTitle("Editar Materia")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString().trim()

                if (nombre.isEmpty()) {
                    Toast.makeText(this, "Ingrese un nombre", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val materiaActualizada = Materia(materia.id, nombre)
                val filasAfectadas = dao.actualizar(materiaActualizada)

                if (filasAfectadas > 0) {
                    Toast.makeText(this, "Materia actualizada correctamente", Toast.LENGTH_SHORT).show()
                    cargarLista()
                } else {
                    Toast.makeText(this, "Error al actualizar la materia", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
