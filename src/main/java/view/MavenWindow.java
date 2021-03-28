package view;

import bean.ArtifactDetail;
import bean.ArtifactItem;
import bean.DependenceGroupItem;
import bean.GroupResult;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import core.Callback;
import model.ArtifactTableModel;
import model.GroupTableModel;
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
    private JBTable artifactTable;
    private JButton prevButton;
    private JButton nextButton;
    private JLabel currentPage;
    private JLabel totalPage;
    private JComboBox sortSelect;
    private Project project;
    private GroupTableModel groupTableModel;
    private ArtifactTableModel artifactTableModel;
    private DetailDialog detailDialog;

    private final String[] sortLabel = { "relevance", "popular", "newest"};

    private String currentSearchText;

    /**
     * 防止多次点击按钮
     */
    private boolean groupTableLoading = false;
    private boolean artifactLoading = false;

    public MavenWindow(Project project) {
        this.project = project;
        DefaultComboBoxModel model = new DefaultComboBoxModel(sortLabel);
        sortSelect.setModel(model);
        detailDialog = new DetailDialog();
        groupTableModel = new GroupTableModel(groupTable);
        artifactTableModel = new ArtifactTableModel(artifactTable);
        TableRowSorter<TableModel> sorter1 = new TableRowSorter<TableModel>(groupTableModel);
        TableRowSorter<TableModel> sorter2 = new TableRowSorter<TableModel>(artifactTableModel);
        groupTable.setRowSorter(sorter1);
        artifactTable.setRowSorter(sorter2);
        searchText.addActionListener(e -> handleSearch());
        searchButton.addActionListener(e -> handleSearch());
        // 上一页
        prevButton.addActionListener(e -> {
            if (!groupTableLoading) {
                int currentPageValue = Integer.parseInt(currentPage.getText());
                if (currentPageValue > 1) {
                    currentPage.setText(--currentPageValue + "");
                    searchGroupList(groupTableModel, artifactTableModel);
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
                    searchGroupList(groupTableModel, artifactTableModel);
                }
            }
        });
        // 双击获取ArtifactList
        handleDbClickGroupList();
        // 双击获取ArtifactDetail
        handleDbClickArtifactList();
    }

    private void handleDbClickArtifactList() {
        (new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(MouseEvent event) {
                if (!artifactLoading) {
                    artifactTable.setPaintBusy(true);
                    artifactLoading = true;
                    detailDialog.dispose();
                    int selectedRow = artifactTable.getSelectedRow();
                    ArtifactItem artifactItem = artifactTableModel.getData().get(selectedRow);
                    MavenDataUtil.searchArtifactDetail(artifactItem, new Callback<ArtifactDetail>() {
                        @Override
                        public void onSuccess(ArtifactDetail detail) {
                            detailDialog.setData(detail);
                            detailDialog.getCopyMaven().addActionListener(e -> NotificationUtils.infoNotify("Copy Maven dependence success", project));
                            detailDialog.getCopyGradle().addActionListener(e -> NotificationUtils.infoNotify("Copy Gradle dependence success", project));
                            detailDialog.showDialog(650, 550, true, false);
                        }

                        @Override
                        public void onFailure(String msg) {

                        }

                        @Override
                        public void onError() {

                        }

                        @Override
                        public void onComplete() {
                            artifactTable.setPaintBusy(false);
                            artifactLoading = false;
                        }
                    });
                }
                return false;
            }
        }).installOn(artifactTable);
    }

    private void handleDbClickGroupList() {
        (new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(MouseEvent event) {
                if (!artifactLoading) {
                    artifactTable.setPaintBusy(true);
                    artifactLoading = true;
                    int selectedRow = groupTable.getSelectedRow();
                    DependenceGroupItem groupItem = groupTableModel.getData().get(selectedRow);
                    MavenDataUtil.searchArtifactList(groupItem, new Callback<List<ArtifactItem>>() {
                        @Override
                        public void onSuccess(List<ArtifactItem> list) {
                            artifactTableModel.getDataVector().removeAllElements();
                            artifactTableModel.setupTable(list);
                        }

                        @Override
                        public void onFailure(String msg) {

                        }

                        @Override
                        public void onError() {

                        }

                        @Override
                        public void onComplete() {
                            artifactTable.setPaintBusy(false);
                            artifactLoading = false;
                        }
                    });
                }
                return false;
            }
        }).installOn(groupTable);
    }

    private void handleSearch() {
        currentPage.setText("1");
        totalPage.setText("1");
        currentSearchText = searchText.getText();
        searchGroupList(groupTableModel, artifactTableModel);
    }

    private void searchGroupList(GroupTableModel groupTableModel, ArtifactTableModel artifactTableModel) {
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
                    totalPage.setText((int)Math.ceil(totalPageValue / 10) + 1 + "");
                    groupTableModel.getDataVector().removeAllElements();
                    artifactTableModel.getDataVector().removeAllElements();
                    groupTableModel.setupTable(list);
                }

                @Override
                public void onFailure(String msg) {

                }

                @Override
                public void onError() {

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
