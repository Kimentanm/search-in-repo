package view;

import bean.ArtifactDetail;
import bean.ArtifactItem;
import bean.DependenceGroupItem;
import com.intellij.openapi.project.Project;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.table.JBTable;
import core.Callback;
import model.ArtifactTableModel;
import org.apache.commons.collections.CollectionUtils;
import utils.MavenDataUtil;
import utils.NotificationUtils;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

public class ArtifactTable extends JPanel {

    private JBTable artifactTable;
    private ArtifactTableModel artifactTableModel;
    private DependenceGroupItem groupItem;

    private DetailDialog detailDialog;

    private Project project;

    private boolean artifactLoading = false;

    private String repositoryPath;

    public ArtifactTable(DependenceGroupItem groupItem, Project project, DetailDialog detailDialog, String repositoryPath) {
        super(false);
        artifactTable = new JBTable();
        this.groupItem = groupItem;
        this.repositoryPath = repositoryPath;
        this.project = project;
        artifactTableModel = new ArtifactTableModel(artifactTable);
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(artifactTableModel);
        artifactTable.setRowSorter(sorter);
        artifactTable.setFillsViewportHeight(true);
        // 解决table无法充满scrollPane
        artifactTable.setPreferredScrollableViewportSize(new Dimension(400, 1000));

        setLayout(new GridLayout(1,1));
        JScrollPane scrollPane = new JScrollPane(artifactTable);
        add(scrollPane);
        this.detailDialog = detailDialog;
        // 双击获取ArtifactDetail
        handleDbClickArtifactList();
    }

    public DependenceGroupItem getGroupItem() {
        return groupItem;
    }

    public void setPaintBusy(boolean busy) {
        artifactTable.setPaintBusy(busy);
    }

    public void setArtifactLoading(boolean loading) {
        artifactLoading = loading;
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
                            NotificationUtils.errorNotify(msg, project);
                        }

                        @Override
                        public void onError(String msg) {
                            NotificationUtils.errorNotify(msg, project);
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

    public void removeAllElements() {
        artifactTableModel.getDataVector().removeAllElements();
    }

    public void setupTable(List<ArtifactItem> list) {
        artifactTableModel.setupTable(list);
    }

    public String getRepositoryPath() {
        return repositoryPath;
    }

    public boolean hasData() {
        return !CollectionUtils.isEmpty(artifactTableModel.getData());
    }
}
