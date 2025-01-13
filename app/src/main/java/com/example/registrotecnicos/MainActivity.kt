package com.example.registrotecnicos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import com.example.registrotecnicos.ui.theme.RegistroTecnicosTheme
import kotlinx.coroutines.flow.Flow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

    }
}

@Entity(tableName = " Tecnicos")
data class TecnicosEntity(
    @PrimaryKey
    val TecnicoId: Int? = null,
    val tecnicos: String = "",
    val sueldo: Double = 0.0
)
@Dao
interface TecnicoDao {


    @Insert
    suspend fun save(tecnico: TecnicosEntity)


    @Query("SELECT * FROM ` tecnicos` WHERE TecnicoId = :id")
    suspend fun find(id: Int): TecnicosEntity?


    @Delete
    suspend fun delete(tecnico: TecnicosEntity)


    @Query("SELECT * FROM ` tecnicos`")
    fun getAll(): Flow<List<TecnicosEntity>>
}
