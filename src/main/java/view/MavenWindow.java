package view;

import bean.ArtifactItem;
import bean.DependenceGroupItem;
import bean.GroupResult;
import bean.RepositoryItem;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import core.Callback;
import model.GroupTableModel;
import org.apache.commons.collections.CollectionUtils;
import utils.MavenDataUtil;
import utils.NotificationUtils;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.MouseEvent;
import java.util.List;

public class MavenWindow {
    private JPanel mavenPanel;
    private JBTextField searchText;
    private JButton searchButton;
    private JBTable groupTable;
    private JButton prevButton;
    private JButton nextButton;
    private JLabel currentPage;
    private JLabel totalPage;
    private JComboBox sortSelect;
    private JTabbedPane repositoryTab;
    private Project project;
    private GroupTableModel groupTableModel;
//    private DetailDialog detailDialog;

    private final String[] sortLabel = {"relevance", "popular", "newest"};

    private String currentSearchText;

    /**
     * 防止多次点击按钮
     */
    private boolean groupTableLoading = false;

    private boolean searchRepositoryLoading = false;

    public MavenWindow(Project project) {
        this.project = project;
//        detailDialog = new DetailDialog();
        DefaultComboBoxModel model = new DefaultComboBoxModel(sortLabel);
        sortSelect.setModel(model);
        groupTableModel = new GroupTableModel(groupTable);
        TableRowSorter<TableModel> sorter1 = new TableRowSorter<>(groupTableModel);
        groupTable.setRowSorter(sorter1);
        searchText.addActionListener(e -> handleSearch());
        searchButton.addActionListener(e -> handleSearch());
        // 上一页
        prevButton.addActionListener(e -> {
            if (!groupTableLoading) {
                int currentPageValue = Integer.parseInt(currentPage.getText());
                if (currentPageValue > 1) {
                    currentPage.setText(--currentPageValue + "");
                    searchGroupList(groupTableModel);
                }
            }
        });
        // 下一页
        nextButton.addActionListener(e -> {
            if (!groupTableLoading) {
                int totalPageValue = Integer.parseInt(totalPage.getText());
                int currentPageValue = Integer.parseInt(currentPage.getText());
                if (currentPageValue < totalPageValue) {
                    currentPage.setText(++currentPageValue + "");
                    searchGroupList(groupTableModel);
                }
            }
        });
        // 设置Tab为滚动模式
        repositoryTab.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        repositoryTab.addChangeListener(e -> {
            int n = repositoryTab.getSelectedIndex();
            n = n == -1 ? 0 : n;
            ArtifactTable artifactTable = (ArtifactTable) repositoryTab.getComponentAt(n);
            if (!artifactTable.hasData()) {
                searchArtifactList(artifactTable);
            }
        });
        // 双击获取RepositoryList
        handleDbClickGroupList();
    }

    private void handleDbClickGroupList() {
        (new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(MouseEvent event) {
                if (!searchRepositoryLoading) {
                    searchRepositoryLoading = true;
                    repositoryTab.removeAll();
//                    repositoryTab.setSelectedIndex(0);
                    int selectedRow = groupTable.getSelectedRow();
                    DependenceGroupItem groupItem = groupTableModel.getData().get(selectedRow);
                    MavenDataUtil.searchRepositoryList(groupItem, new Callback<List<RepositoryItem>>() {
                        @Override
                        public void onSuccess(List<RepositoryItem> data) {
//                            if (!CollectionUtils.isEmpty(data)) {
//                                data.forEach(repository -> {
//                                    ArtifactTable artifactTable = new ArtifactTable(groupItem, project, detailDialog, repository.getPath());
//                                    repositoryTab.addTab(repository.getTitle(), artifactTable);
//                                });
//                                repositoryTab.setSelectedIndex(0);
//                            }
                        }

                        @Override
                        public void onFailure(String msg) {

                        }

                        @Override
                        public void onError(String msg) {
                            NotificationUtils.errorNotify(msg, project);
                        }

                        @Override
                        public void onComplete() {
                            searchRepositoryLoading = false;
                        }
                    });
                }
                return false;
            }
        }).installOn(groupTable);
    }

    private void searchArtifactList(ArtifactTable artifactTable) {
        artifactTable.setPaintBusy(true);
        artifactTable.setArtifactLoading(true);
        MavenDataUtil.searchArtifactList(artifactTable, new Callback<List<ArtifactItem>>() {
            @Override
            public void onSuccess(List<ArtifactItem> list) {
                artifactTable.removeAllElements();
                artifactTable.setupTable(list);
            }

            @Override
            public void onFailure(String msg) {

            }

            @Override
            public void onError(String msg) {
                NotificationUtils.errorNotify(msg, project);
            }

            @Override
            public void onComplete() {
                artifactTable.setPaintBusy(false);
                artifactTable.setArtifactLoading(false);
            }
        });
    }

    private void handleSearch() {
        if (!groupTableLoading) {
            currentPage.setText("1");
            totalPage.setText("1");
            currentSearchText = searchText.getText();
            searchGroupList(groupTableModel);
        }
    }

    private void searchGroupList(GroupTableModel groupTableModel) {
        if (!StringUtil.isEmpty(currentSearchText)) {
            String currentPageText = currentPage.getText();
            String sortText = sortLabel[sortSelect.getSelectedIndex()];
            groupTableLoading = true;
            groupTable.setPaintBusy(true);
            MavenDataUtil.searchGroupList(currentSearchText, currentPageText, sortText, new Callback<GroupResult>() {

                @Override
                public void onSuccess(GroupResult result) {
                    List<DependenceGroupItem> list = result.getData();
                    int totalPageValue = result.getTotalPage();
                    totalPage.setText((int) Math.ceil(totalPageValue / 10) + 1 + "");
                    groupTableModel.getDataVector().removeAllElements();
                    groupTableModel.setupTable(list);
                }

                @Override
                public void onFailure(String msg) {

                }

                @Override
                public void onError(String msg) {
                    NotificationUtils.errorNotify(msg, project);
                }

                @Override
                public void onComplete() {
                    groupTableLoading = false;
                    groupTable.setPaintBusy(false);
                }
            });
        }
    }

    public JPanel getMavenPanel() {
        return mavenPanel;
    }
}
