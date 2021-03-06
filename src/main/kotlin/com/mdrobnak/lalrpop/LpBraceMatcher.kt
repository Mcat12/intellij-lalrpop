package com.mdrobnak.lalrpop

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.parentOfTypes
import com.mdrobnak.lalrpop.psi.*
import org.rust.lang.core.psi.ext.startOffset

object LpBraceMatcher : PairedBraceMatcher {
    private val pairsArray = arrayOf(
        BracePair(LpElementTypes.LBRACE, LpElementTypes.RBRACE, true),
        BracePair(LpElementTypes.LBRACKET, LpElementTypes.RBRACKET, false),
        BracePair(LpElementTypes.LPAREN, LpElementTypes.RPAREN, false),
        BracePair(LpElementTypes.LESSTHAN, LpElementTypes.GREATERTHAN, false),
    )

    override fun getPairs(): Array<BracePair> = pairsArray

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean = true

    override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int): Int =
        file.findElementAt(openingBraceOffset)?.parentOfTypes(
            LpUseStmt::class,
            LpNonterminal::class,
            LpMatchToken::class,
            LpExternToken::class,
            LpEnumToken::class
        )?.startOffset ?: openingBraceOffset
}