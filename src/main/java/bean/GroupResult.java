package bean;

import java.util.List;

public class GroupResult {

    private List<DependenceGroupItem> data;
    private int totalPage;

    public List<DependenceGroupItem> getData() {
        return data;
    }

    public void setData(List<DependenceGroupItem> data) {
        this.data = data;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}
