package minerofmillions.recipeapp.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class TranslationState {
}

@Composable
fun rememberTranslationState() = remember { TranslationState() }
