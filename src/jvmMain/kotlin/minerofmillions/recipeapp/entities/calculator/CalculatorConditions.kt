package minerofmillions.recipeapp.entities.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.value.Value

class CalculatorConditions(val possibleConditions: Collection<String>) {
	var gameMode by mutableStateOf(GameMode.NORMAL)
	val activeConditions = mutableStateListOf<String>()
	
	enum class GameMode {
		NORMAL,
		EXPERT,
		MASTER
	}
}
