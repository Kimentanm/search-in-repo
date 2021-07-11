package utils;

import bean.PackageItem;
import bean.PackageResult;
import bean.VersionItem;
import com.intellij.openapi.application.ApplicationManager;
import core.Callback;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class NpmDataUtil {

    private final static String BASE_URL = "https://www.npmjs.com";

    private final static String ERROR_MSG = "Some errors occurred in the search, please submit your search content to GitHub Issue and we will fix it soon.";

    public static void searchPackageList(String value, String currentPage, String sortText, Callback<PackageResult> callback) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                String result = OkHttpUtil.sendGet(BASE_URL + "/search?q=" + value + "&page=" + currentPage + "&ranking=" + sortText);
                Document document = Jsoup.parse(result);
                Elements packageElements = document.getElementsByTag("section");
                List<PackageItem> list = new ArrayList<>();
                packageElements.forEach(item -> {
                    Element packageElement = item.child(0);
                    String packageName = packageElement.child(0).child(0).text();
                    String description = packageElement.child(2).text();
                    String author = packageElement.child(packageElement.childNodeSize() - 1).child(0).text();
                    String lastVersion = packageElement.child(packageElement.childNodeSize() - 1).child(1).text().split(" ")[1];
                    PackageItem packageItem = new PackageItem();
                    packageItem.setPackageName(packageName);
                    packageItem.setDescription(description);
                    packageItem.setAuthor(author);
                    packageItem.setLastVersion(lastVersion);
                    list.add(packageItem);
                });
                String totalPage = document.getElementById("main").child(0).getElementsByTag("h2").get(0).text().split(" ")[0];
                PackageResult packageResult = new PackageResult();
                packageResult.setTotalPage(Integer.parseInt(totalPage));
                packageResult.setData(list);
                callback.onSuccess(packageResult);
            } catch (Exception e) {
                e.printStackTrace();
                callback.onError(ERROR_MSG);
            } finally {
                callback.onComplete();
            }
        });

    }

    public static void searchVersionList(PackageItem packageItem, Callback<List<VersionItem>> callback) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                String result = OkHttpUtil.sendGet(BASE_URL + "/package/" + packageItem.getPackageName() + "?activeTab=versions");
                Document document = Jsoup.parse(result);
                Element child = document.getElementById("tabpanel-versions").child(0);
                Element versionElement = child.child(child.childNodeSize() - 1);
                Elements versionList = versionElement.children();
                List<VersionItem> list = new ArrayList<>();
                for (int i = 1; i < versionList.size(); i++) {
                    Element versionItem = versionList.get(i);
                    Elements a = versionItem.getElementsByTag("a");
                    if (a.size() > 0) {
                        String version = versionItem.getElementsByTag("a").get(0).text();
                        String downloads = versionItem.getElementsByTag("code").get(0).text();
                        downloads = downloads.replace(",", "");
                        String published = versionItem.getElementsByTag("ul").get(0).text();
                        VersionItem item = new VersionItem();
                        item.setVersion(version);
                        item.setDownloads(Integer.parseInt(downloads));
                        item.setPublished(published);
                        item.setPackageName(packageItem.getPackageName());
                        list.add(item);
                    }
                }
                callback.onSuccess(list);
            } catch (Exception e) {
                e.printStackTrace();
                callback.onError(ERROR_MSG);
            } finally {
                callback.onComplete();
            }
        });
    }
}
