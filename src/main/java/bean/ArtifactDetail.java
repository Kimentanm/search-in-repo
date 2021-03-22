package bean;

public class ArtifactDetail {

    private String artifactId;
    private String version;
    private String license;
    private String category;
    private String organization;
    private String homePage;
    private String date;
    private String repository;
    private String mavenContent;
    private String gradleContent;

    public String getArtifactId() {
        return artifactId == null ? "" : artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version == null ? "" : version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLicense() {
        return license == null ? "" : license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getCategory() {
        return category == null ? "" : category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOrganization() {
        return organization == null ? "" : organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getHomePage() {
        return homePage == null ? "" : homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getDate() {
        return date == null ? "" : date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRepository() {
        return repository == null ? "" : repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getMavenContent() {
        return mavenContent;
    }

    public void setMavenContent(String mavenContent) {
        this.mavenContent = mavenContent;
    }

    public String getGradleContent() {
        return gradleContent;
    }

    public void setGradleContent(String gradleContent) {
        this.gradleContent = gradleContent;
    }
}
