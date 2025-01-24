package com.example.registrotecnicos.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tickets")
data class TicketEntity (
    @PrimaryKey
    val ticketId: Int? = null,
    val prioridadId: Int,
    val fecha: String? = "",
    val cliente: String = "",
    val asunto: String = "",
    val descripcion: String = ""
)