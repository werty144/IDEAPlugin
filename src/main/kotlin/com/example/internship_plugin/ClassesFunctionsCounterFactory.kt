package com.example.internship_plugin


import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.source.PsiClassImpl
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.childrenOfType
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel


class ClassesFunctionsCounterFactory : ToolWindowFactory {
    private val toolWindowContent = TestContent()

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val content: Content = ContentFactory.getInstance().createContent(toolWindowContent.contentPanel, "", false)
        toolWindow.contentManager.addContent(content)
        project.messageBus.connect().subscribe(PsiModificationTracker.TOPIC, MyPsiListener(project, this))
    }

    class FileClassesFunctions(val file: String, val classes: Int, val functions: Int)

    internal class MyPsiListener(
        private val project: Project,
        private val classesFunctionsCounterFactory: ClassesFunctionsCounterFactory
    ) : PsiModificationTracker.Listener {
        init {
            val data = collectData()
            classesFunctionsCounterFactory.redrawContent(data)
        }

        private fun classesInFile(file: VirtualFile): List<PsiClassImpl> {
            return PsiManager.getInstance(project).findFile(file)!!.childrenOfType<PsiClassImpl>()
        }

        private fun collectData(): List<FileClassesFunctions> {
            val files = FilenameIndex.getAllFilesByExt(project, "java", GlobalSearchScope.projectScope(project))
            return files.mapNotNull { file ->
                val classes = classesInFile(file)
                FileClassesFunctions(file.name, classes.size, classes.sumOf { it.methods.size })
            }
        }

        override fun modificationCountChanged() {
            val data = collectData()
            classesFunctionsCounterFactory.redrawContent(data)
        }
    }

    fun redrawContent(fileClassesFunctions: List<FileClassesFunctions>) {
        toolWindowContent.contentPanel.removeAll()
        fileClassesFunctions.forEach {
            val label = JLabel()
            label.text = "${it.file}: ${it.classes} classes; ${it.functions} functions"
            toolWindowContent.contentPanel.add(label)
        }
        toolWindowContent.contentPanel.revalidate()
        toolWindowContent.contentPanel.repaint()
    }

    class TestContent() {
        val contentPanel = JPanel()

        init {
            contentPanel.setLocation(0, 0)
            contentPanel.layout = BoxLayout(contentPanel, BoxLayout.Y_AXIS)
        }
    }
}