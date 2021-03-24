package bean;

import org.apache.commons.lang3.StringUtils;
import utils.DataUtil;

import javax.swing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DependenceGroupItem {

    private ImageIcon imageIcon;
    private String artifactLabel;
    private String artifactId;
    private String groupLabel;
    private String usagesLabel;

    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
    }

    public String getArtifactLabel() {
        return artifactLabel;
    }

    public void setArtifactLabel(String artifactLabel) {
        this.artifactLabel = artifactLabel;
    }

    public String getGroupLabel() {
        return groupLabel;
    }

    public void setGroupLabel(String groupLabel) {
        this.groupLabel = groupLabel;
    }

    public String getUsagesLabel() {
        return usagesLabel;
    }

    public void setUsagesLabel(String usagesLabel) {
        this.usagesLabel = usagesLabel;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public Object getValueByColumn(String columnName) {
        switch (columnName) {
            case "Group ID":
                return getGroupLabel();
            case "Artifact ID":
                return getArtifactLabel();
            case "Usages":
                return DataUtil.parseInt(getUsagesLabel());
            default:
                return "";
        }
    }
}
