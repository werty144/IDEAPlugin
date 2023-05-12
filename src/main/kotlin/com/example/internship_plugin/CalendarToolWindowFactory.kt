package com.example.internship_plugin


import com.intellij.ide.highlighter.JavaFileType
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
import com.intellij.psi.util.childrenOfType
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import javax.swing.JLabel
import javax.swing.JPanel


class CalendarToolWindowFactory : ToolWindowFactory {
    val filesToClasses = HashMap<String, List<String>>()
    private val toolWindowContent = TestContent()
    fun initFilesToClasses(project: Project) {
        val files = FilenameIndex.getAllFilesByExt(project, "java", GlobalSearchScope.projectScope(project))
        files.forEach { file ->
            filesToClasses[file.name] = classesFromFile(project, file).mapNotNull { it.name }
        }
    }
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val content: Content = ContentFactory.getInstance().createContent(toolWindowContent.contentPanel, "", false)
        toolWindow.contentManager.addContent(content)
        project.messageBus.connect().subscribe(VirtualFileManager.VFS_CHANGES, MyFileListener(project, this))
        initFilesToClasses(project)
        redrawContent()
    }

    private fun classesFromFile(project: Project, file: VirtualFile): List<PsiClassImpl> {
        return PsiManager.getInstance(project).findFile(file)!!.childrenOfType<PsiClassImpl>()
    }

    internal class MyFileListener(
        private val project: Project,
        val calendarToolWindowFactory: CalendarToolWindowFactory) : BulkFileListener {
        override fun after(events: MutableList<out VFileEvent>) {
            events.forEach {
                it.file?.let { file ->
                    if (file.fileType == JavaFileType.INSTANCE) {
                        if (!file.isValid) {
                            calendarToolWindowFactory.filesToClasses.remove(file.name)
                        } else {
                            calendarToolWindowFactory.filesToClasses[file.name] =
                                calendarToolWindowFactory.classesFromFile(project, file).mapNotNull { it.name }
                        }
                    }
                }
            }
            calendarToolWindowFactory.redrawContent()
        }
    }

    fun redrawContent() {
        toolWindowContent.text.text = filesToClasses.values.flatten().joinToString()
    }

    class TestContent() {
        val contentPanel = JPanel()
        val text = JLabel()

        init {
            contentPanel.setLocation(0, 0)
            text.text = "Huy!"
            contentPanel.add(text)
        }
    }
}