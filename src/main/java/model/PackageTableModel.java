package model;

import bean.PackageItem;
import com.intellij.ui.table.JBTable;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class PackageTableModel extends DefaultTableModel {

    private final String[] columnNames = { "Index", "Package Name", "Author", "Last Version", "Description"};

    private JBTable table;

    private List<PackageItem> data;

    public PackageTableModel(JBTable table) {
        this.table = table;
        table.setAutoResizeMode(JBTable.AUTO_RESIZE_OFF);
        table.setRowHeight(30);
        table.setModel(this);
        setColumnIdentifiers(columnNames);
        // 设置表头样式
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font(null, Font.BOLD, 12));
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(80);
        columnModel.getColumn(1).setPreferredWidth(200);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(300);
        DefaultTableCellRenderer renderer =  new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JTextField.CENTER);
        table.getColumn("Index").setCellRenderer(renderer);
    }

    private Vector<Object> convertData(PackageItem packageItem) {
        // 与columnNames中的元素保持一致
        Vector<Object> v = new Vector<Object>(columnNames.length);
        for (String columnName : columnNames) {
            v.addElement(packageItem.getValueByColumn(columnName));
        }
        return v;
    }

    /**
     * 查找列项中的valueName所在的行
     *
     * @param columnIndex 列号
     * @param value       值
     * @return 如果不存在返回-1
     */
    protected int findRowIndex(int columnIndex, String value) {
        int rowCount = getRowCount();
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            Object valueAt = getValueAt(rowIndex, columnIndex);
            if (StringUtils.equalsIgnoreCase(value, valueAt.toString())) {
                return rowIndex;
            }
        }
        return -1;
    }

    /**
     * 参考源码{@link DefaultTableModel#setValueAt}，此为直接更新行，提高点效率
     *
     * @param rowIndex
     * @param rowData
     */
    protected void updateRow(int rowIndex, Vector<Object> rowData) {
        dataVector.set(rowIndex, rowData);
        // 通知listeners刷新ui
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    /**
     * 按照编码顺序初始化，for 每次刷新都乱序，没办法控制显示顺序
     *
     * @param list
     */
    public void setupTable(List<PackageItem> list) {
        this.data = list;
        for (int i = 0; i < list.size(); i++) {
            updateData(list.get(i), i);
        }
    }

    private void updateData(PackageItem packageItem, int index) {
        Vector<Object> convertData = convertData(packageItem);
        convertData.set(0, index + 1);
        addRow(convertData);
    }

    /**
     * 设置表格条纹（斑马线）
     *
     * @param striped true设置条纹
     * @throws RuntimeException 如果table不是{@link JBTable}类型，请自行实现setStriped
     */
    public void setStriped(boolean striped) {
        if (table instanceof JBTable) {
            ((JBTable) table).setStriped(striped);
        } else {
            throw new RuntimeException("table不是JBTable类型，请自行实现setStriped");
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public List<PackageItem> getData() {
        return data;
    }
}
