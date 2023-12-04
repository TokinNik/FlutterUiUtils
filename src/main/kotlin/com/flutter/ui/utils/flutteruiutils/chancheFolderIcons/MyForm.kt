package com.flutter.ui.utils.flutteruiutils.chancheFolderIcons

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.util.NlsContexts
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class MyForm : Configurable {
    private val changeIconAndRefreshButton: JButton? = null
    private val pane: JPanel? = null

    init {
        changeIconAndRefreshButton!!.addActionListener {
            val openProject: Project = ProjectManager.getInstance().openProjects[0]
            ProjectView.getInstance(openProject).refresh()
        }
    }

    override fun getDisplayName(): @NlsContexts.ConfigurableName String? {
        return null
    }

    override fun createComponent(): JComponent? {
        return pane
    }

    override fun isModified(): Boolean {
        return false
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
    }
}