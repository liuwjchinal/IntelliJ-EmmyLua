package com.tang.intellij.lua.psi.stub.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.tang.intellij.lua.lang.LuaLanguage;
import com.tang.intellij.lua.psi.*;
import com.tang.intellij.lua.psi.impl.LuaGlobalFuncDefImpl;
import com.tang.intellij.lua.psi.index.LuaGlobalFuncIndex;
import com.tang.intellij.lua.psi.stub.LuaGlobalFuncStub;
import com.tang.intellij.lua.psi.stub.impl.LuaGlobalFuncStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 *
 * Created by tangzx on 2016/11/26.
 */
public class LuaGlobalFuncDefStubElementType extends IStubElementType<LuaGlobalFuncStub, LuaGlobalFuncDef> {
    public LuaGlobalFuncDefStubElementType() {
        super("GLOBAL_FUNC_DEF", LuaLanguage.INSTANCE);
    }

    @Override
    public LuaGlobalFuncDef createPsi(@NotNull LuaGlobalFuncStub luaGlobalFuncStub) {
        return new LuaGlobalFuncDefImpl(luaGlobalFuncStub, LuaElementType.GLOBAL_FUNC_DEF);
    }

    @Override
    public LuaGlobalFuncStub createStub(@NotNull LuaGlobalFuncDef globalFuncDef, StubElement stubElement) {
        LuaFuncName funcName = globalFuncDef.getFuncName();
        LuaNameRef nameDef = PsiTreeUtil.findChildOfType(funcName, LuaNameRef.class);
        assert nameDef != null;
        return new LuaGlobalFuncStubImpl(nameDef.getText(), stubElement);
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "lua.global_func_def";
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return true;
    }

    @Override
    public void serialize(@NotNull LuaGlobalFuncStub luaGlobalFuncStub, @NotNull StubOutputStream stubOutputStream) throws IOException {
        stubOutputStream.writeUTF(luaGlobalFuncStub.getName());
    }

    @NotNull
    @Override
    public LuaGlobalFuncStub deserialize(@NotNull StubInputStream stubInputStream, StubElement stubElement) throws IOException {
        return new LuaGlobalFuncStubImpl(stubInputStream.readUTF(), stubElement);
    }

    @Override
    public void indexStub(@NotNull LuaGlobalFuncStub luaGlobalFuncStub, @NotNull IndexSink indexSink) {
        indexSink.occurrence(LuaGlobalFuncIndex.KEY, luaGlobalFuncStub.getName());
    }
}