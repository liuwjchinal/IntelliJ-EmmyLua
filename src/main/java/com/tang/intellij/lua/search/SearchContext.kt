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

package com.tang.intellij.lua.search

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.ProjectAndLibrariesScope
import com.intellij.util.indexing.AdditionalIndexedRootsScope
import com.tang.intellij.lua.project.LuaPredefinedLibraryProvider

/**

 * Created by tangzx on 2017/1/14.
 */
class SearchContext(val project: Project, private val currentFile: PsiFile? = null, var forStore: Boolean = false) {
    constructor(project: Project) : this(project, null)

    /**
     * 用于有多返回值的索引设定
     */
    var index: Int = 0

    private var scope: GlobalSearchScope? = null

    fun getScope(): GlobalSearchScope {
        if (scope == null) {
            scope = if (isDumb) {
                GlobalSearchScope.EMPTY_SCOPE
            } else {
                LuaPredefinedScope(project)
            }
        }
        return scope!!
    }

    val isDumb: Boolean
        get() = DumbService.isDumb(project) || currentFile != null
}

class LuaPredefinedScope : AdditionalIndexedRootsScope {
    constructor(project: Project) : this(ProjectAndLibrariesScope(project))
    constructor(base: GlobalSearchScope) : super(base, LuaPredefinedLibraryProvider::class.java)
}