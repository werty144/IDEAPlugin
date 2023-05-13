package com.example.internship_plugin


import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel


class ClassesFunctionsCounterFactory : ToolWindowFactory {
    private val pluginContent = PluginContent()

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val content: Content = ContentFactory.getInstance().createContent(pluginContent.contentPanel, "", false)
        toolWindow.contentManager.addContent(content)
        project.messageBus.connect().subscribe(PsiModificationTracker.TOPIC, PsiListener(project, this))
    }

    fun redrawContent(fileClassesFunctions: List<FileClassesFunctions>) {
        pluginContent.contentPanel.removeAll()
        fileClassesFunctions.forEach {
            val label = JLabel()
            label.text = "${it.file}: ${it.classes} classes; ${it.functions} functions"
            pluginContent.contentPanel.add(label)
        }
        pluginContent.contentPanel.revalidate()
        pluginContent.contentPanel.repaint()
    }

    class PluginContent() {
        val contentPanel = JPanel()

        init {
            contentPanel.setLocation(0, 0)
            contentPanel.layout = BoxLayout(contentPanel, BoxLayout.Y_AXIS)
        }
    }
}

data class FileClassesFunctions(val file: String, val classes: Int, val functions: Int)