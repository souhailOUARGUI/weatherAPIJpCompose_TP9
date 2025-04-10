package com.example.weatherapijpcompose_tp9.model

data class MeteoItem(
    val temperature: Int,
    val tempMax: Int,
    val tempMin: Int,
    val pression: Int,
    val humidite: Int,
    val image: String,
    val date: String,
    val ville: String,
    // Ajout de la vitesse du vent comme demandé
    val vitesseVent: Double
)