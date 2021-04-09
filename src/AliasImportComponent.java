import com.intellij.openapi.util.Pair;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nls;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;

public class AliasImportComponent {

    private final JPanel myMainPanel;
    private final JBCheckBox pcve_enabled = new JBCheckBox("Enable in python console and variable evaluator");
    private final JBTable myTable = new JBTable();


    public AliasImportComponent() {
        myTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        myTable.setVisibleRowCount(25);
        myTable.setToolTipText("ANY BLANK WILL NOT HAVE EFFECT.");

        JTableHeader tableHeader = myTable.getTableHeader();

        JPanel panel1 = ToolbarDecorator.createDecorator(myTable).setAddAction(new AnActionButtonRunnable() {
            @Override
            public void run(AnActionButton anActionButton) {
                ArrayList<String> list = tableToList(myTable);
                list.add("NewAlias NewFullName");
                setTable(list);
            }
        }).setRemoveAction(new AnActionButtonRunnable() {
            @Override
            public void run(AnActionButton anActionButton) {
                ArrayList<String> list = tableToList(myTable);
                int rowToDelete = myTable.getSelectedRow();
                list.remove(rowToDelete);
                setTable(list);
            }
        }).createPanel();
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(tableHeader, BorderLayout.NORTH);
        panel.add(panel1, BorderLayout.CENTER);
//        JPanel
        myMainPanel = FormBuilder.createFormBuilder()
                .addComponent(pcve_enabled, 1)
                .addComponent(panel1)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return pcve_enabled;
    }


    public boolean getPCVEEnabled() {
        return pcve_enabled.isSelected();
    }

    public void setPCVEEnabled(boolean newStatus) {
        pcve_enabled.setSelected(newStatus);
    }


    public ArrayList<String> getTable() {
        return this.tableToList(this.myTable);
    }

    public void setTable(ArrayList<String> list) {
        myTable.setModel(new TableModel() {
            @Override
            public int getRowCount() {
                return list.size();
            }

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Nls
            @Override
            public String getColumnName(int columnIndex) {
                String[] titles = {"Alias", "FullName"};
                return titles[columnIndex];
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return true;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                String[] pair = list.get(rowIndex).split(" ");
                String key = pair[0];
                String value = pair[1];
                if (columnIndex == 0) {
                    return key;
                } else {
                    return value;
                }
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                if (((String) aValue).trim().equals("") || ((String) aValue).contains(" ")) {
                    return;
                }
                String[] pair = list.get(rowIndex).split(" ");
                String key = pair[0];
                String value = pair[1];
                if (columnIndex == 0) {
                    key = (String) aValue;
                } else {
                    value = (String) aValue;
                }
                list.set(rowIndex, key + " " + value);
            }

            @Override
            public void addTableModelListener(TableModelListener l) {

            }

            @Override
            public void removeTableModelListener(TableModelListener l) {

            }


        });

    }

    private ArrayList<String> tableToList(JBTable jbTable) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < jbTable.getRowCount(); i++) {
            String key = (String) jbTable.getValueAt(i, 0);
            String value = (String) jbTable.getValueAt(i, 1);
            list.add(key + " " + value);
        }
        return list;
    }
}
