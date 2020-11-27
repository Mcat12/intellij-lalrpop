package com.mdrobnak.lalrpop.psi.ext

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.mdrobnak.lalrpop.psi.LpElementTypes
import com.mdrobnak.lalrpop.psi.LpSelectedType
import com.mdrobnak.lalrpop.psi.LpSymbol
import com.mdrobnak.lalrpop.psi.NonterminalGenericArgument
import org.rust.lang.core.psi.ext.childrenWithLeaves
import org.rust.lang.core.psi.ext.elementType

val LpSymbol.isExplicitlySelected: Boolean
    get() = this.childrenWithLeaves.first().elementType == LpElementTypes.LESSTHAN

val LpSymbol.isNamed: Boolean
    get() = this.symbolName != null

val LpSymbol.isMutable: Boolean
    get() = this.symbolName?.childrenWithLeaves?.any { it.elementType == LpElementTypes.MUT } ?: false

val LpSymbol.name: String?
    get() = this.symbolName?.childrenWithLeaves?.find { it.elementType == LpElementTypes.ID }?.text

fun LpSymbol.removeName() {
    this.symbolName?.delete()
}

abstract class LpSymbolMixin(node: ASTNode) : ASTWrapperPsiElement(node), LpSymbol {
    fun getSelectedType(): LpSelectedType {
        val isMutable = this.isMutable
        val name = this.name
        val type = this.resolveType(listOf())

        return if (name != null) {
            LpSelectedType.WithName(isMutable, name, type)
        } else {
            LpSelectedType.WithoutName(type)
        }
    }

    override fun resolveType(arguments: List<NonterminalGenericArgument>): String =
        symbol0.resolveType(arguments)
}
