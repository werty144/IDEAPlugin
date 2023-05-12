package com.example.internship_plugin


import com.intellij.openapi.project.Project

import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.psi.*
import com.intellij.psi.impl.source.PsiClassImpl
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.tree.IElementType
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
        FilenameIndex.getAllFilesByExt(project, "java", GlobalSearchScope.projectScope(project)).forEach {
            PsiManager.getInstance(project).findFile(it)?.childrenOfType<PsiClassImpl>()?.forEach { jt ->
                println(jt.toString())
            }
            }
    }

    internal class MyListener : PsiTreeChangeListener {
        override fun beforeChildAddition(event: PsiTreeChangeEvent) {
            println(event.toString())
        }

        override fun beforeChildRemoval(event: PsiTreeChangeEvent) {
            println(event.toString())
        }

        override fun beforeChildReplacement(event: PsiTreeChangeEvent) {
            println(event.toString())
        }

        override fun beforeChildMovement(event: PsiTreeChangeEvent) {
            println(event.toString())
        }

        override fun beforeChildrenChange(event: PsiTreeChangeEvent) {
            println(event.toString())
        }

        override fun beforePropertyChange(event: PsiTreeChangeEvent) {
            println(event.toString())
        }

        override fun childAdded(event: PsiTreeChangeEvent) {
            println(event.toString())
        }

        override fun childRemoved(event: PsiTreeChangeEvent) {
            println(event.toString())
        }

        override fun childReplaced(event: PsiTreeChangeEvent) {
            println(event.toString())
        }

        override fun childrenChanged(event: PsiTreeChangeEvent) {
            println(event.toString())
        }

        override fun childMoved(event: PsiTreeChangeEvent) {
            println(event.toString())
        }

        override fun propertyChanged(event: PsiTreeChangeEvent) {
            println(event.toString())
        }

    }

    internal class TestContent() {
        val contentPanel = JPanel()
        private val text = JLabel()

        init {
            contentPanel.setLocation(0, 0)
            text.text = "Huy!"
            contentPanel.add(text)
        }
    }
}