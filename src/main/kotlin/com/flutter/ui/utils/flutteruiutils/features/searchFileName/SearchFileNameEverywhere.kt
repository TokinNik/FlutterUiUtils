package com.flutter.ui.utils.flutteruiutils.features.searchFileName

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.util.ui.TextTransferable
import java.awt.Robot
import java.awt.Toolkit
import java.awt.event.KeyEvent
import java.util.*


class SearchFileNameEverywhere : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        getAllFilesName(e)
    }

    private fun getAllFilesName(e: AnActionEvent) {
        val dataContext = e.dataContext
        val project: Project? = CommonDataKeys.PROJECT.getData(dataContext)

        val selectedElement = project?.let { ProjectView.getInstance(it).currentProjectViewPane.selectedElement }
        println("??? ${selectedElement}")

        when (selectedElement) {
            is PsiDirectory -> {
                println("is Directory: ${selectedElement.name}")
                printFilesNameWithoutExtension(selectedElement)
            }

            is PsiFile -> {
                val name = getFileNameWithoutExtension(selectedElement.name)
                println("is File: ${name}")

                val action = ActionManager.getInstance().getAction(IdeActions.ACTION_FIND_IN_PATH)
                action.actionPerformed(e)
                val clipbaord = Toolkit.getDefaultToolkit().systemClipboard
                clipbaord.setContents(TextTransferable(StringBuffer(name))) { clipboard, contents ->
                    println("?!? lost content?")
                }

                Timer().schedule(
                    object : TimerTask() {
                        override fun run() {
                            val r = Robot()
                            with(r) {
                                autoDelay = 100
                                keyPress(KeyEvent.VK_CONTROL)
                                keyPress(KeyEvent.VK_V)
                                keyRelease(KeyEvent.VK_CONTROL)
                                keyRelease(KeyEvent.VK_V)
                                keyPress(KeyEvent.VK_ALT)
                                keyPress(KeyEvent.VK_P)
                                keyRelease(KeyEvent.VK_ALT)
                                keyRelease(KeyEvent.VK_P)
                            }
                        }
                    }, 1000
                )
            }

            else -> {
                println("wtf")
            }
        }
    }

    private fun getFileNameWithoutExtension(name: String) = name.split(".").first()

    private fun printFilesNameWithoutExtension(directory: PsiDirectory?) {
        directory?.files?.let { psiFiles ->
            psiFiles.map {
                getFileNameWithoutExtension(it.name)
            }.forEach {
                println("!!!  $it")
            }
        }
    }
}
