package com.flutter.ui.utils.flutteruiutils.chancheFolderIcons

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurableProvider
import org.jetbrains.annotations.Nullable

class MyFormConfigurableProvider : ConfigurableProvider() {
    @Nullable
    override fun createConfigurable(): Configurable {
        return MyForm()
    }
}