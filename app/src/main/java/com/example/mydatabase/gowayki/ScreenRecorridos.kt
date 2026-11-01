package com.example.mydatabase.gowayki

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val RojoGoWayki = Color(0xFFF83A34)

// Las rutas reales del prototipo web de GoWayki (RutaSeeder.php) con su color de linea
val rutasGoWayki = listOf(
    "Canarios S.A." to Color(0xFF3498DB),
    "Cotum B" to Color(0xFF27AE60),
    "El Conquistador" to Color(0xFFF39C12),
    "San Martín" to Color(0xFF8E44AD),
    "Arequipa - Sachaca" to Color(0xFFE74C3C)
)

private fun colorDe(ruta: String) = rutasGoWayki.firstOrNull { it.first == ruta }?.second ?: Color.Gray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenRecorridos() {
    val context = LocalContext.current
    val dao = remember { GoWaykiDatabase.getDatabase(context).recorridoDao() }
    val scope = rememberCoroutineScope()
    val recorridos by remember { dao.getAll() }.collectAsState(initial = emptyList())

    var nombre by remember { mutableStateOf("") }
    var origen by remember { mutableStateOf("") }
    var destino by remember { mutableStateOf("") }
    var ruta by remember { mutableStateOf(rutasGoWayki[0].first) }
    var notas by remember { mutableStateOf("") }
    var editando by remember { mutableStateOf<Recorrido?>(null) }

    fun limpiar() {
        nombre = ""; origen = ""; destino = ""; notas = ""; ruta = rutasGoWayki[0].first
        editando = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("GoWayki", fontWeight = FontWeight.Bold)
                        Text(if (editando == null) "Nuevo recorrido" else "Editar recorrido", fontSize = 13.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RojoGoWayki,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre del recorrido") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(origen, { origen = it }, label = { Text("Origen") }, singleLine = true, modifier = Modifier.weight(1f))
                OutlinedTextField(destino, { destino = it }, label = { Text("Destino") }, singleLine = true, modifier = Modifier.weight(1f))
            }
            Text("Ruta que tomo", fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                rutasGoWayki.forEach { (nombreRuta, color) ->
                    FilterChip(
                        selected = ruta == nombreRuta,
                        onClick = { ruta = nombreRuta },
                        label = { Text(nombreRuta) },
                        leadingIcon = { Box(Modifier.size(10.dp).clip(CircleShape).background(color)) }
                    )
                }
            }
            OutlinedTextField(notas, { notas = it }, label = { Text("Notas (opcional)") }, singleLine = true, modifier = Modifier.fillMaxWidth())

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (nombre.isBlank() || origen.isBlank() || destino.isBlank()) {
                            Toast.makeText(context, "Completa nombre, origen y destino", Toast.LENGTH_SHORT).show()
                        } else {
                            val actual = editando
                            // CREATE o UPDATE segun si estoy editando
                            val recorrido = actual?.copy(nombre = nombre, origen = origen, destino = destino, ruta = ruta, notas = notas)
                                ?: Recorrido(nombre = nombre, origen = origen, destino = destino, ruta = ruta, notas = notas)
                            scope.launch {
                                if (actual == null) dao.insert(recorrido) else dao.update(recorrido)
                            }
                            limpiar()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RojoGoWayki)
                ) { Text(if (editando == null) "Guardar" else "Actualizar") }
                if (editando != null) {
                    OutlinedButton(onClick = { limpiar() }) { Text("Cancelar") }
                }
            }

            HorizontalDivider()
            Text(
                "Mis recorridos (${recorridos.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // READ: la lista se actualiza sola gracias al Flow
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(recorridos, key = { it.id }) { r ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.height(IntrinsicSize.Min), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.width(6.dp).fillMaxHeight().background(colorDe(r.ruta)))
                            Column(modifier = Modifier.weight(1f).padding(10.dp)) {
                                Text(r.nombre, fontWeight = FontWeight.Bold)
                                Text("${r.origen} → ${r.destino}", fontSize = 13.sp)
                                Text(
                                    r.ruta + if (r.notas.isNotBlank()) " · ${r.notas}" else "",
                                    fontSize = 12.sp, color = colorDe(r.ruta), fontWeight = FontWeight.SemiBold
                                )
                            }
                            // UPDATE: subo los datos al formulario
                            IconButton(onClick = {
                                editando = r
                                nombre = r.nombre; origen = r.origen; destino = r.destino; ruta = r.ruta; notas = r.notas
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }
                            // DELETE
                            IconButton(onClick = {
                                if (editando?.id == r.id) limpiar()
                                scope.launch { dao.delete(r) }
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
