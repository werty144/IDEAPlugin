package com.example.internship_plugin


import com.intellij.openapi.project.Project
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
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val toolWindowContent = TestContent()
        val content: Content = ContentFactory.getInstance().createContent(toolWindowContent.contentPanel, "", false)
        toolWindow.contentManager.addContent(content)

//        PsiManager.getInstance(project).addPsiTreeChangeListener(MyListener())
        project.messageBus.connect().subscribe(VirtualFileManager.VFS_CHANGES, MyFileListener())
        VirtualFileManager.VFS_CHANGES
//        val classesCollector = ClassesCollector(project)
//        GlobalScope.launch {
//            launch { classesCollector.startCollecting() }
//            classesCollector.classCollection.collect {
//                it -> println(it.toString())
//            }
//        }
    }

    internal class MyFileListener : BulkFileListener {
//        override fun before(events: MutableList<out VFileEvent>) {
//            println(events.toString())
//        }

        override fun after(events: MutableList<out VFileEvent>) {
            println(events.toString())
        }
    }

    internal class MyListener : PsiTreeChangeListener {
        override fun beforeChildAddition(event: PsiTreeChangeEvent){}

        override fun beforeChildRemoval(event: PsiTreeChangeEvent) {}
        override fun beforeChildReplacement(event: PsiTreeChangeEvent) {}

        override fun beforeChildMovement(event: PsiTreeChangeEvent) {}

        override fun beforeChildrenChange(event: PsiTreeChangeEvent) {
        }

        override fun beforePropertyChange(event: PsiTreeChangeEvent) {
        }

        override fun childAdded(event: PsiTreeChangeEvent) {
            println(event.toString())
        }

        override fun childRemoved(event: PsiTreeChangeEvent) {
            println(event.toString())
        }

        override fun childReplaced(event: PsiTreeChangeEvent) {
        }

        override fun childrenChanged(event: PsiTreeChangeEvent) {
        }

        override fun childMoved(event: PsiTreeChangeEvent) {
        }

        override fun propertyChanged(event: PsiTreeChangeEvent) {
        }

    }

    fun printClassess(project: Project) {
        val fileNames = FilenameIndex.getAllFilesByExt(project, "java", GlobalSearchScope.projectScope(project))
        val classes = fileNames.mapNotNull {
            PsiManager.getInstance(project).findFile(it)?.childrenOfType<PsiClassImpl>()?.map {
                    jt -> jt.toString()
            }?.toList()
        }
        println(classes.toList().flatten().toString())
    }

    internal class TestContent() {
        val contentPanel = JPanel()
        val text = JLabel()

        init {
            contentPanel.setLocation(0, 0)
            text.text = "Huy!"
            contentPanel.add(text)
        }
    }
}