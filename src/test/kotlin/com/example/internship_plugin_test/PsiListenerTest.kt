package com.example.internship_plugin_test

import com.example.internship_plugin.ClassesFunctionsCounterFactory
import com.example.internship_plugin.FileClassesFunctions
import com.example.internship_plugin.PsiListener
import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.application.writeAction
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.writeText
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.utils.vfs.deleteRecursively
import groovy.io.FileType
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking


class PsiListenerTest : BasePlatformTestCase() {
    fun testSingleFileSingleClass() {
        val psiListener = PsiListener(project, ClassesFunctionsCounterFactory())
        myFixture.createFile("file1.java",
            """
                class MyClass {}
            """.trimIndent()
        )
        val data = psiListener.collectData()
        TestCase.assertTrue(data.size == 1)
        TestCase.assertTrue(FileClassesFunctions("file1.java", 1, 0) in data)
    }

    fun testSingleFileMultipleClass() {
        val psiListener = PsiListener(project, ClassesFunctionsCounterFactory())
        myFixture.createFile("file1.java",
            """
                class MyClass {}
                class MyClass2 {}
            """.trimIndent()
        )
        val data = psiListener.collectData()
        TestCase.assertTrue(data.size == 1)
        TestCase.assertTrue(FileClassesFunctions("file1.java", 2, 0) in data)
    }

    fun testMultipleFileMultipleClass() {
        val psiListener = PsiListener(project, ClassesFunctionsCounterFactory())
        myFixture.createFile("file1.java",
            """
                class MyClass {}
            """.trimIndent()
        )
        myFixture.createFile("file2.java",
            """
                class MyClass2 {}
            """.trimIndent()
        )
        val data = psiListener.collectData()
        TestCase.assertTrue(data.size == 2)
        TestCase.assertTrue(FileClassesFunctions("file1.java", 1, 0) in data)
        TestCase.assertTrue(FileClassesFunctions("file2.java", 1, 0) in data)
    }

    fun testSingleFileFunctions() {
        val psiListener = PsiListener(project, ClassesFunctionsCounterFactory())
        myFixture.createFile("file1.java",
            """
                class MyClass {
                    public void f(){}
                }
            """.trimIndent()
        )
        val data = psiListener.collectData()
        TestCase.assertTrue(data.size == 1)
        TestCase.assertTrue(FileClassesFunctions("file1.java", 1, 1) in data)
    }

    fun testMultipleFileFunctions() {
        val psiListener = PsiListener(project, ClassesFunctionsCounterFactory())
        myFixture.createFile("file1.java",
            """
                class MyClass {
                    public void f(){}
                    public void g(){}
                }
            """.trimIndent()
        )
        myFixture.createFile("file2.java",
            """
                class MyClass2 {
                    public void f(){}
                    public void h(){}
                }
            """.trimIndent()
        )
        val data = psiListener.collectData()
        TestCase.assertTrue(data.size == 2)
        TestCase.assertTrue(FileClassesFunctions("file1.java", 1, 2) in data)
        TestCase.assertTrue(FileClassesFunctions("file2.java", 1, 2) in data)
    }

    fun testAddFile() {
        val psiListener = PsiListener(project, ClassesFunctionsCounterFactory())
        myFixture.createFile("file1.java",
            """
                class MyClass {}
            """.trimIndent()
        )
        val data = psiListener.collectData()
        TestCase.assertTrue(data.size == 1)
        TestCase.assertTrue(FileClassesFunctions("file1.java", 1, 0) in data)
        myFixture.createFile("file2.java",
            """
                class MyClass2 {}
            """.trimIndent()
        )
        val data2 = psiListener.collectData()
        TestCase.assertTrue(data2.size == 2)
        TestCase.assertTrue(FileClassesFunctions("file1.java", 1, 0) in data2)
        TestCase.assertTrue(FileClassesFunctions("file2.java", 1, 0) in data2)
    }
}