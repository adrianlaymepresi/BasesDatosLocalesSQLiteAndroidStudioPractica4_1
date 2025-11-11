package com.example.practica4_1

import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practica4_1.adapters.AlumnoMateriaListaAdapter
import com.example.practica4_1.database.AlumnoDAO
import com.example.practica4_1.database.AlumnoMateriaDAO
import com.example.practica4_1.database.MateriaDAO
import com.example.practica4_1.models.AlumnoMateria
import com.example.practica4_1.utils.PaginationHelper

class AlumnoMateriaActivity : AppCompatActivity() {

    private lateinit var daoAM: AlumnoMateriaDAO
    private lateinit var daoAlumno: AlumnoDAO
    private lateinit var daoMateria: MateriaDAO
    private lateinit var adapter: AlumnoMateriaListaAdapter
    private val paginationHelper = PaginationHelper<AlumnoMateria>(5)

    private var alumnos = listOf<com.example.practica4_1.models.Alumno>()
    private var materias = listOf<com.example.practica4_1.models.Materia>()

    private var idAlumnoSel = -1
    private var idMateriaSel = ""

    private lateinit var btnAnteriorTop: Button
    private lateinit var btnSiguienteTop: Button
    private lateinit var tvPaginaTop: TextView
    private lateinit var btnAnteriorBottom: Button
    private lateinit var btnSiguienteBottom: Button
    private lateinit var tvPaginaBottom: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumno_materia)

        daoAM = AlumnoMateriaDAO(this)
        daoAlumno = AlumnoDAO(this)
        daoMateria = MateriaDAO(this)

        val spAlumno = findViewById<Spinner>(R.id.spAlumnoAM)
        val spMateria = findViewById<Spinner>(R.id.spMateriaAM)
        val btnAsignar = findViewById<Button>(R.id.btnAsignarAM)
        val etBuscar = findViewById<EditText>(R.id.etBuscarAM)
        val btnBuscar = findViewById<Button>(R.id.btnBuscarAM)
        val recycler = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerAM)

        recycler.layoutManager = LinearLayoutManager(this)

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

        cargarSpinners(spAlumno, spMateria)
        cargarLista()

        btnAsignar.setOnClickListener {
            if (idAlumnoSel == -1 || idMateriaSel.isEmpty()) {
                Toast.makeText(this, "Seleccione ambos campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val am = AlumnoMateria(idAlumno = idAlumnoSel, idMateria = idMateriaSel)
            daoAM.insertar(am)
            cargarLista()
        }

        btnBuscar.setOnClickListener {
            val txt = etBuscar.text.toString().trim()
            val res = daoAM.buscar(txt)
            actualizarListaConPaginacion(res)
        }
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
        val nombres = crearMapaNombres()
        adapter = AlumnoMateriaListaAdapter(
            paginaActual,
            nombres,
            { asignacion -> eliminarAsignacion(asignacion) },
            { asignacion -> mostrarDialogEditar(asignacion) }
        )
        findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerAM).adapter = adapter

        val textoPagina = "${paginationHelper.getCurrentPage()} / ${paginationHelper.getTotalPages()}"
        tvPaginaTop.text = textoPagina
        tvPaginaBottom.text = textoPagina

        btnAnteriorTop.isEnabled = paginationHelper.hasPreviousPage()
        btnSiguienteTop.isEnabled = paginationHelper.hasNextPage()
        btnAnteriorBottom.isEnabled = paginationHelper.hasPreviousPage()
        btnSiguienteBottom.isEnabled = paginationHelper.hasNextPage()
    }

    private fun actualizarListaConPaginacion(lista: List<AlumnoMateria>) {
        paginationHelper.setItems(lista)
        actualizarVista()
    }

    private fun crearMapaNombres(): MutableMap<String, String> {
        val nombres = mutableMapOf<String, String>()
        alumnos.forEach { nombres[it.ci.toString()] = "${it.nombres} ${it.apellidos}" }
        materias.forEach { nombres[it.id] = it.nombreMateria }
        return nombres
    }

    private fun cargarSpinners(spAlumno: Spinner, spMateria: Spinner) {
        alumnos = daoAlumno.obtenerTodos()
        materias = daoMateria.obtenerTodos()

        spAlumno.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item,
            alumnos.map { "${it.ci} - ${it.nombres}" }
        )

        spMateria.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item,
            materias.map { it.nombreMateria }
        )

        spAlumno.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, pos: Int, id: Long) {
                idAlumnoSel = alumnos[pos].ci
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        spMateria.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, pos: Int, id: Long) {
                idMateriaSel = materias[pos].id
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
    }

    private fun cargarLista() {
        val lista = daoAM.obtenerTodos()
        actualizarListaConPaginacion(lista)
    }

    private fun eliminarAsignacion(asignacion: AlumnoMateria) {
        try {
            val filasAfectadas = daoAM.eliminar(asignacion.id)
            if (filasAfectadas > 0) {
                Toast.makeText(this, "Asignación eliminada correctamente", Toast.LENGTH_SHORT).show()
                cargarLista()
            } else {
                Toast.makeText(this, "Error: No se pudo eliminar la asignación", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDialogEditar(asignacion: AlumnoMateria) {
        val spAlumno = Spinner(this)
        val spMateria = Spinner(this)

        // Cargar listas actualizadas
        val alumnosTemp = daoAlumno.obtenerTodos()
        val materiasTemp = daoMateria.obtenerTodos()

        // Configurar spinner de alumnos
        val nombresAlumnos = alumnosTemp.map { "${it.ci} - ${it.nombres}" }
        val adapterAlumnos = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresAlumnos)
        adapterAlumnos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spAlumno.adapter = adapterAlumnos

        // Seleccionar alumno actual
        val indexAlumno = alumnosTemp.indexOfFirst { it.ci == asignacion.idAlumno }
        if (indexAlumno >= 0) {
            spAlumno.setSelection(indexAlumno)
        }

        // Configurar spinner de materias
        val nombresMaterias = materiasTemp.map { it.nombreMateria }
        val adapterMaterias = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresMaterias)
        adapterMaterias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spMateria.adapter = adapterMaterias

        // Seleccionar materia actual
        val indexMateria = materiasTemp.indexOfFirst { it.id == asignacion.idMateria }
        if (indexMateria >= 0) {
            spMateria.setSelection(indexMateria)
        }

        var idAlumnoSelDialog = asignacion.idAlumno
        var idMateriaSelDialog = asignacion.idMateria

        spAlumno.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                idAlumnoSelDialog = if (position in alumnosTemp.indices) alumnosTemp[position].ci else asignacion.idAlumno
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spMateria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                idMateriaSelDialog = if (position in materiasTemp.indices) materiasTemp[position].id else asignacion.idMateria
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 40)
            addView(TextView(context).apply {
                text = "Alumno:"
                setPadding(0, 0, 0, 10)
            })
            addView(spAlumno)
            addView(TextView(context).apply {
                text = "Materia:"
                setPadding(0, 20, 0, 10)
            })
            addView(spMateria)
        }

        AlertDialog.Builder(this)
            .setTitle("Editar Asignación")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                // Aquí no podemos actualizar porque AlumnoMateria no tiene método de actualización
                // Solo podemos eliminar la anterior y crear una nueva
                try {
                    daoAM.eliminar(asignacion.id)
                    val nuevaAsignacion = AlumnoMateria(idAlumno = idAlumnoSelDialog, idMateria = idMateriaSelDialog)
                    daoAM.insertar(nuevaAsignacion)
                    Toast.makeText(this, "Asignación actualizada correctamente", Toast.LENGTH_SHORT).show()
                    cargarLista()
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
