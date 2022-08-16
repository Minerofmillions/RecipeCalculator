package minerofmillions.recipeapp.components.defaults

import com.arkivanov.decompose.ComponentContext
import minerofmillions.recipeapp.components.IApplication

class ApplicationComponent(context: ComponentContext): IApplication, ComponentContext by context {
}
