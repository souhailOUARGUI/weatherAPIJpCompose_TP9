package com.example.weatherapijpcompose_tp9.ui



import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapijpcompose_tp9.model.MeteoItem
import com.example.weatherapijpcompose_tp9.viewmodel.MeteoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeteoScreen(
    meteoViewModel: MeteoViewModel = viewModel()
) {
    val meteoState by meteoViewModel.meteoState.collectAsState()
    val isLoading by meteoViewModel.isLoading.collectAsState()
    val error by meteoViewModel.error.collectAsState()

    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Barre de recherche
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Entrez une ville") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = {
                    if (searchText.isNotEmpty()) {
                        meteoViewModel.searchWeatherData(searchText)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Rechercher"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Affichage du contenu principal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }

                error != null -> {
                    Text(
                        text = error ?: "Une erreur est survenue",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                meteoState != null -> {
                    MeteoContent(meteoState!!, meteoViewModel)
                }

                else -> {
                    Text("Recherchez une ville pour voir la météo")
                }
            }
        }
    }
}

@Composable
fun MeteoContent(meteoItem: MeteoItem, viewModel: MeteoViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF3498DB)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ville
            Text(
                text = meteoItem.ville.uppercase(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Image météo
            Image(
                painter = painterResource(id = viewModel.getWeatherIconResource(meteoItem.image)),
                contentDescription = "Condition météo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date
            Text(
                text = meteoItem.date,
                fontSize = 16.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Température principale
            Text(
                text = "${meteoItem.temperature}°C",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Détails météo
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                MeteoDetailRow("T° Min:", "${meteoItem.tempMin}°C")
                MeteoDetailRow("T° Max:", "${meteoItem.tempMax}°C")
                MeteoDetailRow("Pression:", "${meteoItem.pression} hPa")
                MeteoDetailRow("Humidité:", "${meteoItem.humidite}%")
                // Ajout de la vitesse du vent (nouvelle fonctionnalité)
                MeteoDetailRow("Vitesse du vent:", "${meteoItem.vitesseVent} m/s")
            }
        }
    }
}

@Composable
fun MeteoDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}