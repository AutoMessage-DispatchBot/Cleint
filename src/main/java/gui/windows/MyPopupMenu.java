package gui.windows;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MyPopupMenu<T> extends JPopupMenu {
    private Object[][] objects;

    private final CustomTable customTable;
    private final JTable table;
    private final JCheckBox selectAll;

    public MyPopupMenu (T[] data) {
        selectAll = new JCheckBox("Выбрать все");
        selectAll.setSelected(true);
        this.add(selectAll);

        objects = new Object[data.length][2];
        for(int i = 0; i < data.length; i++) {
            objects[i][0] = true;
            objects[i][1] = data[i];
        }

        this.customTable = new CustomTable(objects);
        this.table = new JTable(customTable);

        table.getColumnModel().getColumn(0).setCellEditor(new ToggleButtonEditor(table));
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        this.add(table);

        selectAll.addActionListener(e -> {
            for(int i = 0; i < customTable.getRowCount(); i++) {
                if(selectAll.isSelected())
                    customTable.table[i][0] = true;
                else
                    customTable.table[i][0] = false;
            }

            customTable.fireTableDataChanged();
            table.repaint();
        });
    }

    public synchronized void updateData(T[] data) {
        objects = new Object[data.length][2];
        for(int i = 0; i < data.length; i++) {
            objects[i][0] = true;
            objects[i][1] = data[i];
        }

        customTable.table = objects;
        customTable.fireTableDataChanged();
        table.repaint();
    }

    public synchronized T[] getSelectedData(Class<T> type) {
        List<T> list = new ArrayList<>();
        for (Object[] obj : objects) {
            if ((boolean) obj[0]) {
                list.add(type.cast(obj[1]));
            }
        }

        return list.toArray((T[]) java.lang.reflect.Array.newInstance(type, list.size()));
    }






    private static class CustomTable extends AbstractTableModel {
        Object[][] table;

        public CustomTable(Object[][] table) {
            this.table = table;
        }

        @Override
        public int getRowCount() {
            return table.length;
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return table[rowIndex][columnIndex];
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            table[rowIndex][columnIndex] = aValue;
            fireTableCellUpdated(rowIndex, columnIndex); // Обновление отображения ячейки
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0)
                return Boolean.class;
            else
                return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0;
        }
    }

    private static class ToggleButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private final JToggleButton button;

        public ToggleButtonEditor(JTable table) {
            button = new JToggleButton();

            button.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                boolean isSelected = button.isSelected();

                table.setValueAt(isSelected, selectedRow, 0); // Обновление данных в модели таблицы
                stopCellEditing(); // Завершение редактирования ячейки
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setSelected((Boolean) value);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button.isSelected();
        }
    }
}
