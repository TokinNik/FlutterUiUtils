package com.flutter.ui.utils.flutteruiutils

import com.flutter.ui.utils.flutteruiutils.features.deleteCodeBlock.DeleteCodeBlockInFile
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.fileEditor.FileEditorManager

class RunDetektMenuGroup : DefaultActionGroup(DeleteCodeBlockInFile()) {

    override fun update(e: AnActionEvent) {
        val project = e.project
        val presentation = e.presentation
        if (project == null) {
            // If no project defined, disable the menu item
            presentation.isEnabled = false
            presentation.isVisible = false
            return
        } else {
            presentation.isVisible = true
        }
        if (e.place == ActionPlaces.MAIN_MENU) {
            // Enabled only if some files are selected.
            val selectedFiles = FileEditorManager.getInstance(project).selectedFiles
            presentation.isEnabled = selectedFiles.isNotEmpty()
        }
    }
}