package org.oriolestero.project.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.oriolestero.project.vm.WeatherViewModel
import kotlin.math.round

@Composable
fun WeatherScreen(
    vm: WeatherViewModel = remember { WeatherViewModel() }
) {
    var query by remember { mutableStateOf(TextFieldValue("Madrid")) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Weatherly", style = MaterialTheme.typography.headlineLarge)

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Ciudad") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = { vm.search(query.text.trim()) },
            enabled = !vm.uiState.isLoading
        ) {
            Text(if (vm.uiState.isLoading) "Buscando..." else "Buscar")
        }

        when {
            vm.uiState.isLoading -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Buscando clima…")
                }
            }
            vm.uiState.error != null -> {
                Text("⚠️ ${vm.uiState.error}", color = MaterialTheme.colorScheme.error)
            }
            vm.uiState.cityLabel != null -> {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(vm.uiState.cityLabel!!, style = MaterialTheme.typography.titleLarge)
                        Text("Temperatura: ${formatTemp(vm.uiState.temperatureC)}")
                        Text("Viento: ${formatWind(vm.uiState.windMs)}")
                    }
                }
            }
            else -> {
                Text("Escribe una ciudad para ver el clima")
            }
        }
    }
}



private fun formatTemp(value: Double?): String =
    value?.let { "${(round(it * 10) / 10)} °C" } ?: "—"

private fun formatWind(value: Double?): String =
    value?.let { "${(round(it * 10) / 10)} m/s" } ?: "—"