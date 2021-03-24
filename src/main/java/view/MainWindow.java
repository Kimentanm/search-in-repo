package view;

import bean.ArtifactDetail;
import bean.ArtifactItem;
import bean.DependenceGroupItem;
import bean.GroupResult;
import com.intellij.execution.ui.BaseContentCloseListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.content.ContentManagerListener;
import com.intellij.ui.table.JBTable;
import core.Callback;
import model.ArtifactTableModel;
import model.GroupTableModel;
import org.jetbrains.annotations.NotNull;
import utils.DataUtil;
import utils.NotificationUtils;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;

public class MainWindow implements ToolWindowFactory {

    private JPanel mainPanel;
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

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.project = project;
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(mainPanel, "MAVEN", false);
        toolWindow.getContentManager().addContent(content);
        // 双击获取ArtifactList
        handleDbClickGroupList();
        // 双击获取ArtifactDetail
        handleDbClickArtifactList();
    }

    @Override
    public void init(@NotNull ToolWindow toolWindow) {
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
                    DataUtil.searchArtifactDetail(artifactItem, new Callback<ArtifactDetail>() {
                        @Override
                        public void onSuccess(ArtifactDetail detail) {
                            artifactTable.setPaintBusy(false);
                            artifactLoading = false;
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
                    DataUtil.searchArtifactList(groupItem, new Callback<List<ArtifactItem>>() {
                        @Override
                        public void onSuccess(List<ArtifactItem> list) {
                            artifactTableModel.getDataVector().removeAllElements();
                            artifactTableModel.setupTable(list);
                            artifactTable.setPaintBusy(false);
                            artifactLoading = false;
                        }

                        @Override
                        public void onFailure(String msg) {

                        }

                        @Override
                        public void onError() {

                        }

                        @Override
                        public void onComplete() {

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
            DataUtil.searchGroupList(currentSearchText, currentPageText, sortText, new Callback<GroupResult>() {

                @Override
                public void onSuccess(GroupResult result) {
                    List<DependenceGroupItem> list = result.getData();
                    int totalPageValue = result.getTotalPage();
                    totalPage.setText((int)Math.ceil(totalPageValue / 10) + 1 + "");
                    groupTableModel.getDataVector().removeAllElements();
                    artifactTableModel.getDataVector().removeAllElements();
                    groupTableModel.setupTable(list);
                    groupTableLoading = false;
                    groupTable.setPaintBusy(false);
                }

                @Override
                public void onFailure(String msg) {

                }

                @Override
                public void onError() {

                }

                @Override
                public void onComplete() {

                }
            });
        }
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return true;
    }

    @Override
    public boolean isDoNotActivateOnStart() {
        return true;
    }
}
