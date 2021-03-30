package view;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.ex.ApplicationInfoEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MainWindow implements ToolWindowFactory {

    private JPanel mainPanel;
    private NpmWindow npmWindow;
    private MavenWindow mavenWindow;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        String fullApplicationName = ApplicationInfoEx.getInstanceEx().getFullApplicationName();
        npmWindow = new NpmWindow(project);
        mavenWindow = new MavenWindow(project);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content mavenContent = contentFactory.createContent(mavenWindow.getMavenPanel(), "MAVEN", false);
        Content npmContent = contentFactory.createContent(npmWindow.getNpmPanel(), "NPM", false);
        if (fullApplicationName.startsWith("WebStorm")) {
            toolWindow.getContentManager().addContent(npmContent);
            toolWindow.getContentManager().addContent(mavenContent);
        } else {
            toolWindow.getContentManager().addContent(mavenContent);
            toolWindow.getContentManager().addContent(npmContent);
        }
    }

    @Override
    public void init(@NotNull ToolWindow toolWindow) {}

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return true;
    }
}
