package minerofmillions.recipeapp.util

import java.io.File
import kotlin.io.resolve as originalResolve

fun File.resolve(vararg relatives: String): File = relatives.fold(this) { file, relative -> file.originalResolve(relative) }
fun File.resolve(vararg relatives: File): File = relatives.fold(this) { file, relative -> file.originalResolve(relative) }
