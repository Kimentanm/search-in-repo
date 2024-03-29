package view;

import bean.ArtifactDetail;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.Alarm;
import com.intellij.util.AlarmFactory;
import utils.ClipboardUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class DetailDialog extends JDialog {
    private JPanel contentPane;
    private JBLabel artifactId;
    private JTextArea mavenContent;
    private JTextArea gradleContent;
    private JBLabel versionContent;
    private JBLabel licenseContent;
    private JBLabel categoryContent;
    private JBLabel organizationContent;
    private JBLabel homePageContent;
    private JBLabel dateContent;
    private JBLabel repositoryContent;
    private JButton copyMaven;
    private JButton copyGradle;

    public DetailDialog() {
//        setContentPane(contentPane);
//        setModal(true);
//        setTitle("Artifact Detail");
    }

    public void setData(ArtifactDetail detail) {
        artifactId.setCopyable(true);
        artifactId.setText(detail.getArtifactId());
        versionContent.setText(detail.getVersion());
        versionContent.setCopyable(true);
        licenseContent.setText(detail.getLicense());
        licenseContent.setCopyable(true);
        categoryContent.setText(detail.getCategory());
        categoryContent.setCopyable(true);
        organizationContent.setText(detail.getOrganization());
        organizationContent.setCopyable(true);
        homePageContent.setText(detail.getHomePage());
        homePageContent.setCopyable(true);
        dateContent.setText(detail.getDate());
        dateContent.setCopyable(true);
        repositoryContent.setText(detail.getRepository());
        repositoryContent.setCopyable(true);
        mavenContent.setText(detail.getMavenContent());
        gradleContent.setText(detail.getGradleContent());
        copyMaven.addActionListener(e -> ClipboardUtil.setClipboardString(mavenContent.getText()));
        copyGradle.addActionListener(e -> ClipboardUtil.setClipboardString(gradleContent.getText()));
    }

    public JButton getCopyMaven() {
        return copyMaven;
    }

    public void setCopyMaven(JButton copyMaven) {
        this.copyMaven = copyMaven;
    }

    public JButton getCopyGradle() {
        return copyGradle;
    }

    public void setCopyGradle(JButton copyGradle) {
        this.copyGradle = copyGradle;
    }

    public void showDialog(int width, int height, boolean isInCenter, boolean isResizeable) {
        pack();
        setResizable(isResizeable);
        setSize(width, height);
        if (isInCenter) {
            setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - width / 2, Toolkit.getDefaultToolkit().getScreenSize().height / 2 - height / 2);
        }
        setVisible(true);
    }
}
