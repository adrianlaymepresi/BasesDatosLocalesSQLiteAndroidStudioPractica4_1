package com.example.practica4_1

import android.app.DatePickerDialog
import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practica4_1.adapters.TareaListaAdapter
import com.example.practica4_1.database.MateriaDAO
import com.example.practica4_1.database.TareaDAO
import com.example.practica4_1.models.Materia
import com.example.practica4_1.models.Tarea
import com.example.practica4_1.utils.PaginationHelper
import java.text.SimpleDateFormat
import java.util.*

class TareaActivity : AppCompatActivity() {

    private lateinit var dao: TareaDAO
    private lateinit var daoMateria: MateriaDAO
    private lateinit var adapter: TareaListaAdapter
    private var materiasLista = listOf<Materia>()
    private var idMateriaSeleccionada = ""
    private val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val paginationHelper = PaginationHelper<Tarea>(5)

    private lateinit var btnAnteriorTop: Button
    private lateinit var btnSiguienteTop: Button
    private lateinit var tvPaginaTop: TextView
    private lateinit var btnAnteriorBottom: Button
    private lateinit var btnSiguienteBottom: Button
    private lateinit var tvPaginaBottom: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarea)

        dao = TareaDAO(this)
        daoMateria = MateriaDAO(this)

        val etTitulo = findViewById<EditText>(R.id.etTituloTarea)
        val etFecha = findViewById<EditText>(R.id.etFechaEntregaTarea)
        val etBuscar = findViewById<EditText>(R.id.etBuscarTarea)
        val spinner = findViewById<Spinner>(R.id.spMateriasTarea)
        val btnCrear = findViewById<Button>(R.id.btnCrearTarea)
        val btnBuscar = findViewById<Button>(R.id.btnBuscarTarea)

        val recycler = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerTareas)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = TareaListaAdapter(
            emptyList(),
            { tarea -> eliminarTarea(tarea) },
            { tarea -> mostrarDialogEditar(tarea) }
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

        etFecha.isFocusable = false
        etFecha.isClickable = true
        etFecha.setOnClickListener { abrirDatePickerEntrega(etFecha) }

        cargarMateriasSpinner(spinner)

        btnCrear.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()
            val fechaStr = etFecha.text.toString().trim()
            if (titulo.isEmpty() || fechaStr.isEmpty() || idMateriaSeleccionada.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val fecha = formato.parse(fechaStr) ?: Date()
            val hoy = limpiarHora(Date())
            if (fecha.before(hoy)) {
                Toast.makeText(this, "La fecha de entrega debe ser hoy o futura", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val tarea = Tarea(titulo = titulo, fechaEntrega = fecha, idMateria = idMateriaSeleccionada)
            dao.insertar(tarea)
            etTitulo.setText("")
            etFecha.setText("")
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

    private fun actualizarListaConPaginacion(lista: List<Tarea>) {
        paginationHelper.setItems(lista)
        actualizarVista()
    }

    private fun abrirDatePickerEntrega(et: EditText) {
        val cal = Calendar.getInstance()
        val hoy = limpiarHora(Date())
        val dlg = DatePickerDialog(
            this,
            { _, y, m, d ->
                val cSel = Calendar.getInstance()
                cSel.set(y, m, d, 0, 0, 0)
                cSel.set(Calendar.MILLISECOND, 0)
                et.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cSel.time))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        dlg.datePicker.minDate = hoy.time
        dlg.show()
    }

    private fun cargarMateriasSpinner(spinner: Spinner) {
        materiasLista = daoMateria.obtenerTodos()
        val nombres = materiasLista.map { it.nombreMateria }
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombres)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adaptador
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                idMateriaSeleccionada = if (position in materiasLista.indices) materiasLista[position].id else ""
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                idMateriaSeleccionada = ""
            }
        }
    }

    private fun limpiarHora(fecha: Date): Date {
        val c = Calendar.getInstance()
        c.time = fecha
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.time
    }

    private fun cargarLista() {
        val lista = dao.obtenerTodos()
        actualizarListaConPaginacion(lista)
    }

    private fun eliminarTarea(tarea: Tarea) {
        try {
            val filasAfectadas = dao.eliminar(tarea.id)
            if (filasAfectadas > 0) {
                Toast.makeText(this, "Tarea eliminada correctamente", Toast.LENGTH_SHORT).show()
                cargarLista()
            } else {
                Toast.makeText(this, "Error: No se pudo eliminar la tarea", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDialogEditar(tarea: Tarea) {
        val etTitulo = EditText(this).apply {
            setText(tarea.titulo)
            hint = "Título de la tarea"
        }

        val etFechaEntrega = EditText(this).apply {
            setText(formato.format(tarea.fechaEntrega))
            hint = "Fecha de entrega (dd/MM/yyyy)"
            isFocusable = false
            isClickable = true
            setOnClickListener {
                abrirDatePickerEditarTarea(this, tarea.fechaEntrega)
            }
        }

        val spinner = Spinner(this)
        val materiasTemp = daoMateria.obtenerTodos()
        val nombresMateria = materiasTemp.map { it.nombreMateria }
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresMateria)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapterSpinner

        // Seleccionar la materia actual
        val indexMateria = materiasTemp.indexOfFirst { it.id == tarea.idMateria }
        if (indexMateria >= 0) {
            spinner.setSelection(indexMateria)
        }

        var idMateriaSeleccionadaDialog = tarea.idMateria
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                idMateriaSeleccionadaDialog = if (position in materiasTemp.indices) materiasTemp[position].id else tarea.idMateria
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 40)
            addView(etTitulo)
            addView(etFechaEntrega)
            addView(TextView(context).apply {
                text = "Materia:"
                setPadding(0, 20, 0, 10)
            })
            addView(spinner)
        }

        AlertDialog.Builder(this)
            .setTitle("Editar Tarea")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val titulo = etTitulo.text.toString().trim()
                val fechaStr = etFechaEntrega.text.toString().trim()

                if (titulo.isEmpty() || fechaStr.isEmpty()) {
                    Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val fecha = formato.parse(fechaStr) ?: Date()
                val hoy = limpiarHora(Date())
                if (fecha.before(hoy)) {
                    Toast.makeText(this, "La fecha de entrega debe ser hoy o futura", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val tareaActualizada = Tarea(tarea.id, titulo, fecha, idMateriaSeleccionadaDialog)
                val filasAfectadas = dao.actualizar(tareaActualizada)

                if (filasAfectadas > 0) {
                    Toast.makeText(this, "Tarea actualizada correctamente", Toast.LENGTH_SHORT).show()
                    cargarLista()
                } else {
                    Toast.makeText(this, "Error al actualizar la tarea", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun abrirDatePickerEditarTarea(et: EditText, fechaActual: Date) {
        val cal = Calendar.getInstance()
        cal.time = fechaActual
        val hoy = limpiarHora(Date())
        val dlg = DatePickerDialog(
            this,
            { _, y, m, d ->
                val cSel = Calendar.getInstance()
                cSel.set(y, m, d, 0, 0, 0)
                cSel.set(Calendar.MILLISECOND, 0)
                et.setText(formato.format(cSel.time))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        dlg.datePicker.minDate = hoy.time
        dlg.show()
    }
}
