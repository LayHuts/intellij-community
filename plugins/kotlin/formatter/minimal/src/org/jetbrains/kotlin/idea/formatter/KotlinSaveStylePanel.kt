// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.jetbrains.kotlin.idea.formatter

import com.intellij.application.options.CodeStyleAbstractPanel
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.ui.ComboBox
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.panels.HorizontalLayout
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.idea.base.resources.KotlinBundle
import java.awt.BorderLayout
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JPanel

internal class KotlinSaveStylePanel(settings: CodeStyleSettings) : CodeStyleAbstractPanel(KotlinLanguage.INSTANCE, null, settings) {
    override fun getRightMargin() = throw UnsupportedOperationException()
    override fun createHighlighter(scheme: EditorColorsScheme) = throw UnsupportedOperationException()
    override fun getFileType() = throw UnsupportedOperationException()
    override fun getPreviewText(): String? = null

    override fun getTabTitle(): String = KotlinBundle.message("formatter.title.load.save")

    private data class SaveItem(val label: String, val id: String?)

    private val saveDefaultsComboBox = ComboBox<SaveItem>()
    private val saveDefaultsItems = listOf(
        SaveItem("<ide defaults>", null),
        SaveItem(KotlinOfficialStyleGuide.CODE_STYLE_TITLE, KotlinOfficialStyleGuide.CODE_STYLE_ID),
        SaveItem(KotlinObsoleteStyleGuide.CODE_STYLE_TITLE, KotlinObsoleteStyleGuide.CODE_STYLE_ID),
    )

    private var selectedId: String?
        get() {
            val (_, id) = saveDefaultsComboBox.selectedItem as SaveItem
            return id
        }
        set(value) {
            saveDefaultsComboBox.selectedItem = saveDefaultsItems.firstOrNull { (_, id) -> id == value } ?: saveDefaultsItems.first()
        }

    private val jPanel = JPanel(BorderLayout()).apply {
        add(
            JBScrollPane(
                JPanel(VerticalLayout(JBUI.scale(5))).apply {
                    border = BorderFactory.createEmptyBorder(0, UIUtil.DEFAULT_HGAP, UIUtil.DEFAULT_VGAP, UIUtil.DEFAULT_HGAP)
                    add(JPanel(HorizontalLayout(JBUI.scale(5))).apply {
                        saveDefaultsItems.forEach {
                            saveDefaultsComboBox.addItem(it)
                        }

                        saveDefaultsComboBox.renderer = SimpleListCellRenderer.create("") {
                            it.label
                        }

                        add(JLabel(KotlinBundle.message("formatter.text.use.defaults.from")))
                        add(saveDefaultsComboBox)
                    })
                }
            )
        )
    }

    override fun apply(settings: CodeStyleSettings) {
        settings.kotlinCustomSettings.CODE_STYLE_DEFAULTS = selectedId
        settings.kotlinCommonSettings.CODE_STYLE_DEFAULTS = selectedId
    }

    override fun isModified(settings: CodeStyleSettings): Boolean {
        return selectedId != settings.kotlinCustomSettings.CODE_STYLE_DEFAULTS ||
                selectedId != settings.kotlinCommonSettings.CODE_STYLE_DEFAULTS
    }

    override fun getPanel() = jPanel

    override fun resetImpl(settings: CodeStyleSettings) {
        selectedId = settings.kotlinCustomSettings.CODE_STYLE_DEFAULTS ?: settings.kotlinCommonSettings.CODE_STYLE_DEFAULTS
    }

    override fun onSomethingChanged() {
        // There's no way settings are going to be changed from other tabs without calling resetImpl
    }
}
