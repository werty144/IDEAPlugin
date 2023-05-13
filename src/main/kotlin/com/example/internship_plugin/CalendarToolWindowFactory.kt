package com.example.internship_plugin


import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.AnActionResult
import com.intellij.openapi.actionSystem.ex.AnActionListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiTreeChangeEvent
import com.intellij.psi.PsiTreeChangeListener
import com.intellij.psi.impl.source.PsiClassImpl
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.childrenOfType
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import javax.swing.BoxLayout
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.OverlayLayout


class CalendarToolWindowFactory : ToolWindowFactory {
    private val toolWindowContent = TestContent()

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val content: Content = ContentFactory.getInstance().createContent(toolWindowContent.contentPanel, "", false)
        toolWindow.contentManager.addContent(content)
        project.messageBus.connect().subscribe(PsiModificationTracker.TOPIC, MyPsiListener(project, this))
    }

    internal class MyPsiListener(val project: Project,
                                    val calendarToolWindowFactory: CalendarToolWindowFactory) : PsiModificationTracker.Listener {
        init {
            val classes = collectClasses()
            calendarToolWindowFactory.redrawContent(classes)
        }

        private fun classesFromFile(file: VirtualFile): List<PsiClassImpl> {
            return PsiManager.getInstance(project).findFile(file)!!.childrenOfType<PsiClassImpl>()
        }

        fun collectClasses(): List<PsiClassImpl> {
            val files = FilenameIndex.getAllFilesByExt(project, "java", GlobalSearchScope.projectScope(project))
            return files.mapNotNull { file ->  classesFromFile(file)}.flatten()
        }

        override fun modificationCountChanged() {
            val classes = collectClasses()
            calendarToolWindowFactory.redrawContent(classes)
        }
    }

    fun redrawContent(classes: List<PsiClassImpl>) {
        toolWindowContent.contentPanel.removeAll()
        classes.forEach {
            val label = JLabel()
            label.text = it.name
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