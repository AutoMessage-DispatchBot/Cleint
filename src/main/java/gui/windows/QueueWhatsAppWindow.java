package gui.windows;

import gate.Output;
import data.Buyer;
import data.Correspondence;
import senderData.MessageToServer;
import senderData.MessageTypeToServer;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.TreeSet;

public class QueueWhatsAppWindow extends JPanel {

    private final JPanel messagesList;
    private final JPanel authorizedPanel;

    private final JButton signInButton;

    private final SignInDialogClass signInDialogClass;

    private final JTextField signText;

    private final GridBagConstraints constraints;

    private final SeeBuyersForMessageClass seeBuyers;
    private final JDialog seeBuyersDialog;

    public QueueWhatsAppWindow() {
        this.setLayout(new BorderLayout());

        messagesList = new JPanel();
        messagesList.setLayout(new GridBagLayout());

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(5, 5, 5, 5);



        // JDialog - выполнить вход
        JDialog signDialog = new JDialog((JFrame) null, "Выполнить вход", true);
        signDialog.setLayout(null);
        signDialog.setSize(new Dimension(400, 550));

        this.signInDialogClass = new SignInDialogClass(signDialog, this);
        signDialog.setContentPane(signInDialogClass);



        // Меню входа
        authorizedPanel = new JPanel(new GridBagLayout());

        signInButton = new JButton("Выполнить вход");
        signInButton.addActionListener(e -> {
            Output.addMessage(new MessageToServer(
                    MessageTypeToServer.WHATSAPP_READY_TO_AUTHORIZE,
                    true,
                    true
            ));

            signInDialogClass.clearImage();
            signDialog.setVisible(true);
        });
        signInButton.setPreferredSize(new Dimension(150, 25));

        signText = new JTextField("Вход выполнен: ДА");
        signText.setEditable(false);
        signText.setPreferredSize(new Dimension(150, 25));

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        authorizedPanel.add(signText, constraints);



        // Вставить элементы
        this.add(authorizedPanel, BorderLayout.NORTH);
        this.add(new JScrollPane(messagesList), BorderLayout.CENTER);

        seeBuyersDialog = new JDialog((JFrame) null, "Клиенты в очереди", true);
        seeBuyersDialog.setLayout(null);
        seeBuyersDialog.setSize(1500, 1000);

        seeBuyers = new SeeBuyersForMessageClass(seeBuyersDialog, SeeBuyersForMessageClass.QUEUE_WHATSAPP);
        seeBuyersDialog.setContentPane(seeBuyers);
    }

    public synchronized void setQueue(Map<Correspondence[], TreeSet<Buyer>> queue) {

        for(int i = 0; i < messagesList.getComponentCount(); i++)
            messagesList.remove(i);

        for(Correspondence[] key: queue.keySet()) {
            constraints.gridx = messagesList.getComponentCount() % 3;
            constraints.gridy = messagesList.getComponentCount() / 3;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;

            messagesList.add(new SeeMessageClass(key, queue.get(key), messagesList, seeBuyers, seeBuyersDialog), constraints);
        }

        messagesList.updateUI();
    }

    public synchronized void setAuthorized(boolean isAuthorized) {
        if(isAuthorized) {
            authorizedPanel.remove(signInButton);
            signText.setText("Вход выполнен: ДА");
        }

        else {
            constraints.gridx = 1;
            constraints.gridy = 0;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            authorizedPanel.add(signInButton, constraints);
            signText.setText("Вход выполнен: НЕТ");
        }

        authorizedPanel.updateUI();
    }

    public synchronized void setQrCode(BufferedImage image) {
        signInDialogClass.setImage(image);
    }





    private static class SeeMessageClass extends JPanel {


        public SeeMessageClass(
                Correspondence[] correspondences,
                TreeSet<Buyer> buyers,
                JPanel messagesList,
                SeeBuyersForMessageClass seeBuyers,
                JDialog dialog) {


            JPanel localMessagesList = new JPanel();
            localMessagesList.setLayout(new GridBagLayout());
            this.setLayout(new GridBagLayout());


            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.BOTH;
            constraints.insets = new Insets(5, 5, 5, 5);


            // JButtons
            JButton seeAll = new JButton("Посмотреть получателей");
            JButton clearAll = new JButton("Отменить все");

            seeAll.addActionListener(e -> {
                seeBuyers.setNewData(buyers, correspondences);
                dialog.setVisible(true);
            });

            clearAll.addActionListener(e -> {
                int result = JOptionPane.showOptionDialog(
                        null,
                        "Вы подтверждаете отмену отправки сообщений получателям?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new Object[]{"Да", "Нет"},
                        "Да"
                );

                if (result == JOptionPane.YES_OPTION) {
                    Output.addMessage(new MessageToServer(
                            MessageTypeToServer.WHATSAPP_MESSAGE_CANCEL,
                            correspondences,
                            buyers.toArray(new Buyer[0])
                    ));
                    messagesList.remove(this);
                    messagesList.updateUI();
                }
            });

            constraints.gridx = 0;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            for(Correspondence correspondence: correspondences) {
                constraints.gridy = localMessagesList.getComponentCount();
                localMessagesList.add(new SeeOneMessageForMessageClass(correspondence), constraints);
            }

            constraints.gridy = 0;
            JScrollPane scrollPane = new JScrollPane(localMessagesList);
            scrollPane.setPreferredSize(new Dimension(450, 500));
            this.add(scrollPane, constraints);

            constraints.gridy = 1;
            this.add(seeAll, constraints);

            constraints.gridy = 2;
            this.add(clearAll, constraints);
        }
    }


    private static class SeeOneMessageForMessageClass extends JPanel {

        public SeeOneMessageForMessageClass(Correspondence correspondence) {
            this.setLayout(new GridBagLayout());
            this.setBorder(new LineBorder(Color.BLACK, 1));


            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.BOTH;
            constraints.insets = new Insets(5, 5, 5, 5);



            JTextField number = new JTextField();
            number.setEditable(false);
            number.setPreferredSize(new Dimension(200, 25));

            JTextArea text = new JTextArea();
            text.setEditable(false);
            text.setPreferredSize(new Dimension(400, 100));

            JTextField type = new JTextField();
            type.setEditable(false);
            type.setPreferredSize(new Dimension(200, 25));


            type.setText("Тип: " + correspondence.type().toString());

            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            this.add(type, constraints);

            switch (correspondence.type()) {
                case NONE -> {
                    text.setText("Сообщение:\n" + correspondence.message());

                    constraints.gridy = 1;
                    constraints.gridwidth = 2;
                    this.add(text, constraints);
                }

                case PHOTO_VIDEO, FILE -> {
                    text.setText("Сообщение:\n" + correspondence.message());
                    number.setText("Файл: " + correspondence.file().getName());

                    constraints.gridy = 1;
                    constraints.gridwidth = 1;
                    this.add(text, constraints);

                    constraints.gridy = 2;
                    this.add(number, constraints);
                }

                case CONTACT -> {
                    number.setText("Контакт: " + correspondence.message());

                    constraints.gridy = 1;
                    constraints.gridwidth = 1;
                    this.add(number, constraints);
                }
            }
        }
    }









    private static class SignInDialogClass extends JPanel {
        JDialog dialog;
        final JLabel qrCode;


        public SignInDialogClass(JDialog dialog, QueueWhatsAppWindow queueWhatsAppWindow) {
            this.dialog = dialog;

            this.setLayout(new GridBagLayout());

            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.BOTH;
            constraints.insets = new Insets(5, 5, 5, 5);

            JButton signInButton = new JButton("Вход выполнен");
            signInButton.addActionListener(e -> {
                Output.addMessage(new MessageToServer(
                        MessageTypeToServer.WHATSAPP_AUTHORIZED,
                        true,
                        true
                ));

                dialog.setVisible(false);
                queueWhatsAppWindow.setAuthorized(true);
            });

            JButton cancelButton = new JButton("Отмена");
            cancelButton.addActionListener(e -> dialog.setVisible(false));

            this.qrCode = new JLabel();
            this.qrCode.setPreferredSize(new Dimension(300, 300));

            JTextField signText = new JTextField("Выполнить вход:");
            signText.setEditable(false);


            // Размещение
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 3;
            constraints.gridheight = 1;
            this.add(signText, constraints);

            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 3;
            constraints.gridheight = 3;
            this.add(qrCode, constraints);

            constraints.gridx = 0;
            constraints.gridy = 4;
            constraints.gridwidth = 2;
            constraints.gridheight = 1;
            this.add(signInButton,constraints);

            constraints.gridx = 2;
            constraints.gridy = 4;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            this.add(cancelButton, constraints);
        }


        public synchronized void clearImage() {
            this.qrCode.setIcon(null);

            this.updateUI();
        }

        public synchronized void setImage(BufferedImage image) {
            this.qrCode.setIcon(null);
            this.qrCode.setIcon(new ImageIcon(image));
            this.updateUI();
        }
    }
}
