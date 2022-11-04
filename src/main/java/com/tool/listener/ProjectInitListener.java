package com.tool.listener;

import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.tool.action.JsonEditorActionHandler;
import org.jetbrains.annotations.NotNull;

/**
 * @author liguang
 * @date 2022/11/3 星期四 6:46 下午
 */
public class ProjectInitListener implements ProjectManagerListener {

    @Override
    public void projectOpened(@NotNull Project project) {
        //LocalFileSystem.getInstance().addVirtualFileListener(new VirtualFileListenerImpl());
        EditorActionManager actionManager = EditorActionManager.getInstance();
        actionManager.setActionHandler(IdeActions.ACTION_EDITOR_PASTE, new JsonEditorActionHandler());
    }
}
