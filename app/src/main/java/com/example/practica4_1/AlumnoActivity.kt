package com.example.practica4_1

import android.app.DatePickerDialog
import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practica4_1.adapters.AlumnoListaAdapter
import com.example.practica4_1.database.AlumnoDAO
import com.example.practica4_1.models.Alumno
import com.example.practica4_1.utils.PaginationHelper
import java.text.SimpleDateFormat
import java.util.*

class AlumnoActivity : AppCompatActivity() {

    private lateinit var dao: AlumnoDAO
    private lateinit var adapter: AlumnoListaAdapter
    private val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val paginationHelper = PaginationHelper<Alumno>(5)

    private lateinit var btnAnteriorTop: Button
    private lateinit var btnSiguienteTop: Button
    private lateinit var tvPaginaTop: TextView
    private lateinit var btnAnteriorBottom: Button
    private lateinit var btnSiguienteBottom: Button
    private lateinit var tvPaginaBottom: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumno)

        dao = AlumnoDAO(this)

        val etCi = findViewById<EditText>(R.id.etCiAlumno)
        val etNom = findViewById<EditText>(R.id.etNombresAlumno)
        val etApe = findViewById<EditText>(R.id.etApellidosAlumno)
        val etFecha = findViewById<EditText>(R.id.etFechaNacimientoAlumno)
        val etBuscar = findViewById<EditText>(R.id.etBuscarAlumno)
        val btnCrear = findViewById<Button>(R.id.btnCrearAlumno)
        val btnBuscar = findViewById<Button>(R.id.btnBuscarAlumno)

        val recycler = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerAlumnos)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = AlumnoListaAdapter(
            emptyList(),
            { alumno -> eliminarAlumno(alumno) },
            { alumno -> mostrarDialogEditar(alumno) }
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
        etFecha.setOnClickListener { abrirDatePickerNacimiento(etFecha) }

        btnCrear.setOnClickListener {
            val ciStr = etCi.text.toString().trim()
            val nombres = etNom.text.toString().trim()
            val apellidos = etApe.text.toString().trim()
            val fechaStr = etFecha.text.toString().trim()

            if (ciStr.isEmpty() || nombres.isEmpty() || apellidos.isEmpty() || fechaStr.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val regexNombre = Regex("^[a-zA-ZÁÉÍÓÚáéíóúÑñ ]+$")

            if (!regexNombre.matches(nombres)) {
                Toast.makeText(this, "Nombre inválido: solo letras y espacios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!regexNombre.matches(apellidos)) {
                Toast.makeText(this, "Apellido inválido: solo letras y espacios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ci = ciStr.toIntOrNull()
            if (ci == null || ci <= 0) {
                Toast.makeText(this, "CI inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fecha = formato.parse(fechaStr) ?: Date()
            val hoy = limpiarHora(Date())
            if (fecha.after(hoy)) {
                Toast.makeText(this, "La fecha de nacimiento no puede ser futura", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val alumno = Alumno(ci, nombres, apellidos, fecha)
            dao.insertar(alumno)

            etCi.setText("")
            etNom.setText("")
            etApe.setText("")
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

    private fun actualizarListaConPaginacion(lista: List<Alumno>) {
        paginationHelper.setItems(lista)
        actualizarVista()
    }

    private fun abrirDatePickerNacimiento(et: EditText) {
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
        dlg.datePicker.maxDate = hoy.time
        dlg.show()
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

    private fun eliminarAlumno(alumno: Alumno) {
        try {
            val filasAfectadas = dao.eliminar(alumno.ci)
            if (filasAfectadas > 0) {
                Toast.makeText(this, "Alumno eliminado correctamente", Toast.LENGTH_SHORT).show()
                cargarLista()
            } else {
                Toast.makeText(this, "Error: No se pudo eliminar el alumno", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDialogEditar(alumno: Alumno) {
        val etNombres = EditText(this).apply {
            setText(alumno.nombres)
            hint = "Nombres"
        }
        val etApellidos = EditText(this).apply {
            setText(alumno.apellidos)
            hint = "Apellidos"
        }
        val etFechaNac = EditText(this).apply {
            setText(formato.format(alumno.fechaNacimiento))
            hint = "Fecha de nacimiento (dd/MM/yyyy)"
            isFocusable = false
            isClickable = true
            setOnClickListener {
                abrirDatePickerEditar(this, alumno.fechaNacimiento)
            }
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 40)
            addView(TextView(context).apply {
                text = "CI: ${alumno.ci}"
                textSize = 16f
                setPadding(0, 0, 0, 20)
            })
            addView(etNombres)
            addView(etApellidos)
            addView(etFechaNac)
        }

        AlertDialog.Builder(this)
            .setTitle("Editar Alumno")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val nombres = etNombres.text.toString().trim()
                val apellidos = etApellidos.text.toString().trim()
                val fechaStr = etFechaNac.text.toString().trim()

                if (nombres.isEmpty() || apellidos.isEmpty() || fechaStr.isEmpty()) {
                    Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val regexNombre = Regex("^[a-zA-ZÁÉÍÓÚáéíóúÑñ ]+$")
                if (!regexNombre.matches(nombres)) {
                    Toast.makeText(this, "Nombre inválido: solo letras y espacios", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (!regexNombre.matches(apellidos)) {
                    Toast.makeText(this, "Apellido inválido: solo letras y espacios", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val fecha = formato.parse(fechaStr) ?: Date()
                val hoy = limpiarHora(Date())
                if (fecha.after(hoy)) {
                    Toast.makeText(this, "La fecha de nacimiento no puede ser futura", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val alumnoActualizado = Alumno(alumno.ci, nombres, apellidos, fecha)
                val filasAfectadas = dao.actualizar(alumnoActualizado)

                if (filasAfectadas > 0) {
                    Toast.makeText(this, "Alumno actualizado correctamente", Toast.LENGTH_SHORT).show()
                    cargarLista()
                } else {
                    Toast.makeText(this, "Error al actualizar el alumno", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun abrirDatePickerEditar(et: EditText, fechaActual: Date) {
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
        dlg.datePicker.maxDate = hoy.time
        dlg.show()
    }
}
