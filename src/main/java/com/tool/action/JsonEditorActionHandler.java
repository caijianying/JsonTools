package com.tool.action;

import java.awt.datatransfer.DataFlavor;
import java.util.List;
import java.util.Objects;

import cn.hutool.json.JSONUtil;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import io.github.sharelison.jsontojava.JsonToJava;
import io.github.sharelison.jsontojava.converter.JsonClassResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author liguang
 * @date 2022/11/3 星期四 1:58 下午
 */
public class JsonEditorActionHandler extends EditorActionHandler {

    @Override
    protected void doExecute(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
        String text = CopyPasteManager.getInstance().getContents(DataFlavor.stringFlavor);
        try {
            // 内容为空不处理
            if (Objects.equals(text, "")) {
                return;
            }
            // 若为json，则转成JAVA文件
            if (JSONUtil.isJson(text)) {
                VirtualFile file = dataContext.getData(CommonDataKeys.VIRTUAL_FILE);
                JsonToJava toJava = new JsonToJava();
                List<JsonClassResult> javaClasses = toJava.jsonToJava(text, getFileName(file), getPackage(file), false);
                for (JsonClassResult item : javaClasses) {
                    try {
                        // 获取当前文件所在文件夹
                        VirtualFile parentFile = file.getParent();
                        // 在当前文件夹下创建类文件
                        VirtualFile javaClassFile = parentFile.createChildData(null, item.getClassName() + ".java");
                        // 设置生成的文件内容
                        javaClassFile.setBinaryContent(item.getClassDeclaration().getBytes());
                    } catch (Exception ignored) {
                    }
                }
            }

        } catch (Exception ex) {
            // json 解析失败，执行粘贴逻辑，直接在插入符号偏移量位置写入剪切板文本
            WriteCommandAction.runWriteCommandAction(ProjectManager.getInstance().getDefaultProject(), () -> {
                editor.getDocument().insertString(editor.getCaretModel().getOffset(), text);
            });
            return;
        }
    }

    private String getFileName(VirtualFile file) {
        return file.getName().replace("." + file.getFileType().getName().toLowerCase(), "");
    }

    private String getPackage(VirtualFile vf) {
        String path = vf.getParent().getPath();
        return path.substring(this.getPackageIndex(path) + 14).replace('/', '.');
    }

    private int getPackageIndex(String path) {
        int matchIndex = path.indexOf("src/main/java");
        if (matchIndex != -1) {
            return matchIndex;
        }
        matchIndex = path.indexOf("src/test/java");
        if (matchIndex != -1) {
            return matchIndex;
        }
        return -1;
    }
}
