package com.flutter.ui.utils.flutteruiutils

import com.intellij.ide.IdeView
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.psi.impl.file.PsiDirectoryFactory
import com.intellij.util.IncorrectOperationException
import org.apache.velocity.runtime.parser.ParseException
import java.awt.Dimension
import java.util.*
import javax.swing.*

class CreateScreenTemplate : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        MyDialog(e).showAndGet()
    }
}

class MyDialog(val event: AnActionEvent) : DialogWrapper(true) {
    init {
        title = "Create Flutter Screen"
        init()
    }

    lateinit var editName: JTextField
    lateinit var editBlocName: JTextField
    lateinit var editScreenName: JTextField


    override fun doOKAction() {
        val dataContext: DataContext = event.getDataContext()

        val view: IdeView = LangDataKeys.IDE_VIEW.getData(dataContext) ?: return

        val project = CommonDataKeys.PROJECT.getData(dataContext)
        val dir = view.orChooseDirectory
        if (dir == null || project == null) {
            throw IllegalArgumentException("Provided directory is null")
        }

        val projectPath = ProjectFileIndex.getInstance(project).getContentRootForFile(dir.virtualFile)
            ?: throw IllegalArgumentException("Unable to get project path")

        val relativePath = VfsUtilCore.getRelativePath(dir.virtualFile, projectPath)
            .orEmpty()

        val newPath = "${projectPath.toNioPath()}/$relativePath"

        val newDir = VfsUtil.createDirectories(newPath)
        val newDirForReals = PsiDirectoryFactory.getInstance(project).createDirectory(newDir)


        val template = FileTemplateManager.getInstance(dir.project).getInternalTemplate("Tmp")

        var screenName = editScreenName.text
        screenName = screenName.replaceFirstChar { (screenName.first().uppercase()) }

        var blocName = if (editBlocName.text.isNullOrBlank()) screenName else editBlocName.text
        blocName = blocName.replaceFirstChar { (blocName.first().uppercase()) }

        val strProp = Properties()
        strProp.setProperty("NAME1", screenName)
        strProp.setProperty("NAME2", blocName)

        try {
            val psiFile = FileTemplateUtil.createFromTemplate(
                template,
                editName.text,
                strProp,
                newDirForReals,
            )
                .containingFile
            val virtualFile = psiFile.virtualFile
            if (virtualFile != null) {
                FileEditorManager.getInstance(project).openFile(virtualFile, true)
            }
        } catch (e: ParseException) {
            throw IncorrectOperationException("Error parsing Velocity template: " + e.message, e as Throwable)
        } catch (e: IncorrectOperationException) {
            throw e
        } finally {
            this.close(0)
        }
    }

    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel()
        dialogPanel.layout = BoxLayout(dialogPanel, BoxLayout.Y_AXIS)
        dialogPanel.size = Dimension(300, 300)

        val labelTitle = JLabel("Create Flutter Screen")
        labelTitle.preferredSize = Dimension(100, 50)
        dialogPanel.add(labelTitle)

        val labelFileName = JLabel("File Name")
        labelFileName.preferredSize = Dimension(100, 30)
        dialogPanel.add(labelFileName)

        editName = JTextField()
        dialogPanel.add(editName)

        val labelBlocName = JLabel("Bloc Name (<name>Bloc) (empty to screen same)")
        labelBlocName.preferredSize = Dimension(100, 30)
        dialogPanel.add(labelBlocName)

        editBlocName = JTextField()
        dialogPanel.add(editBlocName)

        val labelScreenName = JLabel("Screen Name (<name>Screen)")
        labelScreenName.preferredSize = Dimension(100, 30)
        dialogPanel.add(labelScreenName)

        editScreenName = JTextField()
        dialogPanel.add(editScreenName)

        return dialogPanel
    }

}