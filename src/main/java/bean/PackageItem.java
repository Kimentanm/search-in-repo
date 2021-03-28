package bean;

public class PackageItem {

    private String packageName;
    private String description;
    private String author;
    private String lastVersion;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(String lastVersion) {
        this.lastVersion = lastVersion;
    }

    public Object getValueByColumn(String columnName) {
        switch (columnName) {
            case "Package Name":
                return getPackageName();
            case "Author":
                return getAuthor();
            case "Last Version":
                return getLastVersion();
            case "Description":
                return getDescription();
            default:
                return "";
        }
    }
}
