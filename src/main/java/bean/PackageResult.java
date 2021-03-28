package bean;

import java.util.List;

public class PackageResult {

    private List<PackageItem> data;
    private int totalPage;

    public List<PackageItem> getData() {
        return data;
    }

    public void setData(List<PackageItem> data) {
        this.data = data;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}
