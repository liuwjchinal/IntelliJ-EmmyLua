/*
 * Copyright (c) 2017. tangzx(love.tangzx@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tang.intellij.lua.codeInsight;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import com.intellij.util.Query;
import com.tang.intellij.lua.comment.psi.LuaDocClassDef;
import com.tang.intellij.lua.lang.type.LuaType;
import com.tang.intellij.lua.psi.LuaClassMethodDef;
import com.tang.intellij.lua.psi.LuaClassMethodName;
import com.tang.intellij.lua.psi.search.LuaClassInheritorsSearch;
import com.tang.intellij.lua.psi.search.LuaOverridingMethodsSearch;
import com.tang.intellij.lua.search.SearchContext;
import com.tang.intellij.lua.stubs.index.LuaClassMethodIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * line marker
 * Created by tangzx on 2016/12/11.
 */
public class LuaLineMarkerProvider implements LineMarkerProvider {

    private static final Function<LuaClassMethodDef, String> overridingMethodTooltipProvider = (classMethodDef) -> {
        final StringBuilder builder = new StringBuilder("<html>Is overridden in:");
        String name = classMethodDef.getName();
        if (name != null) {
        }
        return builder.toString();
    };

    private static final LuaLineMarkerNavigator<LuaClassMethodDef> overridingMethodNavigator = new LuaLineMarkerNavigator<LuaClassMethodDef>() {

        @Override
        protected String getTitle(LuaClassMethodDef elt) {
            return "Choose Overriding Method of " + elt.getName();
        }

        @NotNull
        @Override
        protected Query<LuaClassMethodDef> search(LuaClassMethodDef elt) {
            return LuaOverridingMethodsSearch.search(elt);
        }
    };

    private static final Function<LuaDocClassDef, String> subclassTooltipProvider = LuaDocClassDef::getName;

    private static final LuaLineMarkerNavigator<LuaDocClassDef> subclassNavigator = new LuaLineMarkerNavigator<LuaDocClassDef>() {
        @Override
        protected String getTitle(LuaDocClassDef elt) {
            return "Choose Subclass of " + elt.getName();
        }

        @NotNull
        @Override
        protected Query<LuaDocClassDef> search(LuaDocClassDef elt) {
            Project project = elt.getProject();
            return LuaClassInheritorsSearch.search(GlobalSearchScope.allScope(project), project, elt.getName());
        }
    };

    private void collectNavigationMarkers(@NotNull PsiElement element, Collection<? super LineMarkerInfo> result) {
        if (element instanceof LuaClassMethodName) {
            LuaClassMethodName classMethodName = (LuaClassMethodName) element;
            LuaClassMethodDef methodDef = PsiTreeUtil.getParentOfType(element, LuaClassMethodDef.class);
            assert methodDef != null;
            Project project = methodDef.getProject();
            SearchContext context = new SearchContext(project);
            LuaType type = methodDef.getClassType(context);

            //OverridingMethod
            PsiElement classMethodNameId = classMethodName.getId();
            if (type != null) {
                String methodName = methodDef.getName();
                assert methodName != null;
                LuaType superType = type.getSuperClass(context);

                while (superType != null) {
                    String superTypeName = superType.getClassName();
                    LuaClassMethodDef superMethod = LuaClassMethodIndex.findMethodWithName(superTypeName, methodName, context);
                    if (superMethod != null) {
                        NavigationGutterIconBuilder<PsiElement> builder =
                                NavigationGutterIconBuilder.create(AllIcons.Gutter.OverridingMethod)
                                        .setTargets(superMethod)
                                        .setTooltipText("Override in " + superTypeName);
                        result.add(builder.createLineMarkerInfo(classMethodNameId));
                        break;
                    }
                    superType = superType.getSuperClass(context);
                }
            }

            // OverridenMethod
            Query<LuaClassMethodDef> search = LuaOverridingMethodsSearch.search(methodDef);
            if (search.findFirst() != null) {
                result.add(new LineMarkerInfo<>(methodDef,
                        classMethodNameId.getTextRange(),
                        AllIcons.Gutter.OverridenMethod,
                        Pass.LINE_MARKERS,
                        overridingMethodTooltipProvider,
                        overridingMethodNavigator,
                        GutterIconRenderer.Alignment.CENTER));
            }
        }
        else if (element instanceof LuaDocClassDef) {
            LuaDocClassDef docClassDef = (LuaDocClassDef) element;
            LuaType classType = docClassDef.getClassType();
            Project project = element.getProject();
            Query<LuaDocClassDef> query = LuaClassInheritorsSearch.search(GlobalSearchScope.allScope(project), project, classType.getClassName());
            if (query.findFirst() != null) {
                result.add(new LineMarkerInfo<>(docClassDef,
                        docClassDef.getTextRange(),
                        AllIcons.Gutter.OverridenMethod,
                        Pass.LINE_MARKERS,
                        subclassTooltipProvider,
                        subclassNavigator,
                        GutterIconRenderer.Alignment.CENTER));
            }
        }
    }

    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement psiElement) {
        return null;
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> list, @NotNull Collection<LineMarkerInfo> collection) {
        for (PsiElement element : list) {
            collectNavigationMarkers(element, collection);
        }
    }
}
