package view;

import bean.ArtifactDetail;
import bean.ArtifactItem;
import bean.DependenceGroupItem;
import bean.GroupResult;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.table.JBTable;
import core.Callback;
import model.ArtifactTableModel;
import model.GroupTableModel;
import org.jetbrains.annotations.NotNull;
import utils.MavenDataUtil;
import utils.NotificationUtils;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.MouseEvent;
import java.util.List;

public class MainWindow implements ToolWindowFactory {

    private JPanel mainPanel;
    private NpmWindow npmWindow;
    private MavenWindow mavenWindow;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        npmWindow = new NpmWindow(project);
        mavenWindow = new MavenWindow(project);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content mavenContent = contentFactory.createContent(mavenWindow.getMavenPanel(), "MAVEN", false);
        Content npmContent = contentFactory.createContent(npmWindow.getNpmPanel(), "NPM", false);
        toolWindow.getContentManager().addContent(mavenContent);
        toolWindow.getContentManager().addContent(npmContent);
    }

    @Override
    public void init(@NotNull ToolWindow toolWindow) {}

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return true;
    }
}
