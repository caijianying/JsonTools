package com.tool.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.tool.utils.JsonUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author caijy
 * @description 右键选择
 * @date 2022/11/5 星期六 10:41 下午
 */
public class SelectTextAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        String text = editor.getSelectionModel().getSelectedText();
        JsonUtils.executeJsonToJava(editor,event.getDataContext(),text);
    }
}
