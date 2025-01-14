package com.example.registrotecnicos

import android.os.Build.VERSION
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Upsert
import com.example.registrotecnicos.ui.theme.RegistroTecnicosTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private lateinit var tecnicoDb: TecnicoDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        tecnicoDb = Room.databaseBuilder(
            applicationContext,
            TecnicoDb::class.java,
            "tecnicodata"
        ).fallbackToDestructiveMigration()
            .build()

        setContent {
            TecnicoScreen(tecnicoDb)
        }
    }
}


@Entity(tableName = "Tecnicos")
data class TecnicosEntity(
    @PrimaryKey(autoGenerate = true)
    val tecnicoId: Int? = null,
    val tecnicos: String = "",
    val sueldo: Double = 0.0
)


@Dao
interface TecnicoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(tecnico: TecnicosEntity)

    @Query("SELECT * FROM Tecnicos WHERE tecnicoId = :id")
    suspend fun find(id: Int): TecnicosEntity?

    @Delete
    suspend fun delete(tecnico: TecnicosEntity)

    @Query("SELECT * FROM Tecnicos")
    fun getAll(): Flow<List<TecnicosEntity>>
}


@Database(entities = [TecnicosEntity::class], version = 1, exportSchema = false)
abstract class TecnicoDb : RoomDatabase() {
    abstract fun tecnicoDao(): TecnicoDao
}


@Composable
fun TecnicoScreen(tecnicoDb: TecnicoDb) {
    var tecnico by remember { mutableStateOf("") }
    var sueldo by remember { mutableStateOf(0.0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }


    val scope = rememberCoroutineScope()


    val tecnicoList by tecnicoDb.tecnicoDao().getAll().collectAsState(initial = emptyList())

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    OutlinedTextField(
                        label = { Text(text = "Técnico") },
                        value = tecnico,
                        onValueChange = { tecnico = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        label = { Text(text = "Sueldo") },
                        value = if (sueldo > 0.0) sueldo.toString() else "",
                        onValueChange = { sueldo = it.toDoubleOrNull() ?: 0.0 },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.padding(2.dp))

                    errorMessage?.let {
                        Text(text = it, color = Color.Red)
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(onClick = {
                            tecnico = ""
                            sueldo = 0.0
                        }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Nuevo")
                            Text(text = "Nuevo")
                        }

                        OutlinedButton(
                            onClick = {
                                if (tecnico.isBlank()) {
                                    errorMessage = "Nombre de técnico vacío"
                                } else if (sueldo <= 0.0) {
                                    errorMessage = "Sueldo no válido"
                                } else {
                                    errorMessage = null
                                    scope.launch {
                                        tecnicoDb.tecnicoDao().save(
                                            TecnicosEntity(
                                                tecnicos = tecnico,
                                                sueldo = sueldo
                                            )
                                        )
                                        tecnico = ""
                                        sueldo = 0.0
                                    }
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Guardar")
                            Text(text = "Guardar")
                        }
                    }
                }
            }

            // Mostrar lista de técnicos
            TecnicoListScreen(tecnicoList)
        }
    }
}


@Composable
fun TecnicoListScreen(tecnicoList: List<TecnicosEntity>) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Lista de Técnicos", style = MaterialTheme.typography.headlineSmall)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(tecnicoList) { tecnico ->
                TecnicoRow(tecnico)
            }
        }
    }
}


@Composable
private fun TecnicoRow(tecnico: TecnicosEntity) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
        Text(
            modifier = Modifier.weight(1f),
            text = tecnico.tecnicoId?.toString() ?: "",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            modifier = Modifier.weight(2f),
            text = tecnico.tecnicos,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            modifier = Modifier.weight(2f),
            text = "$${tecnico.sueldo}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
    Divider()
}


