package com.example.internship_plugin_test

import com.example.internship_plugin.ClassesFunctionsCounterFactory
import com.example.internship_plugin.FileClassesFunctions
import com.example.internship_plugin.PsiListener
import com.intellij.ide.highlighter.JavaFileType
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import groovy.io.FileType
import junit.framework.TestCase


class PsiListenerTest : BasePlatformTestCase() {
    fun test1() {
        val psiListener = PsiListener(project, ClassesFunctionsCounterFactory())
        myFixture.createFile("file1.java",
            """
                class MyClass {}
            """.trimIndent()
        )
        val data = psiListener.collectData()
        TestCase.assertTrue(data.size == 1)
        TestCase.assertTrue(data[0] == FileClassesFunctions("file1.java", 1, 0))
    }
}