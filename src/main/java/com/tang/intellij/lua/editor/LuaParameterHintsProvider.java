package com.tang.intellij.lua.editor;

import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.codeInsight.hints.InlayParameterHintsProvider;
import com.intellij.codeInsight.hints.MethodInfo;
import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.tang.intellij.lua.psi.LuaArgs;
import com.tang.intellij.lua.psi.LuaCallExpr;
import com.tang.intellij.lua.psi.LuaExpr;
import com.tang.intellij.lua.psi.LuaExprList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * Created by TangZX on 2016/12/14.
 */
public class LuaParameterHintsProvider implements InlayParameterHintsProvider {
    @NotNull
    @Override
    public List<InlayInfo> getParameterHints(PsiElement psiElement) {
        List<InlayInfo> list = new ArrayList<>();
        if (psiElement instanceof LuaCallExpr) {
            LuaCallExpr callExpr = (LuaCallExpr) psiElement;

            LuaArgs args = callExpr.getArgs();
            if (args != null) {
                LuaExprList luaExprList = args.getExprList();
                if (luaExprList != null) {
                    List<LuaExpr> exprList = luaExprList.getExprList();
                    for (int i = 0; i < exprList.size(); i++) {
                        LuaExpr expr = exprList.get(i);
                        list.add(new InlayInfo("AAA", expr.getTextOffset()));
                    }
                }
            }
        }

        return list;
    }

    @Nullable
    @Override
    public MethodInfo getMethodInfo(PsiElement psiElement) {
        if (psiElement instanceof LuaCallExpr) {
            List<String> list = new ArrayList<>();
            return new MethodInfo("MethodInfo", list);
        }
        return null;
    }

    @NotNull
    @Override
    public Set<String> getDefaultBlackList() {
        Set<String> stringSet = new HashSet<>();
        stringSet.add("test");
        return stringSet;
    }

    @Nullable
    @Override
    public Language getBlackListDependencyLanguage() {
        return null;
    }
}
