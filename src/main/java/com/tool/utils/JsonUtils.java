package com.tool.utils;

import cn.hutool.json.JSONUtil;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import io.github.sharelison.jsontojava.JsonToJava;
import io.github.sharelison.jsontojava.converter.JsonClassResult;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author caijy
 * @description TODO
 * @date 2022/11/5 星期六 10:43 下午
 */
public class JsonUtils {

    public static void executeJsonToJava(@NotNull Editor editor, DataContext dataContext, String text) {
        try {
            // 内容为空不处理
            if (StringUtils.isBlank(text)) {
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
            return;
        } finally {

        }
    }

    private static String getFileName(VirtualFile file) {
        return file.getName().replace("." + file.getFileType().getName().toLowerCase(), "");
    }

    private static String getPackage(VirtualFile vf) {
        String path = vf.getParent().getPath();
        return path.substring(getPackageIndex(path) + 14).replace('/', '.');
    }

    private static int getPackageIndex(String path) {
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
