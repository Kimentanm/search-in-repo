package utils;

import bean.*;
import com.intellij.openapi.application.ApplicationManager;
import core.Callback;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import view.ArtifactTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MavenDataUtil {

    private final static String BASE_URL = "https://mf-edu.kimen.com.cn/transmit/mvnrepository";

    private final static String ERROR_MSG = "Some errors occurred in the search, please submit your search content to GitHub Issue and we will fix it soon.";

    private final static String FAILURE_MSG = "The server is down. Please try again in 10 minutes";

    public static void searchRepositoryList(DependenceGroupItem groupItem, Callback<List<RepositoryItem>> callback) {
        String artifactId = groupItem.getArtifactId();
        String groupId = groupItem.getGroupLabel();
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                String result = OkHttpUtil.sendGet(BASE_URL + "/artifact/" + groupId + "/" + artifactId);
                if (!result.contains("Cloudflare")) {
                    Document document = Jsoup.parse(result);
                    Element snippets = document.getElementById("snippets");
                    Element repositoryTabs = snippets.getElementsByClass("tabs").get(0);
                    Elements elements = repositoryTabs.getElementsByTag("a");
                    List<RepositoryItem> repositoryList = new ArrayList<>();
                    elements.forEach(element -> {
                        RepositoryItem item = new RepositoryItem();
                        item.setPath(element.attr("href"));
                        item.setTitle(element.text());
                        repositoryList.add(item);
                    });
                    callback.onSuccess(repositoryList);
                } else {
                    callback.onFailure(FAILURE_MSG);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                callback.onError(ERROR_MSG);
            } finally {
                callback.onComplete();
            }
        });
    }

    public static void searchGroupList(String value, String currentPage, String sortText, Callback<GroupResult> callback) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                String result = OkHttpUtil.sendGet(BASE_URL + "/search?q=" + value + "&p=" + currentPage + "&sort=" + sortText);
                // 如果返回了"On more step"认证信息，则提示用户过一会再试
                if (!result.contains("Cloudflare")) {
                    Document document = Jsoup.parse(result);
                    // 获取分页信息
                    String totalPage = document.getElementById("maincontent").child(0).child(0).text();
                    // 获取GroupList信息
                    Elements dependenceDom = document.getElementsByClass("im-header");
                    List<DependenceGroupItem> list = new ArrayList<>();
                    dependenceDom.forEach(item -> {
                        Element titleDom = item.getElementsByClass("im-title").first();
                        Element subTitleDom = item.getElementsByClass("im-subtitle").first();
                        Element artifactTitle = titleDom.getElementsByTag("a").first();
                        Element usage = titleDom.getElementsByClass("im-usage").first();
                        Elements subTitleArray = subTitleDom.getElementsByTag("a");
                        Element groupId = subTitleArray.first();
                        Element artifactId = subTitleArray.get(1);
                        DependenceGroupItem groupItem = new DependenceGroupItem();
                        groupItem.setArtifactLabel(artifactTitle.text());
                        groupItem.setGroupLabel(groupId.text());
                        groupItem.setUsagesLabel(usage == null ? "0" : usage.text().split(" ")[0]);
                        groupItem.setArtifactId(artifactId.text());
                        list.add(groupItem);
                    });
                    GroupResult data = new GroupResult();
                    data.setData(list);
                    data.setTotalPage(Integer.parseInt(totalPage));
                    callback.onSuccess(data);
                } else {
                    callback.onFailure(FAILURE_MSG);
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onError(ERROR_MSG);
            } finally {
                callback.onComplete();
            }
        });
    }

    public static void searchArtifactList(ArtifactTable artifactTable, Callback<List<ArtifactItem>> callback) {
        DependenceGroupItem groupItem = artifactTable.getGroupItem();
        String repositoryPath = artifactTable.getRepositoryPath();
        String artifactId = groupItem.getArtifactId();
        String groupId = groupItem.getGroupLabel();
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                String result = OkHttpUtil.sendGet(BASE_URL + repositoryPath);
                if (!result.contains("Cloudflare")) {
                    Document document = Jsoup.parse(result);
                    Element versionTable = document.getElementsByClass("grid versions").get(0);
                    Elements tbodyList = versionTable.getElementsByTag("tbody");
                    Elements thList = versionTable.getElementsByTag("th");
                    List<ArtifactItem> list = new ArrayList<>();
                    tbodyList.forEach(tbody -> {
                        Elements trList = tbody.children();
                        trList.forEach(tr -> {
                            Elements tdList = tr.children();
                            int offset = 0;
                            if (tdList.size() < thList.size()) {
                                offset = -1;
                            }
                            ArtifactItem artifactItem = new ArtifactItem();
                            String versionText = tdList.get(getIndexFromThList(thList, "Version") + offset).text();
                            String repositoryText = tdList.get(getIndexFromThList(thList, "Repository") + offset).text();
                            String usagesText = tdList.get(getIndexFromThList(thList, "Usages") + offset).text();
                            String offsetText = tdList.get(getIndexFromThList(thList, "Date") + offset).text();
                            artifactItem.setVersion(versionText);
                            artifactItem.setRepository(repositoryText);
                            artifactItem.setUsages(usagesText);
                            artifactItem.setDate(offsetText);
                            artifactItem.setGroupId(groupId);
                            artifactItem.setArtifactId(artifactId);
                            list.add(artifactItem);
                        });
                    });
                    callback.onSuccess(list);
                } else {
                    callback.onFailure(FAILURE_MSG);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                callback.onError(ERROR_MSG);
            } finally {
                callback.onComplete();
            }
        });
    }

    private static int getIndexFromThList(Elements thList, String thName) {
        for (int i = 0; i < thList.size(); i++) {
            if (thName.equals(thList.get(i).text())) {
                return i;
            }
        }
        return 0;
    }

    public static void searchArtifactDetail(ArtifactItem artifactItem, Callback<ArtifactDetail> callback) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                String result = OkHttpUtil.sendGet(BASE_URL + "/artifact/" + artifactItem.getGroupId() + "/" + artifactItem.getArtifactId() + "/" + artifactItem.getVersion());
                if (!result.contains("Cloudflare")) {
                    Document document = Jsoup.parse(result);
                    Element table = document.getElementsByTag("table").get(0);
                    Elements trList = table.getElementsByTag("tr");
                    Element maven = document.getElementById("maven-a");
                    Element gradle = document.getElementById("gradle-a");
                    ArtifactDetail detail = new ArtifactDetail();
                    detail.setArtifactId(artifactItem.getArtifactId());
                    detail.setVersion(artifactItem.getVersion());
                    detail.setMavenContent(toValue(maven.text()));
                    detail.setGradleContent(toValue(gradle.text()));
                    trList.forEach(tr -> {
                        String key = tr.child(0).text();
                        String value = tr.child(1).text();
                        if ("License".equals(key)) {
                            detail.setLicense(value);
                        } else if ("Categories".equals(key)) {
                            detail.setCategory(value);
                        } else if ("Organization".equals(key)) {
                            detail.setOrganization(value);
                        } else if ("HomePage".equals(key)) {
                            detail.setHomePage(value);
                        } else if ("Date".equals(key)) {
                            detail.setDate(value);
                        } else if ("Repositories".equals(key)) {
                            detail.setRepository(value);
                        }
                    });
                    callback.onSuccess(detail);
                } else {
                    callback.onFailure(FAILURE_MSG);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                callback.onError(ERROR_MSG);
            } finally {
                callback.onComplete();
            }
        });
    }

    private static String toValue(String text) {
        List<String> split = Arrays.asList(text.split("\n"));
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < split.size(); i++) {
            sb.append(split.get(i));
            if (i != split.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public static Integer parseInt(String content) {
        Matcher matcher = null;
        if (StringUtils.isBlank(content)) {
            return 0;
        }
        matcher = Pattern.compile("[0-9.]+").matcher(content.replaceAll(",", ""));
        while (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return 0;
    }
}
