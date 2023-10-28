package gui.windows;

import database.Database;
import gate.Output;
import data.Buyer;
import data.ContactInfo;
import data.StaticData.BuyerType;
import data.StaticData.ClientCategory;
import data.StaticData.Region;
import data.StaticData.SourceType;
import senderData.MessageToServer;
import senderData.MessageTypeToServer;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CreateRedactClientWindow extends JPanel {
    Buyer buyer;
    boolean isNew;

    private final JTextField id;
    private final JTextField companyName;
    private final JComboBox<BuyerType> type;
    private final JComboBox<Region> region;
    private final JComboBox<ClientCategory> category;
    private final JComboBox<String> manager;
    private final JTextArea address;
    private final JTextField site;
    private final JComboBox<SourceType> source;
    private final JTextArea additionalInfo;

    private final JPanel emailsList;
    private final JPanel contactsList;

    private final JPanel emailP;
    private final JPanel contactsP;

    private final JButton addEmail;
    private final JButton addContact;

    private final JButton saveButton;

    public CreateRedactClientWindow(JDialog dialog) {

        this.id = new JTextField();
        this.companyName = new JTextField();
        this.type = new JComboBox<>();
        this.region = new JComboBox<>();
        this.category = new JComboBox<>();
        this.manager = new JComboBox<>();
        this.address = new JTextArea();
        this.site = new JTextField();
        this.source = new JComboBox<>();
        this.additionalInfo = new JTextArea();

        this.id.setEditable(false);
        this.type.setEditable(false);
        this.region.setEditable(false);
        this.category.setEditable(false);
        this.manager.setEditable(false);
        this.source.setEditable(false);

        for(BuyerType typeB: BuyerType.values())
            this.type.addItem(typeB);

        for(Region regionR: Region.values())
            this.region.addItem(regionR);

        for(ClientCategory clientCategory: ClientCategory.values())
            this.category.addItem(clientCategory);

        for(String managerM: Database.getManagersList())
            this.manager.addItem(managerM);

        for(SourceType sourceType: SourceType.values())
            this.source.addItem(sourceType);

        this.emailsList = new JPanel();
        this.contactsList = new JPanel();

        emailsList.setLayout(new GridBagLayout());
        contactsList.setLayout(new GridBagLayout());


        JTextField idN = new JTextField("ID:");
        JTextField companyNameN = new JTextField("Название компании:");
        JTextField typeN = new JTextField("Тип:");
        JTextField regionN = new JTextField("Регион:");
        JTextField categoryN = new JTextField("Категория:");
        JTextField managerN = new JTextField("Менеджер:");
        JTextField addressN = new JTextField("Адрес:");
        JTextField siteN = new JTextField("Сайт:");
        JTextField sourceN = new JTextField("Источник:");
        JTextField additionalInfoN = new JTextField("Комментарий:");

        idN.setEditable(false);
        companyNameN.setEditable(false);
        typeN.setEditable(false);
        regionN.setEditable(false);
        categoryN.setEditable(false);
        managerN.setEditable(false);
        addressN.setEditable(false);
        siteN.setEditable(false);
        sourceN.setEditable(false);
        additionalInfoN.setEditable(false);

        LayoutManager layout = new GridLayout(1,2);

        JPanel idP = new JPanel(layout);
        JPanel companyNameP = new JPanel(layout);
        JPanel typeP = new JPanel(layout);
        JPanel regionP = new JPanel(layout);
        JPanel categoryP = new JPanel(layout);
        JPanel managerP = new JPanel(layout);
        JPanel addressP = new JPanel(layout);
        JPanel siteP = new JPanel(layout);
        JPanel sourceP = new JPanel(layout);
        JPanel additionalInfoP = new JPanel(layout);

        idP.add(idN);
        idP.add(id);
        companyNameP.add(companyNameN);
        companyNameP.add(companyName);
        typeP.add(typeN);
        typeP.add(type);
        regionP.add(regionN);
        regionP.add(region);
        categoryP.add(categoryN);
        categoryP.add(category);
        managerP.add(managerN);
        managerP.add(manager);
        addressP.add(addressN);
        addressP.add(address);
        siteP.add(siteN);
        siteP.add(site);
        sourceP.add(sourceN);
        sourceP.add(source);
        additionalInfoP.add(additionalInfoN);
        additionalInfoP.add(additionalInfo);

        JPanel standardInfo = new JPanel();
        standardInfo.setLayout(new BoxLayout(standardInfo, BoxLayout.Y_AXIS));

        standardInfo.add(idP);
        standardInfo.add(companyNameP);
        standardInfo.add(typeP);
        standardInfo.add(regionP);
        standardInfo.add(categoryP);
        standardInfo.add(managerP);
        standardInfo.add(addressP);
        standardInfo.add(siteP);
        standardInfo.add(sourceP);
        standardInfo.add(additionalInfoP);

         JPanel emailAll = new JPanel();
         JPanel contactsAll = new JPanel();

         emailAll.setLayout(new BoxLayout(emailAll, BoxLayout.Y_AXIS));
         contactsAll.setLayout(new BoxLayout(contactsAll, BoxLayout.Y_AXIS));

         this.addEmail = new JButton("Добавить почту");
         this.addContact = new JButton("Добавить контакт");

        addEmail.addActionListener(e -> {
            EmailClass emailClass = new EmailClass(this.emailsList);
            addComponent(this.emailsList, emailClass, emailsList.getComponentCount());
            emailClass.viewEmail(true);

            emailsList.updateUI();
            this.updateUI();
        });

        addContact.addActionListener(e -> {
            ContactsClass contactsClass = new ContactsClass(this.contactsList);
            addComponent(this.contactsList, contactsClass, contactsList.getComponentCount());

            contactsClass.viewContactInfo(true);

            this.contactsList.updateUI();
            this.repaint();
        });

        JTextField emailN = new JTextField("Почта:");
        JTextField contactN = new JTextField("Контакты:");

        emailN.setEditable(false);
        contactN.setEditable(false);

        this.emailP = new JPanel(layout);
        this.contactsP = new JPanel(layout);

        emailP.add(emailN);
        emailP.add(addEmail);

        contactsP.add(contactN);
        contactsP.add(addContact);

        JScrollPane emailScrollPane = new JScrollPane(this.emailsList);
        emailScrollPane.setPreferredSize(new Dimension(1000, 200));
        emailAll.add(emailP);
        emailAll.add(emailScrollPane);
        emailAll.setPreferredSize(new Dimension(1000, 250));

        JScrollPane contactsScrollPane = new JScrollPane(this.contactsList);
        contactsScrollPane.setPreferredSize(new Dimension(1000, 200));
        contactsAll.add(contactsP);
        contactsAll.add(contactsScrollPane);
        contactsAll.setPreferredSize(new Dimension(1000, 250));

        this.saveButton = new JButton("Сохранить");
        this.saveButton.addActionListener(e -> {
            if(isNew)
                Output.addMessage(new MessageToServer(MessageTypeToServer.ADD_NEW_CLIENT, this.returnClient(), null));
            else
                Output.addMessage(new MessageToServer(MessageTypeToServer.REDACT_CLIENT, this.returnClient(), null));

            dialog.setVisible(false);
        });

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(standardInfo);
        this.add(emailAll);
        this.add(contactsAll);

        this.setPreferredSize(new Dimension(1000, 1000));
    }

    public synchronized void setClient(Buyer buyer, boolean isNew) {
        this.buyer = buyer;
        this.isNew = isNew;
    }

    public synchronized void viewClient(boolean isRedact) {
        this.emailP.remove(this.addEmail);
        this.contactsP.remove(this.addContact);
        this.remove(saveButton);

        removeAllComponents(this.emailsList);
        removeAllComponents(this.contactsList);

        if(isRedact) {
            this.add(saveButton);

            this.emailP.add(this.addEmail);
            this.contactsP.add(this.addContact);

            this.companyName.setEditable(true);
            this.address.setEditable(true);
            this.site.setEditable(true);
            this.additionalInfo.setEditable(true);

            this.type.setEnabled(true);
            this.region.setEnabled(true);
            this.category.setEnabled(true);
            this.manager.setEnabled(true);
            this.source.setEnabled(true);
        } else {
            this.companyName.setEditable(false);
            this.address.setEditable(false);
            this.site.setEditable(false);
            this.additionalInfo.setEditable(false);

            this.type.setEnabled(false);
            this.region.setEnabled(false);
            this.category.setEnabled(false);
            this.manager.setEnabled(false);
            this.source.setEnabled(false);
        }

        if(buyer != null) {
            this.id.setText("" + buyer.id());

            this.companyName.setText(buyer.companyName());
            this.type.setSelectedItem(buyer.type());
            this.region.setSelectedItem(buyer.region());
            this.category.setSelectedItem(buyer.category());
            this.manager.setSelectedItem(buyer.manager());
            this.address.setText(buyer.address());
            this.site.setText(buyer.site());
            this.source.setSelectedItem(buyer.source());
            this.additionalInfo.setText(buyer.additionalInfo());

            for(ContactInfo contactInfo: buyer.contactInfos()) {
                ContactsClass contactsClass = new ContactsClass(this.contactsList);
                contactsClass.setContactInfo(contactInfo);
                contactsClass.viewContactInfo(isRedact);

                addComponent(this.contactsList, contactsClass, contactsList.getComponentCount());

            }

            for(String email: buyer.emails()) {
                EmailClass emailClass = new EmailClass(this.emailsList);
                emailClass.setEmail(email);
                emailClass.viewEmail(isRedact);

                addComponent(this.emailsList, emailClass, emailsList.getComponentCount());
            }

        } else {
            this.id.setText("-1");
            this.companyName.setText("");
            this.type.setSelectedItem(BuyerType.NONE);
            this.region.setSelectedItem(Region.NONE);
            this.category.setSelectedItem(ClientCategory.NONE);
            this.address.setText("");
            this.site.setText("");
            this.source.setSelectedItem(SourceType.NONE);
            this.additionalInfo.setText("");
        }
    }

    public synchronized void updateManagersList(String[] managersArray) {
        manager.removeAllItems();
        for(String managerM: managersArray)
            this.manager.addItem(managerM);

        manager.updateUI();
        this.updateUI();
    }

    public synchronized Buyer returnClient() {
        List<String> emails = new ArrayList<>();
        List<ContactInfo> contactInfos = new ArrayList<>();

        for(int i = 0; i < emailsList.getComponentCount(); i++) {
            String emailStr = ( (EmailClass) emailsList.getComponent(i) ).returnEmail();
            if(emailStr != null)
                emails.add(emailStr);
        }

        for(int i = 0; i < contactsList.getComponentCount(); i++) {
            ContactInfo cont = ((ContactsClass) contactsList.getComponent(i)).returnContactInfo();
            if(cont != null)
                contactInfos.add(cont);
        }

        return new Buyer(
                Integer.parseInt(id.getText()),
                companyName.getText(),
                address.getText(),
                emails.toArray(new String[0]),
                site.getText(),
                contactInfos.toArray(new ContactInfo[0]),
                (String) manager.getSelectedItem(),
                (BuyerType) type.getSelectedItem(),
                (Region) region.getSelectedItem(),
                (SourceType) source.getSelectedItem(),
                (ClientCategory) category.getSelectedItem(),
                additionalInfo.getText()
                );
    }

    private synchronized void removeAllComponents(JPanel panel) {
        Component[] components = panel.getComponents();

        for (Component component : components) {
            panel.remove(component);
        }

        panel.updateUI();
    }

    private static void addComponent(JPanel panel, JComponent component, int gridy) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = gridy;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.insets = new Insets(5, 5, 5, 5); // Отступы
        panel.add(component, constraints);
    }



    private static class EmailClass extends JPanel {
        private final JTextField email;
        private final JButton delete;

        public EmailClass(JPanel emailList) {

            email = new JTextField();
            JTextField emailName = new JTextField("Почта:");
            emailName.setEditable(false);

            delete = new JButton("Удалить");
            delete.addActionListener(e -> {
                emailList.remove(this);
                emailList.updateUI();
            });

            this.setLayout(new GridLayout(1, 3));

            this.add(emailName);
            this.add(email);

            this.setPreferredSize(new Dimension(1000, 40));
        }

        public void setEmail(String email) {
            this.email.setText(email);
        }

        public void viewEmail(boolean isRedact) {
            if(isRedact) {
                this.email.setEditable(true);
                this.add(delete);
            }
            else
                this.email.setEditable(false);
        }

        public String returnEmail() {
            if(!this.email.getText().contains("@"))
                return null;

            return this.email.getText();
        }
    }

    private static class ContactsClass extends JPanel {
        private ContactInfo info;

        private final JTextField name;
        private final JTextField surname;
        private final JTextField jobTitle;
        private final JTextField phone;
        private final JCheckBox isWhatsApp;

        private final JButton delete;

        public ContactsClass(JPanel contactsList) {

            JTextField nameName = new JTextField("Имя:");
            JTextField surnameName = new JTextField("Фамилия:");
            JTextField jobTitleName = new JTextField("Должность:");
            JTextField phoneName = new JTextField("Телефон:");

            nameName.setEditable(false);
            surnameName.setEditable(false);
            jobTitleName.setEditable(false);
            phoneName.setEditable(false);

            this.name = new JTextField();
            this.surname = new JTextField();
            this.jobTitle = new JTextField();
            this.phone = new JTextField();
            this.isWhatsApp = new JCheckBox("Есть ли WhatsApp?");

            this.delete = new JButton("Удалить");

            this.setLayout(new GridLayout(3,4));


            this.add(nameName);
            this.add(name);
            this.add(phoneName);
            this.add(phone);
            this.add(surnameName);
            this.add(surname);
            this.add(jobTitleName);
            this.add(jobTitle);
            this.add(isWhatsApp);

            this.setBorder(new LineBorder(Color.black, 1));

            delete.addActionListener(e -> {
                contactsList.remove(this);
                contactsList.updateUI();
            });

            this.setPreferredSize(new Dimension(1000, 100));
        }

        public void setContactInfo(ContactInfo info) {
            this.info = info;
        }

        public void viewContactInfo(boolean isRedact) {
            if(isRedact) {
                name.setEditable(true);
                surname.setEditable(true);
                jobTitle.setEditable(true);
                phone.setEditable(true);

                isWhatsApp.setSelected(true);
                isWhatsApp.setEnabled(true);

                this.add(delete);
            }
            else {
                name.setEditable(false);
                surname.setEditable(false);
                jobTitle.setEditable(false);
                phone.setEditable(false);
                isWhatsApp.setEnabled(false);
            }

            if(info != null) {
                name.setText(info.name());
                surname.setText(info.surname());
                jobTitle.setText(info.jobTitle());
                phone.setText("+" + info.phone());
                isWhatsApp.setSelected(info.isWhatsApp());
            }

            this.repaint();
        }

        public ContactInfo returnContactInfo() {
            if(phone.getText().length() == 0)
                return null;

            return new ContactInfo(name.getText(), surname.getText(), jobTitle.getText(),
                    Long.parseLong(phone.getText().replaceAll("\\D", "")), isWhatsApp.isSelected());
        }
    }
}
