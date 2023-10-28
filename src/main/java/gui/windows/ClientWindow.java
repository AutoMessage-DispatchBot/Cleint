package gui.windows;

import database.Database;
import database.SaveClients;
import gate.Output;
import data.Buyer;
import data.Correspondence;
import data.Filter;
import data.StaticData.BuyerType;
import data.StaticData.ClientCategory;
import data.StaticData.Region;
import senderData.MessageToServer;
import senderData.MessageTypeToServer;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClientWindow extends JPanel {
    private final CustomTable customTable;
    private final JTable table;

    private final MyPopupMenu<BuyerType> buyerTypePopup;
    private final MyPopupMenu<ClientCategory> clientCategoryPopup;
    private final MyPopupMenu<Region> regionPopup;
    private final MyPopupMenu<String> managersPopup;

    private final JButton[] buttons;

    private final CreateRedactClientWindow createRedactClientWindow;
    private final JDialog createRedactClientDialog;

    private final CreateRedactMessageEmailWindow createRedactMessageEmailWindow;
    private final JDialog createRedactMessageEmailDialog;

    private final CreateRedactMessageWhatsAppWindow createRedactMessageWhatsAppWindow;
    private final JDialog createRedactMessageWhatsAppDialog;

    private final JCheckBox isSendEmail;
    private final JCheckBox isSendWhatsApp;

    private final JTextField total;

    public ClientWindow() {
        // сумма клиентов
        this.total = new JTextField();
        total.setEditable(false);

        // Всплывающие меню создание/редактирование клиентов
        createRedactClientDialog = new JDialog((JFrame) null, "Клиент", true);
        createRedactClientDialog.setLayout(null);
        createRedactClientDialog.setSize(new Dimension(1500, 1000));

        createRedactClientWindow = new CreateRedactClientWindow(createRedactClientDialog);
        createRedactClientDialog.setContentPane(createRedactClientWindow);

        // Всплывающие меню сообщение почта
        createRedactMessageEmailDialog = new JDialog((JFrame) null, "Создание сообщения на почту", true);
        createRedactMessageEmailDialog.setLayout(null);
        createRedactMessageEmailDialog.setSize(new Dimension(650, 800));

        createRedactMessageEmailWindow = new CreateRedactMessageEmailWindow(createRedactMessageEmailDialog);
        createRedactMessageEmailDialog.setContentPane(createRedactMessageEmailWindow);

        // Всплывающие меню сообщение whatsApp
        createRedactMessageWhatsAppDialog = new JDialog((JFrame) null, "Создание сообщения на WhatsApp", true);
        createRedactMessageWhatsAppDialog.setLayout(null);
        createRedactMessageWhatsAppDialog.setSize(new Dimension(650, 800));

        createRedactMessageWhatsAppWindow = new CreateRedactMessageWhatsAppWindow(createRedactMessageWhatsAppDialog);
        createRedactMessageWhatsAppDialog.setContentPane(createRedactMessageWhatsAppWindow);


        // таблица
        this.customTable = new CustomTable();

        this.table = new JTable(customTable);
        setTableProperties();


        // фильтры
        this.buyerTypePopup = new MyPopupMenu<>(BuyerType.values());
        this.clientCategoryPopup = new MyPopupMenu<>(ClientCategory.values());
        this.regionPopup = new MyPopupMenu<>(Region.values());
        this.managersPopup = new MyPopupMenu<>(Database.getManagersList().toArray(new String[0]));

        buttons = new JButton[] {
                new JButton("Тип"),
                new JButton("Категория"),
                new JButton("Регион"),
                new JButton("Менеджер")
        };

        buttons[0].addActionListener(e -> buyerTypePopup.show(buttons[0], 0, buttons[0].getHeight()));
        buttons[1].addActionListener(e -> clientCategoryPopup.show(buttons[1], 0, buttons[1].getHeight()));
        buttons[2].addActionListener(e -> regionPopup.show(buttons[2], 0, buttons[2].getHeight()));
        buttons[3].addActionListener(e -> managersPopup.show(buttons[3], 0, buttons[3].getHeight()));


        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new GridLayout(1, 7));


        // Выбрать всех
        JCheckBox selectAllCheckBox = new JCheckBox("Выбрать всех");
        selectAllCheckBox.addActionListener(e -> {
            boolean selected = selectAllCheckBox.isSelected();
            for(int i = 0; i < customTable.objects.length; i++) {
                if (selected) {
                    customTable.objects[i][0] = true;
                } else {
                    customTable.objects[i][0] = false;
                }
            }
            customTable.fireTableDataChanged();
            table.repaint();
        });

        filterPanel.add(total);
        filterPanel.add(selectAllCheckBox);


        // Кнопки фильтров
        for(JButton button: buttons) {
            button.setForeground(Color.BLUE);
            filterPanel.add(button);
        }

        JButton filterButton = new JButton("Показать");
        filterButton.setForeground(Color.ORANGE);
        filterButton.addActionListener(e -> Output.addMessage(new MessageToServer(MessageTypeToServer.GIVE_CLIENT_LIST_WITH_FILTER, getFilter(), null)));

        filterPanel.add(filterButton);

        // сортировка
        JPanel sortedPanel = new JPanel();
        sortedPanel.setLayout(new GridLayout(1, 4));

        JButton compareById = new JButton("Сортировка по ID");
        JButton compareByNumber = new JButton("Сортировка по номеру");
        JButton compareByCompanyName = new JButton("Сортировка по компании (алфавит)");
        JButton compareByManager = new JButton("Сортировка по менеджеру (алфавит)");

        compareById.setForeground(Color.DARK_GRAY);
        compareByNumber.setForeground(Color.DARK_GRAY);
        compareByCompanyName.setForeground(Color.DARK_GRAY);
        compareByManager.setForeground(Color.DARK_GRAY);

        sortedPanel.add(compareById);
        sortedPanel.add(compareByCompanyName);
        sortedPanel.add(compareByManager);
        sortedPanel.add(compareByNumber);

        compareById.addActionListener(e -> {
            Database.getBuyersList().sort(new Buyer.COMPARE_BY_ID());

            setNewData(Database.getBuyersList());

        });
        compareByCompanyName.addActionListener(e -> {
            Database.getBuyersList().sort(new Buyer.COMPARE_BY_ALPHABET());

            setNewData(Database.getBuyersList());
        });
        compareByManager.addActionListener(e -> {
            Database.getBuyersList().sort(new Buyer.COMPARE_BY_MANAGER());

            setNewData(Database.getBuyersList());

        });
        compareByNumber.addActionListener(e -> {
            Database.getBuyersList().sort(new Buyer.COMPARE_BY_NUMBER());

            setNewData(Database.getBuyersList());

        });


        // Панель сообщений
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new GridLayout(1, 5));

        this.isSendEmail = new JCheckBox("Отправка почты", false);
        this.isSendWhatsApp = new JCheckBox("Отправка whatsApp", false);

        JButton emailMessage = new JButton("Настройка сообщения e-mail");
        JButton whatsappMessage = new JButton("Настройка сообщения WhatsApp");
        JButton sendMessages = new JButton("Начать рассылку");

        emailMessage.addActionListener(e -> createRedactMessageEmailDialog.setVisible(true));
        whatsappMessage.addActionListener(e -> createRedactMessageWhatsAppDialog.setVisible(true));
        sendMessages.addActionListener(e -> {
            List<Buyer> buyerList = new ArrayList<>();
            for(int i = 0; i < customTable.getRowCount(); i++) {
                if((boolean) customTable.objects[i][0])
                    buyerList.add(Database.getBuyer((int) customTable.objects[i][4]));
            }

            if(isSendEmail.isSelected() && createRedactMessageEmailWindow.getCorrespondence() != null) {
                Correspondence[] correspondenceToMessage = createRedactMessageEmailWindow.getCorrespondence();
                Buyer[] buyersToMessage = buyerList.toArray(new Buyer[0]);

                Output.addMessage(new MessageToServer(
                        MessageTypeToServer.GMAIL_MESSAGE_SENDING,
                        correspondenceToMessage,
                        buyersToMessage
                ));
            }

            if(isSendWhatsApp.isSelected() && createRedactMessageWhatsAppWindow.getCorrespondence() != null) {
                Output.addMessage(new MessageToServer(
                        MessageTypeToServer.WHATSAPP_MESSAGE_SENDING,
                        createRedactMessageWhatsAppWindow.getCorrespondence(),
                        buyerList.toArray(new Buyer[0])
                ));
            }

            isSendEmail.setSelected(false);
            isSendWhatsApp.setSelected(false);
        });

        messagePanel.add(isSendEmail);
        messagePanel.add(isSendWhatsApp);
        messagePanel.add(emailMessage);
        messagePanel.add(whatsappMessage);
        messagePanel.add(sendMessages);



        // Сохранение клиентов
        JPanel saveClients = new JPanel(new GridLayout(1,4));

        JButton newClient = new JButton("Новый клиент");
        newClient.addActionListener(e -> {
            createRedactClientWindow.setClient(null, true);
            createRedactClientWindow.viewClient(true);

            createRedactClientDialog.setVisible(true);
        });

        JButton saveCSV = new JButton("Экспорт в Google контакты");
        saveCSV.addActionListener(e -> {
            List<Buyer> buyerList = new ArrayList<>();

            for(int i = 0; i < customTable.getRowCount(); i++) {
                if((boolean) customTable.objects[i][0])
                    buyerList.add(Database.getBuyer((int) customTable.objects[i][4]));
            }

            if(buyerList.size() > 0)
                SaveClients.saveCsvContacts(buyerList);
        });
        JButton saveExcel = new JButton("Экспорт в Excel");
        saveExcel.addActionListener(e -> {
            List<Buyer> buyerList = new ArrayList<>();

            for(int i = 0; i < customTable.getRowCount(); i++) {
                if((boolean) customTable.objects[i][0])
                    buyerList.add(Database.getBuyer((int) customTable.objects[i][4]));
            }

            if(buyerList.size() > 0)
                SaveClients.saveExcelContacts(buyerList);
        });

        JButton newManager = new JButton("Новый менеджер");
        newManager.addActionListener(e -> {
            String inputText = JOptionPane.showInputDialog(null, "Введите нового менеджера:", "Создание менеджера", JOptionPane.QUESTION_MESSAGE);

            // Обработка введенного текста
            if (inputText != null && !inputText.isEmpty()) {
                Output.addMessage(new MessageToServer(
                        MessageTypeToServer.ADD_MANAGER_TO_LIST,
                        inputText,
                        null
                ));
            }
        });

        saveClients.add(newClient);
        saveClients.add(newManager);
        saveClients.add(saveCSV);
        saveClients.add(saveExcel);



        // messagePanel + saveClients panel
        JPanel sumMessageSavePanels = new JPanel(new GridLayout(2,1));
        sumMessageSavePanels.add(messagePanel);
        sumMessageSavePanels.add(saveClients);



        // Добавление
        this.setLayout(new BorderLayout());
        this.add(new JScrollPane(this.table), BorderLayout.CENTER);
        JPanel filterNsorted = new JPanel();
        filterNsorted.setLayout(new GridLayout(2,1));
        filterNsorted.add(filterPanel);
        filterNsorted.add(sortedPanel);
        this.add(filterNsorted, BorderLayout.NORTH);
        this.add(sumMessageSavePanels, BorderLayout.SOUTH);
    }

    public synchronized void setNewData(List<Buyer> buyers) {
        List<Object[]> objectsList = new ArrayList<>();
        for(Buyer buyer: buyers)
            objectsList.add(buyer.getLine());

        customTable.objects = objectsList.toArray(new Object[0][0]);
        customTable.fireTableDataChanged();
        table.repaint();
        total.setText("Всего клиентов: " + objectsList.size());

    }

    public synchronized void updateManagersList(List<String> managers) {
        String[] managersArray = managers.toArray(new String[0]);

        managersPopup.updateData(managersArray);
        createRedactClientWindow.updateManagersList(managersArray);

        this.updateUI();
    }

    private synchronized void setTableProperties() {
        this.table.getColumnModel().getColumn(0).setCellEditor(new ToggleButtonEditor(this.table));
        this.table.getColumnModel().getColumn(1).setCellEditor(new ButtonEditor(this.table, createRedactClientWindow, createRedactClientDialog));
        this.table.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(this.table, createRedactClientWindow, createRedactClientDialog));
        this.table.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(this.table, createRedactClientWindow, createRedactClientDialog));

        this.table.getColumnModel().getColumn(0).setPreferredWidth(50);
        this.table.getColumnModel().getColumn(1).setPreferredWidth(120);
        this.table.getColumnModel().getColumn(2).setPreferredWidth(80);
        this.table.getColumnModel().getColumn(3).setPreferredWidth(80);
        this.table.getColumnModel().getColumn(4).setPreferredWidth(40);
        this.table.getColumnModel().getColumn(5).setPreferredWidth(320);
        this.table.getColumnModel().getColumn(6).setPreferredWidth(110);
        this.table.getColumnModel().getColumn(7).setPreferredWidth(120);
        this.table.getColumnModel().getColumn(8).setPreferredWidth(150);
        this.table.getColumnModel().getColumn(9).setPreferredWidth(80);
        this.table.getColumnModel().getColumn(10).setPreferredWidth(80);
        this.table.getColumnModel().getColumn(11).setPreferredWidth(200);
        this.table.getColumnModel().getColumn(12).setPreferredWidth(350);
        this.table.getColumnModel().getColumn(13).setPreferredWidth(400);
    }

    private synchronized Filter getFilter() {
        return new Filter(
                buyerTypePopup.getSelectedData(BuyerType.class),
                clientCategoryPopup.getSelectedData(ClientCategory.class),
                regionPopup.getSelectedData(Region.class),
                managersPopup.getSelectedData(String.class)
        );
    }







    private static class CustomTable extends AbstractTableModel {
        private Object[][] objects = new Object[0][14];

        private final String[] columnNames = {"Выделение", "Редактировать", "Удалить", "Просмотр", "ID", "Имя компании", "Тип",
                "Регион", "Менеджер", "Категория", "Источник", "Почты", "Контактная информация", "Комментарии"};

        public void setNewData(Object[][] objects) {
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
            else if (columnIndex == 1 || columnIndex == 2 || columnIndex == 3)
                return JButton.class;
            else if (columnIndex == 4)
                return Integer.class;
            else
                return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex <= 3;
        }
    }

    static class ToggleButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JToggleButton button;

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

    private static class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JButton button;

        public ButtonEditor(JTable table, CreateRedactClientWindow createRedactClientWindow, JDialog dialog) {
            button = new JButton();

            button.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                int selectedColumn = table.getSelectedColumn();
                int id = (int) table.getValueAt(selectedRow, 4);

                if(selectedColumn == 1) {
                    // редактировать
                    createRedactClientWindow.setClient(Database.getBuyer(id), false);
                    createRedactClientWindow.viewClient(true);
                    dialog.setVisible(true);
                }

                else if(selectedColumn == 2) {
                    int result = JOptionPane.showOptionDialog(
                            null,
                            "Вы подтверждаете удаление клиента #" + id,
                            "Confirmation",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            new Object[]{"Да", "Нет"},
                            "Да"
                    );

                    if (result == JOptionPane.YES_OPTION) {
                        Output.addMessage(new MessageToServer(MessageTypeToServer.DELETE_CLIENT, Database.getBuyer(id), null));
                    }
                }

                else if(selectedColumn == 3) {
                    // Просмотр
                    createRedactClientWindow.setClient(Database.getBuyer(id), false);
                    createRedactClientWindow.viewClient(false);
                    dialog.setVisible(true);

                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setText(table.getColumnName(column));
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }
    }
}
