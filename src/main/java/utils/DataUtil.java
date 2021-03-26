package utils;

import bean.ArtifactDetail;
import bean.ArtifactItem;
import bean.DependenceGroupItem;
import bean.GroupResult;
import com.intellij.openapi.application.ApplicationManager;
import core.Callback;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataUtil {

    private final static String BASE_URL = "https://mvn-forward.vercel.app/mvnrepository";

    public static void searchGroupList(String value, String currentPage, String sortText, Callback<GroupResult> callback) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                String result = OkHttpUtil.sendGet(BASE_URL + "/search?q=" + value + "&p=" + currentPage + "&sort=" + sortText);
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
            } catch (Exception e) {
                e.printStackTrace();
                callback.onError();
            } finally {
                callback.onComplete();
            }
        });
    }

    public static void searchArtifactList(DependenceGroupItem groupItem, Callback<List<ArtifactItem>> callback) {
        String artifactId = groupItem.getArtifactId();
        String groupId = groupItem.getGroupLabel();
        new Thread(() -> {
            try {
                String result = OkHttpUtil.sendGet(BASE_URL + "/artifact/" + groupId + "/" + artifactId);
                Document document = Jsoup.parse(result);
                Element versionTable = document.getElementsByClass("grid versions").get(0);
                Elements tbodyList = versionTable.getElementsByTag("tbody");
                List<ArtifactItem> list = new ArrayList<>();
                tbodyList.forEach(tbody -> {
                    Elements trList = tbody.children();
                    trList.forEach(tr -> {
                        Elements tdList = tr.children();
                        int start = 0;
                        if (tdList.size() == 5) {
                            start = 1;
                        }
                        ArtifactItem artifactItem = new ArtifactItem();
                        artifactItem.setVersion(tdList.get(start).text());
                        artifactItem.setRepository(tdList.get(++start).text());
                        artifactItem.setUsages(tdList.get(++start).text());
                        artifactItem.setDate(tdList.get(++start).text());
                        artifactItem.setGroupId(groupId);
                        artifactItem.setArtifactId(artifactId);
                        list.add(artifactItem);
                    });
                });
                callback.onSuccess(list);
            } catch (Exception e1) {
                e1.printStackTrace();
                callback.onError();
            } finally {
                callback.onComplete();
            }
        }).start();
    }

    public static void searchArtifactDetail(ArtifactItem artifactItem, Callback<ArtifactDetail> callback) {
        new Thread(() -> {
            try {
                String result = OkHttpUtil.sendGet("https://mf-edu.kimen.com.cn/transmit/mvnrepository/artifact/" + artifactItem.getGroupId() + "/" + artifactItem.getArtifactId() + "/" + artifactItem.getVersion());
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
            } catch (Exception e1) {
                e1.printStackTrace();
                callback.onError();
            } finally {
                callback.onComplete();
            }
        }).start();
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
