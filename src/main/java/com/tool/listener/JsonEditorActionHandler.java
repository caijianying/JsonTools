package com.tool.listener;

import java.awt.datatransfer.DataFlavor;
import java.util.List;
import java.util.Objects;

import cn.hutool.json.JSONUtil;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.containers.ContainerUtil;
import com.tool.utils.JsonUtils;
import io.github.sharelison.jsontojava.JsonToJava;
import io.github.sharelison.jsontojava.converter.JsonClassResult;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 编辑器对象，处理json转换
 * @author liguang
 * @date 2022/11/3 星期四 1:58 下午
 */
public class JsonEditorActionHandler extends EditorActionHandler {

    @Override
    protected void doExecute(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
        String text = CopyPasteManager.getInstance().getContents(DataFlavor.stringFlavor);
        JsonUtils.executeJsonToJava(editor, dataContext, text);
        // 执行粘贴逻辑，直接在插入符号偏移量位置写入剪切板文本
        Project project = ProjectManager.getInstance().getDefaultProject();
        WriteCommandAction.runWriteCommandAction(project, () -> {
            // 插入文档
            int offset = editor.getCaretModel().getOffset();
            Document document = editor.getDocument();
            document.insertString(offset, text);
            // 格式化
            PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);
            //codeStyleManager.reformatText(psiFile,offset,);
            codeStyleManager.reformatText(psiFile, ContainerUtil.newArrayList(psiFile.getTextRange()));
        });
    }
}
