package gui.windows;

import database.Database;
import gate.Output;
import data.Buyer;
import data.Correspondence;
import senderData.MessageToServer;
import senderData.MessageTypeToServer;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class SeeBuyersForMessageClass extends JPanel {
    public static int QUEUE_EMAIL = 1;
    public static int QUEUE_WHATSAPP = 2;


    private Correspondence[] correspondences;
    private final JTextField total;

    SeeTableForBuyersClass customTable;
    JTable table;
    JDialog dialog;

    public SeeBuyersForMessageClass(JDialog dialog, int queueType) {
        this.dialog = dialog;
        this.setLayout(new BorderLayout());

        this.total = new JTextField();
        this.total.setEditable(false);


        customTable = new SeeTableForBuyersClass();
        this.table = new JTable(customTable);
        this.table.getColumnModel().getColumn(0).setCellEditor(new ClientWindow.ToggleButtonEditor(this.table));

        JCheckBox selectAllCheckBox = new JCheckBox("Выбрать всех");
        selectAllCheckBox.addActionListener(e -> {
            boolean selected = selectAllCheckBox.isSelected();
            for (int i = 0; i < customTable.objects.length; i++) {
                if (selected) {
                    customTable.objects[i][0] = true;
                } else {
                    customTable.objects[i][0] = false;
                }
            }
            customTable.fireTableDataChanged();
            table.repaint();
        });

        JButton deleteSelected = new JButton("Удалить выбранных");
        deleteSelected.addActionListener(e -> {
            List<Buyer> buyerList = new ArrayList<>();
            for (int i = 0; i < customTable.getRowCount(); i++) {
                if ((boolean) customTable.objects[i][0])
                    buyerList.add(Database.getBuyer((int) customTable.objects[i][1]));
            }

            if (queueType == QUEUE_EMAIL)
                Output.addMessage(new MessageToServer(
                        MessageTypeToServer.GMAIL_MESSAGE_CANCEL,
                        correspondences,
                        buyerList.toArray(new Buyer[0])
                ));

            if (queueType == QUEUE_WHATSAPP)
                Output.addMessage(new MessageToServer(
                        MessageTypeToServer.WHATSAPP_MESSAGE_CANCEL,
                        correspondences,
                        buyerList.toArray(new Buyer[0])
                ));

            dialog.setVisible(false);
        });


        JPanel upPanel = new JPanel();
        upPanel.add(selectAllCheckBox);
        upPanel.add(deleteSelected);

        this.add(upPanel, BorderLayout.NORTH);
        this.add(table, BorderLayout.CENTER);
    }

    public synchronized void setNewData(TreeSet<Buyer> buyers, Correspondence[] correspondences) {
        total.setText("Всего получателей: " + buyers.size());

        this.correspondences = correspondences;

        customTable.setNewData(buyers);
        this.table.repaint();
    }


    private static class SeeTableForBuyersClass extends AbstractTableModel {
        private Object[][] objects;


        private final String[] columnNames = {"Выделение", "ID", "Имя компании", "Тип",
                "Регион", "Менеджер", "Категория", "Источник", "Почты", "Контактная информация", "Комментарии"};

        public synchronized void setNewData(TreeSet<Buyer> buyers) {

            List<Object[]> objectList = new ArrayList<>();
            for (Buyer buyer : buyers) {
                Object[] buyerObject = buyer.getLine();

                objectList.add(new Object[]{buyerObject[0], buyerObject[4], buyerObject[5], buyerObject[6], buyerObject[7],
                        buyerObject[8], buyerObject[9], buyerObject[10], buyerObject[11], buyerObject[12], buyerObject[13]});
            }

            objects = objectList.toArray(new Object[0][0]);

            this.fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return objects.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return objects[rowIndex][columnIndex];
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            objects[rowIndex][columnIndex] = aValue;
            fireTableCellUpdated(rowIndex, columnIndex); // Обновление отображения ячейки
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0)
                return Boolean.class;
            else if (columnIndex == 1)
                return Integer.class;
            else
                return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex <= 0;
        }
    }
}