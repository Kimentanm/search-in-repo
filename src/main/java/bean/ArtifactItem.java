package bean;

import utils.DataUtil;

public class ArtifactItem {

    private String version;
    private String repository;
    private String usages;
    private String date;
    private String groupId;
    private String artifactId;

    public ArtifactItem() {
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getUsages() {
        return usages;
    }

    public void setUsages(String usages) {
        this.usages = usages;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public Object getValueByColumn(String columnName) {
        switch (columnName) {
            case "Version":
                return getVersion();
            case "Repository":
                return getRepository();
            case "Usages":
                return DataUtil.parseInt(getUsages());
            case "Date":
                return getDate();
            default:
                return "";
        }
    }
}
