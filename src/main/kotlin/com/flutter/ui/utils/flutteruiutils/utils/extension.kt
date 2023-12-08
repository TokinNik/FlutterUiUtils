package com.flutter.ui.utils.flutteruiutils.utils

import com.intellij.psi.PsiElement

val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()
val snakeRegex = "_[a-zA-Z]".toRegex()

fun String.camelToSnakeCase(): String {
    return camelRegex.replace(this) {
        "_${it.value}"
    }.lowercase()
}

fun String.snakeToLowerCamelCase(): String {
    return snakeRegex.replace(this) {
        it.value.replace("_", "").uppercase()
    }
}

fun PsiElement?.printAllChildRec(level: Int = 1) {
    this?.let { element ->
        element.children.forEach {
            (1..level).forEach { _ ->
                print(" | ")
            }
            println("File child: $it  ${it.javaClass}")
            it.printAllChildRec(level + 1)
        }
    }
}

fun PsiElement?.findInChildRec(query: (PsiElement) -> Boolean): PsiElement? {
    this?.let { element ->
        if (query(this)) return this
        element.children.forEach {
//            println("File child: $it  ${it.children.isEmpty()}")
            val check = it.findInChildRec(query)
            if (check != null) return check
        }
    }
    return null
}