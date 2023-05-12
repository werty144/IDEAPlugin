package com.example.internship_plugin

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.source.PsiClassImpl
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.childrenOfType
import it.unimi.dsi.fastutil.ints.IntSets.SynchronizedSet
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*


class ClassesCollector(private val project: Project) {
    private val _classCollection = MutableStateFlow<List<String>>(listOf())
    val classCollection = _classCollection.asStateFlow()
    val classes = Collections.synchronizedList(mutableListOf<String>())

    suspend fun startCollecting() {
        while (true) {
            delay(1000)
            val fileNames = FilenameIndex.getAllFilesByExt(project, "java", GlobalSearchScope.projectScope(project))
            val classes = fileNames.mapNotNull {
                PsiManager.getInstance(project).findFile(it)?.childrenOfType<PsiClassImpl>()?.map {
                    jt -> jt.toString()
                }?.toList()
            }
            _classCollection.update { classes.toList().flatten() }
        }
    }
}