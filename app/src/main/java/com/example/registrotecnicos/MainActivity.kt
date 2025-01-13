package com.example.registrotecnicos

import android.os.Build.VERSION
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
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Upsert
import com.example.registrotecnicos.ui.theme.RegistroTecnicosTheme
import kotlinx.coroutines.flow.Flow

class MainActivity : ComponentActivity() {
    private lateinit var TecnicoDb: TecnicoDb
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        TecnicoDb = Room.databaseBuilder(
            applicationContext,
            TecnicoDb::class.java,
            "tecnicodata"

        ).fallbackToDestructiveMigration()
            .build()
    }
}





@Entity(tableName = " Tecnicos")
data class TecnicosEntity(
    @PrimaryKey
    val tecnicoId: Int? = null,
    val tecnicos: String = "",
    val sueldo: Double = 0.0
)
@Dao
interface TecnicoDao {


    @Insert
    suspend fun save(tecnico: TecnicosEntity)


    @Query("SELECT * FROM ` tecnicos` WHERE tecnicoId = :id")
    suspend fun find(id: Int): TecnicosEntity?


    @Delete
    suspend fun delete(tecnico: TecnicosEntity)


    @Query("SELECT * FROM ` tecnicos`")
    fun getAll(): Flow<List<TecnicosEntity>>
}

@Database(
    entities = [
        TecnicosEntity :: class
    ],
    version = 1,
    exportSchema = false
)
abstract  class  TecnicoDb : RoomDatabase(){
    abstract  fun  tecnicoDao(): TecnicoDao
}
