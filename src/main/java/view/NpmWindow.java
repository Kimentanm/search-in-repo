package view;

import bean.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import core.Callback;
import model.PackageTableModel;
import model.VersionTableModel;
import utils.NpmDataUtil;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.MouseEvent;
import java.util.List;

public class NpmWindow {
    private JBTable versionTable;
    private JBTable packageTable;
    private JButton prevButton;
    private JButton nextButton;
    private JLabel currentPage;
    private JLabel totalPage;
    private JBTextField searchText;
    private JComboBox sortSelect;
    private JButton searchButton;
    private JPanel npmPanel;
    private PackageTableModel packageTableModel;
    private VersionTableModel versionTableModel;
    private String currentSearchText;
    private boolean packageTableLoading = false;
    private boolean versionTableLoading = false;

    private Project project;

    private final String[] sortLabel = { "optimal", "popularity", "quality", "maintenance"};

    public NpmWindow(Project project) {
        this.project = project;
        DefaultComboBoxModel model = new DefaultComboBoxModel(sortLabel);
        sortSelect.setModel(model);
        packageTableModel = new PackageTableModel(packageTable);
        versionTableModel = new VersionTableModel(versionTable);
        TableRowSorter<TableModel> versionSorter = new TableRowSorter<TableModel>(versionTableModel);
        versionTable.setRowSorter(versionSorter);
        searchText.addActionListener(e -> handleSearch());
        searchButton.addActionListener(e -> handleSearch());
        // 上一页
        prevButton.addActionListener(e -> {
            if (!packageTableLoading) {
                int currentPageValue = Integer.parseInt(currentPage.getText());
                if (currentPageValue > 1) {
                    currentPage.setText(--currentPageValue + "");
                    searchGroupList();
                }
            }
        });
        // 下一页
        nextButton.addActionListener(e -> {
            if (!packageTableLoading) {
                int totalPageValue = Integer.parseInt(totalPage.getText());
                int currentPageValue = Integer.parseInt(currentPage.getText());
                if (currentPageValue < totalPageValue) {
                    currentPage.setText(++currentPageValue + "");
                    searchGroupList();
                }
            }
        });
        handleDbClickPackageList();
    }

    private void handleDbClickPackageList() {
        (new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(MouseEvent event) {
                if (!versionTableLoading) {
                    versionTable.setPaintBusy(true);
                    versionTableLoading = true;
//                    detailDialog.dispose();
                    int selectedRow = packageTable.getSelectedRow();
                    PackageItem packageItem = packageTableModel.getData().get(selectedRow);
                    NpmDataUtil.searchVersionList(packageItem, new Callback<List<VersionItem>>() {
                        @Override
                        public void onSuccess(List<VersionItem> list) {
                            versionTableModel.getDataVector().removeAllElements();
                            versionTableModel.setupTable(list);
                        }

                        @Override
                        public void onFailure(String msg) {

                        }

                        @Override
                        public void onError() {

                        }

                        @Override
                        public void onComplete() {
                            versionTable.setPaintBusy(false);
                            versionTableLoading = false;
                        }
                    });
                }
                return false;
            }
        }).installOn(packageTable);
    }

    private void handleSearch() {
        currentPage.setText("1");
        totalPage.setText("1");
        currentSearchText = searchText.getText();
        searchGroupList();
    }

    private void searchGroupList() {
        if (!StringUtil.isEmpty(currentSearchText)) {
            String currentPageText = currentPage.getText();
            String pageValue = Integer.parseInt(currentPageText) - 1 + "";
            String sortText = sortLabel[sortSelect.getSelectedIndex()];
            packageTableLoading = true;
            packageTable.setPaintBusy(true);
            NpmDataUtil.searchPackageList(currentSearchText, pageValue, sortText, new Callback<PackageResult>() {

                @Override
                public void onSuccess(PackageResult result) {
                    List<PackageItem> list = result.getData();
                    int totalPageValue = result.getTotalPage();
                    totalPage.setText((int)Math.ceil(totalPageValue / 10) + 1 + "");
                    packageTableModel.getDataVector().removeAllElements();
                    versionTableModel.getDataVector().removeAllElements();
                    packageTableModel.setupTable(list);
                }

                @Override
                public void onFailure(String msg) {

                }

                @Override
                public void onError() {

                }

                @Override
                public void onComplete() {
                    packageTableLoading = false;
                    packageTable.setPaintBusy(false);
                }
            });
        }
    }

    public JPanel getNpmPanel() {
        return npmPanel;
    }
}
