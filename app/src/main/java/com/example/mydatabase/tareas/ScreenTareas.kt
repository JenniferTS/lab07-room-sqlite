package com.example.mydatabase.tareas

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenTareas() {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).tareaDao() }
    val scope = rememberCoroutineScope()
    val tareas by remember { dao.getAll() }.collectAsState(initial = emptyList())

    var titulo by remember { mutableStateOf("") }
    var curso by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var tareaEditando by remember { mutableStateOf<Tarea?>(null) }

    fun limpiar() {
        titulo = ""; curso = ""; fecha = ""
        tareaEditando = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (tareaEditando == null) "Nueva tarea" else "Editar tarea") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = titulo, onValueChange = { titulo = it },
                label = { Text("Título de la tarea") },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = curso, onValueChange = { curso = it },
                label = { Text("Curso") },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = fecha, onValueChange = { fecha = it },
                label = { Text("Fecha de entrega (dd/mm/aaaa)") },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = {
                    if (titulo.isBlank() || curso.isBlank()) {
                        Toast.makeText(context, "Llena al menos el título y el curso", Toast.LENGTH_SHORT).show()
                    } else {
                        val editando = tareaEditando
                        // CREATE o UPDATE segun si estoy editando
                        val tarea = editando?.copy(titulo = titulo, curso = curso, fechaEntrega = fecha)
                            ?: Tarea(titulo = titulo, curso = curso, fechaEntrega = fecha)
                        scope.launch {
                            if (editando == null) dao.insert(tarea) else dao.update(tarea)
                        }
                        limpiar()
                    }
                }) {
                    Text(if (tareaEditando == null) "Guardar" else "Actualizar")
                }
                if (tareaEditando != null) {
                    OutlinedButton(onClick = { limpiar() }) { Text("Cancelar") }
                }
            }

            HorizontalDivider()
            Text(
                "Mis tareas (${tareas.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // READ: la lista se actualiza sola gracias al Flow
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tareas, key = { it.id }) { tarea ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = tarea.completada,
                                onCheckedChange = { marcado ->
                                    scope.launch { dao.update(tarea.copy(completada = marcado)) }
                                }
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(tarea.titulo, fontWeight = FontWeight.Bold)
                                Text("${tarea.curso} - ${tarea.fechaEntrega}", fontSize = 13.sp)
                            }
                            // UPDATE: subo los datos al formulario
                            IconButton(onClick = {
                                tareaEditando = tarea
                                titulo = tarea.titulo
                                curso = tarea.curso
                                fecha = tarea.fechaEntrega
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }
                            // DELETE
                            IconButton(onClick = {
                                if (tareaEditando?.id == tarea.id) limpiar()
                                scope.launch { dao.delete(tarea) }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}
