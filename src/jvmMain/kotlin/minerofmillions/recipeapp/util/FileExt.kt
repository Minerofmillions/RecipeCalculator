package minerofmillions.recipeapp.util

import java.io.File

fun File.resolve(vararg paths: String) = paths.fold(this) { acc, path -> acc.resolve(relative = path)}
