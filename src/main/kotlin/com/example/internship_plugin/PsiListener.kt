package com.example.internship_plugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.source.PsiClassImpl
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.childrenOfType
import org.jetbrains.annotations.TestOnly
import org.jetbrains.annotations.VisibleForTesting

class PsiListener(
    private val project: Project,
    private val classesFunctionsCounterFactory: ClassesFunctionsCounterFactory
) : PsiModificationTracker.Listener {
    init {
        modificationCountChanged()
    }

    private fun classesInFile(file: VirtualFile): List<PsiClassImpl> {
        return PsiManager.getInstance(project).findFile(file)?.childrenOfType<PsiClassImpl>() ?: listOf()
    }

    @VisibleForTesting
    fun collectData(): List<FileClassesFunctions> {
        val files = FilenameIndex.getAllFilesByExt(project, "java", GlobalSearchScope.projectScope(project))
        return files.mapNotNull { file ->
            val classes = classesInFile(file)
            FileClassesFunctions(
                file.name,
                classes.size,
                classes.sumOf { it.methods.size })
        }
    }

    override fun modificationCountChanged() {
        val data = collectData()
        classesFunctionsCounterFactory.redrawContent(data)
    }
}