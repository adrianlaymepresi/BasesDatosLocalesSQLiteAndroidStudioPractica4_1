package com.example.practica4_1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practica4_1.adapters.AdaptadorCombinadoAlumnoMateria
import com.example.practica4_1.database.*
import com.example.practica4_1.models.*

class MainActivity : AppCompatActivity() {

    private lateinit var alumnoDAO: AlumnoDAO
    private lateinit var materiaDAO: MateriaDAO
    private lateinit var tareaDAO: TareaDAO
    private lateinit var alumnoMateriaDAO: AlumnoMateriaDAO
    private lateinit var adapter: AdaptadorCombinadoAlumnoMateria

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        alumnoDAO = AlumnoDAO(this)
        materiaDAO = MateriaDAO(this)
        tareaDAO = TareaDAO(this)
        alumnoMateriaDAO = AlumnoMateriaDAO(this)

        val btnAlumno = findViewById<Button>(R.id.btnIrAlumno)
        val btnMateria = findViewById<Button>(R.id.btnIrMateria)
        val btnTarea = findViewById<Button>(R.id.btnIrTarea)
        val btnAlumnoMateria = findViewById<Button>(R.id.btnIrAlumnoMateria)

        btnAlumno.setOnClickListener { startActivity(Intent(this, AlumnoActivity::class.java)) }
        btnMateria.setOnClickListener { startActivity(Intent(this, MateriaActivity::class.java)) }
        btnTarea.setOnClickListener { startActivity(Intent(this, TareaActivity::class.java)) }
        btnAlumnoMateria.setOnClickListener { startActivity(Intent(this, AlumnoMateriaActivity::class.java)) }

        val recycler = findViewById<RecyclerView>(R.id.recyclerMain)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = AdaptadorCombinadoAlumnoMateria(emptyList())
        recycler.adapter = adapter

        // Buscador
        val etBuscar = findViewById<EditText>(R.id.etBuscarMain)
        val btnBuscar = findViewById<Button>(R.id.btnBuscarMain)

        btnBuscar.setOnClickListener {
            val texto = etBuscar.text.toString().trim()
            if (texto.isEmpty()) {
                cargarLista()
            } else {
                cargarFiltrado(texto)
            }
        }

        cargarLista()
    }

    private fun cargarLista() {
        val alumnos = alumnoDAO.obtenerTodos()
        val materias = materiaDAO.obtenerTodos()
        val tareas = tareaDAO.obtenerTodos()
        val asignaciones = alumnoMateriaDAO.obtenerTodos()

        adapter.updateData(buildCombinedList(alumnos, materias, tareas, asignaciones))
    }

    private fun cargarFiltrado(texto: String) {
        val textoLower = texto.lowercase()

        val alumnos = alumnoDAO.obtenerTodos()
            .filter { it.nombres.lowercase().contains(textoLower) || it.apellidos.lowercase().contains(textoLower) }

        val materias = materiaDAO.obtenerTodos()
            .filter { it.nombreMateria.lowercase().contains(textoLower) }

        val tareas = tareaDAO.obtenerTodos()
            .filter { it.titulo.lowercase().contains(textoLower) }

        val asignaciones = alumnoMateriaDAO.obtenerTodos()

        val lista = mutableListOf<ItemListaAlumnoMateria>()

        // Si coincide alumno → se muestran sus materias y tareas
        for (alumno in alumnos) {
            lista.add(ItemListaAlumnoMateria.AlumnoItem(alumno))

            val materiasDelAlumno = asignaciones
                .filter { it.idAlumno == alumno.ci }
                .mapNotNull { am -> materiaDAO.obtenerPorId(am.idMateria) }

            for (m in materiasDelAlumno) {
                lista.add(ItemListaAlumnoMateria.MateriaItem(m, alumno.nombres))

                val tareasDeMateria = tareaDAO.obtenerTodos().filter { it.idMateria == m.id }

                for (t in tareasDeMateria) {
                    lista.add(
                        ItemListaAlumnoMateria.TareaItem(
                            tarea = t,
                            nombreMateria = m.nombreMateria,
                            nombreAlumno = alumno.nombres
                        )
                    )
                }
            }
        }

        // Si coincide materia pero NO alumno
        for (m in materias) {

            val asignados = asignaciones.filter { it.idMateria == m.id }

            for (am in asignados) {
                val alumno = alumnoDAO.obtenerPorId(am.idAlumno) ?: continue

                lista.add(ItemListaAlumnoMateria.AlumnoItem(alumno))
                lista.add(ItemListaAlumnoMateria.MateriaItem(m, alumno.nombres))

                val tareasDeMateria = tareas.filter { it.idMateria == m.id }
                for (t in tareasDeMateria) {
                    lista.add(
                        ItemListaAlumnoMateria.TareaItem(
                            tarea = t,
                            nombreMateria = m.nombreMateria,
                            nombreAlumno = alumno.nombres
                        )
                    )
                }
            }
        }

        // Si coincide tarea
        for (t in tareas) {
            val materia = materiaDAO.obtenerPorId(t.idMateria) ?: continue

            val asignados = asignaciones.filter { it.idMateria == materia.id }
            for (am in asignados) {
                val alumno = alumnoDAO.obtenerPorId(am.idAlumno) ?: continue

                lista.add(ItemListaAlumnoMateria.AlumnoItem(alumno))
                lista.add(ItemListaAlumnoMateria.MateriaItem(materia, alumno.nombres))
                lista.add(
                    ItemListaAlumnoMateria.TareaItem(
                        tarea = t,
                        nombreMateria = materia.nombreMateria,
                        nombreAlumno = alumno.nombres
                    )
                )
            }
        }

        adapter.updateData(lista.distinctBy { it.hashCode() })
    }


    private fun buildCombinedList(
        alumnos: List<Alumno>,
        materias: List<Materia>,
        tareas: List<Tarea>,
        asignaciones: List<AlumnoMateria>
    ): List<ItemListaAlumnoMateria> {

        val lista = mutableListOf<ItemListaAlumnoMateria>()

        for (alumno in alumnos) {

            lista.add(ItemListaAlumnoMateria.AlumnoItem(alumno))

            val materiasDelAlumno = asignaciones
                .filter { it.idAlumno == alumno.ci }
                .mapNotNull { am -> materias.find { it.id == am.idMateria } }

            for (m in materiasDelAlumno) {
                lista.add(ItemListaAlumnoMateria.MateriaItem(m, alumno.nombres))

                val tareasDeMateria = tareas.filter { it.idMateria == m.id }

                for (t in tareasDeMateria) {
                    lista.add(
                        ItemListaAlumnoMateria.TareaItem(
                            tarea = t,
                            nombreMateria = m.nombreMateria,
                            nombreAlumno = alumno.nombres
                        )
                    )
                }
            }
        }

        return lista
    }
}
