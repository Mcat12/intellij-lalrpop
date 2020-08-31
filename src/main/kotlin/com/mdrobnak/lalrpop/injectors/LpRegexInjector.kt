package com.mdrobnak.lalrpop.injectors

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.mdrobnak.lalrpop.psi.impl.LpQuotedLiteralImpl
import org.intellij.lang.regexp.RegExpLanguage

class LpRegexInjector : MultiHostInjector {
    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        if (!context.isValid || context !is LpQuotedLiteralImpl || !context.isRegex()) {
            return
        }

        val range = TextRange(2, context.textLength - 1)
        registrar
            .startInjecting(RegExpLanguage.INSTANCE)
            .addPlace(null, null, context, range)
            .doneInjecting()
    }

    override fun elementsToInjectIn(): List<Class<out PsiElement>> =
        listOf(LpQuotedLiteralImpl::class.java)
}