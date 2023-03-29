package com.flutter.ui.utils.flutteruiutils

import com.intellij.ide.IdeView
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
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

    private val dataContext: DataContext = event.dataContext
    private val view: IdeView? = LangDataKeys.IDE_VIEW.getData(dataContext)
    private val project: Project? = CommonDataKeys.PROJECT.getData(dataContext)
    private val dir: PsiDirectory? = view?.orChooseDirectory

    private lateinit var editName: JTextField
    private lateinit var editBlocName: JTextField
    private lateinit var editScreenName: JTextField

    private lateinit var config: ScreenConfig

    override fun doOKAction() {
        if (view == null) return

        if (dir == null || project == null) {
            throw IllegalArgumentException("Provided directory is null")
        }

        val projectPath = ProjectFileIndex.getInstance(project).getContentRootForFile(dir.virtualFile)
            ?: throw IllegalArgumentException("Unable to get project path")

        val relativePath = VfsUtilCore.getRelativePath(dir.virtualFile, projectPath).orEmpty()

        var screenName = editScreenName.text
        screenName = screenName.replaceFirstChar { (screenName.first().uppercase()) }

        var blocName = if (editBlocName.text.isNullOrBlank()) screenName else editBlocName.text
        blocName = blocName.replaceFirstChar { (blocName.first().uppercase()) }

        val screenFileName = editName.text
        val blocFileName = blocName.lowercase() + "_bloc"
        val stateFileName = blocName.lowercase() + "_state"
        val eventFileName = blocName.lowercase() + "_event"

        config = ScreenConfig(screenFileName, screenName, blocFileName, blocName, stateFileName, eventFileName)

        createScreenTemplate(projectPath, relativePath)
        createBlocTemplate(projectPath, relativePath)
        createStateTemplate(projectPath, relativePath)
        createEventTemplate(projectPath, relativePath)
    }

    private fun createScreenTemplate(projectPath: VirtualFile, relativePath: String) {
        val template = FileTemplateManager.getInstance(dir!!.project).getInternalTemplate("tmp_screen")

        val screenFilePath = "${projectPath.toNioPath()}/$relativePath/${config.screenFileName}"
        val screenFileNewDir = VfsUtil.createDirectories(screenFilePath)
        val screenFileDirectory = PsiDirectoryFactory.getInstance(project).createDirectory(screenFileNewDir)

        val strProp = Properties()
        strProp.setProperty("SCREEN_NAME", config.screenName)
        strProp.setProperty("BLOC_NAME", config.blocName)
        strProp.setProperty("SCREEN_FILE_NAME", config.screenFileName)
        strProp.setProperty("BLOC_FILE_NAME", config.blocFileName)

        createFileFromTemplate(template, config.screenFileName, strProp, screenFileDirectory)
    }

    private fun createBlocTemplate(projectPath: VirtualFile, relativePath: String) {
        val template = FileTemplateManager.getInstance(dir!!.project).getInternalTemplate("tmp_bloc")

        val blocFilePath = "${projectPath.toNioPath()}/$relativePath/${config.screenFileName}/bloc"
        val blocFileNewDir = VfsUtil.createDirectories(blocFilePath)
        val blocFileDirectory = PsiDirectoryFactory.getInstance(project).createDirectory(blocFileNewDir)

        val strProp = Properties()
        strProp.setProperty("BLOC_NAME", config.blocName)
        strProp.setProperty("BLOC_STATE_FILE_NAME", config.stateFileName)
        strProp.setProperty("BLOC_EVENT_FILE_NAME", config.eventFileName)

        createFileFromTemplate(template, config.blocFileName, strProp, blocFileDirectory)
    }

    private fun createStateTemplate(projectPath: VirtualFile, relativePath: String) {
        val template = FileTemplateManager.getInstance(dir!!.project).getInternalTemplate("tmp_state")

        val blocFilePath = "${projectPath.toNioPath()}/$relativePath/${config.screenFileName}/bloc"
        val blocFileNewDir = VfsUtil.createDirectories(blocFilePath)
        val blocFileDirectory = PsiDirectoryFactory.getInstance(project).createDirectory(blocFileNewDir)

        val strProp = Properties()
        strProp.setProperty("BLOC_NAME", config.blocName)
        strProp.setProperty("BLOC_FILE_NAME", config.blocFileName)

        createFileFromTemplate(template, config.stateFileName, strProp, blocFileDirectory)
    }

    private fun createEventTemplate(projectPath: VirtualFile, relativePath: String) {
        val template = FileTemplateManager.getInstance(dir!!.project).getInternalTemplate("tmp_event")

        val blocFilePath = "${projectPath.toNioPath()}/$relativePath/${config.screenFileName}/bloc"
        val blocFileNewDir = VfsUtil.createDirectories(blocFilePath)
        val blocFileDirectory = PsiDirectoryFactory.getInstance(project).createDirectory(blocFileNewDir)

        val strProp = Properties()
        strProp.setProperty("BLOC_NAME", config.blocName)
        strProp.setProperty("BLOC_FILE_NAME", config.blocFileName)

        createFileFromTemplate(template, config.eventFileName, strProp, blocFileDirectory)
    }

    private fun createFileFromTemplate(
        template: FileTemplate,
        screenFileName: String,
        strProp: Properties,
        newDirForReals: PsiDirectory,
    ) {
        try {
            val psiFile = FileTemplateUtil.createFromTemplate(
                template,
                screenFileName,
                strProp,
                newDirForReals,
            )
                .containingFile
            val virtualFile = psiFile.virtualFile
            if (virtualFile != null && project != null) {
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

data class ScreenConfig(
    val screenFileName: String,
    val screenName: String,
    val blocFileName: String,
    val blocName: String,
    val stateFileName: String,
    val eventFileName: String,
)