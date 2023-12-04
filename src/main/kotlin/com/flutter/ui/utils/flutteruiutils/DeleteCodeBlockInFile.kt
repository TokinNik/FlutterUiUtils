package com.flutter.ui.utils.flutteruiutils

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.VisualPosition
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import com.intellij.util.ui.TextTransferable
import org.jetbrains.kotlin.com.intellij.psi.PsiMethodCallExpression
import org.jetbrains.kotlin.com.intellij.psi.impl.source.PsiMethodImpl
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi2ir.endOffsetOrUndefined
import java.awt.Robot
import java.awt.Toolkit
import java.awt.event.KeyEvent
import java.util.*


class DeleteCodeBlockInFile : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        deleteCodeBlockInFile(e)
    }

    private fun deleteCodeBlockInFile(e: AnActionEvent) {
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

                selectedElement.printAllChildRec()
            }

            else -> {
                println("wtf")
            }
        }

        val editor: Editor? = project?.let { FileEditorManager.getInstance(it).selectedTextEditor }
        val position: VisualPosition? = editor?.caretModel?.visualPosition
        val document: Document? = editor?.document
        if (position != null) {
            WriteCommandAction.runWriteCommandAction(project) {
                runWriteAction()
                {
                    val deletedCodeBlockName = "useInStandardFeature"

                    document?.let {

                        fun findBlock(deletedCodeBlock: String): PsiElement? =
                            (selectedElement as PsiFile).findInChildRec { e ->
//                                println("check: $e - ${e.javaClass}  = ${e.javaClass == KtCallExpression::class.java} ?? ${e is KtCallExpression}")
                                if (e is KtCallExpression) {
                                    val name = (e as KtCallExpression).text // maybe other method
                                    println("Found method: name = $name")
                                    name.contains(deletedCodeBlock)
                                } else
                                    false
                            }

                        var block: PsiElement? = findBlock(deletedCodeBlockName)
                        while (block != null) {
                            println("doc len: ${document.textLength}")
                            println("offsets: ${block.startOffset} -- ${block.endOffset}")
                            it.replaceString(block.startOffset, block.endOffset, "//|REMOVED|")
                            block = findBlock(deletedCodeBlockName)
                        }
                    }
                }
            }
        }
    }

    private fun getFileNameWithoutExtension(name: String) = name.split(".").first()

    fun printFilesNameWithoutExtension(directory: PsiDirectory?) {
        directory?.files?.let { psiFiles ->
            psiFiles.map {
                getFileNameWithoutExtension(it.name)
            }.forEach {
                println("!!!  $it")
            }
        }
    }
}
