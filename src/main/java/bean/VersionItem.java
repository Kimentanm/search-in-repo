package bean;

public class VersionItem {

    private String version;
    private Integer downloads;
    private String published;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getDownloads() {
        return downloads;
    }

    public void setDownloads(Integer downloads) {
        this.downloads = downloads;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public Object getValueByColumn(String columnName) {
        switch (columnName) {
            case "Version":
                return getVersion();
            case "Weekly Downloads":
                return getDownloads();
            case "Published":
                return getPublished();
            default:
                return "";
        }
    }
}
