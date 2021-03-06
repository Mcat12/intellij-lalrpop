package com.mdrobnak.lalrpop.psi.ext

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import com.intellij.psi.util.parentOfType
import com.mdrobnak.lalrpop.psi.*
import com.mdrobnak.lalrpop.resolve.LpNonterminalReference

val LpNonterminalRef.arguments: LpNonterminalArguments?
    get() = this.nextSibling as? LpNonterminalArguments

fun LpNonterminalRef.createNonterminal() {
    val factory = LpElementFactory(project)

    val nonterminal = this.parentOfType<LpNonterminal>() ?: return
    val grammar = nonterminal.parent

    grammar.addAfter(
        factory.createNonterminal(this.text, this.arguments?.symbolList?.mapIndexed { index, _ -> "Rule${index + 1}" }),
        nonterminal,
    )

    grammar.addAfter(factory.createWhitespace("\n\n"), nonterminal)
}

abstract class LpNonterminalRefMixin(node: ASTNode) : ASTWrapperPsiElement(node), LpNonterminalRef {
    override fun getReference(): PsiReference = LpNonterminalReference(this)

    override fun resolveType(context: LpTypeResolutionContext, arguments: LpMacroArguments): String {
        return when (val ref = reference.resolve()) {
            is LpNonterminalName -> {
                val nonterminalParams = ref.nonterminalParams
                    ?: return ref.nonterminalParent.resolveType(context, LpMacroArguments(listOf(), listOf()))

                val nonterminal = ref.nonterminalParent

                this.arguments?.let { nonterminalArguments ->
                    nonterminal.resolveType(
                        context,
                        LpMacroArguments(
                            arguments.rootArguments,
                            nonterminalArguments.symbolList.zip(nonterminalParams.nonterminalParamList).map {
                                LpMacroArgument(it.first.resolveType(context, arguments), it.second.id.text)
                            })
                    )
                } ?: nonterminal.resolveType(context, LpMacroArguments(arguments.rootArguments, nonterminalParams.nonterminalParamList.map {
                    LpMacroArgument("()", it.id.text)
                }))
            }
            is LpNonterminalParam -> arguments.find { it.name == ref.id.text }?.rustType ?: ref.text
            else -> "()"
        }
    }
}
