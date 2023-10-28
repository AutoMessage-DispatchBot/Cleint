package gui.windows;

import gate.Output;
import data.Buyer;
import data.Correspondence;
import senderData.MessageToServer;
import senderData.MessageTypeToServer;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.TreeSet;

public class QueueGmailWindow extends JPanel {
    private final GridBagConstraints constraints;
    private final JPanel messagesList;
    private final SeeBuyersForMessageClass seeBuyers;
    private final JDialog dialog;


    public QueueGmailWindow() {
        this.messagesList = new JPanel(new GridBagLayout());
        this.setLayout(new GridBagLayout());


        this.dialog = new JDialog((JFrame) null, "Клиенты в очереди", true);
        this.dialog.setLayout(null);
        this.dialog.setSize(1500, 1000);

        this.seeBuyers = new SeeBuyersForMessageClass(this.dialog, SeeBuyersForMessageClass.QUEUE_EMAIL);
        dialog.setContentPane(seeBuyers);

        // Добавление всего
        this.constraints = new GridBagConstraints();
        this.constraints.fill = GridBagConstraints.BOTH;
        this.constraints.insets = new Insets(5, 5, 5, 5);

//        this.add(messagesList, BorderLayout.CENTER);
    }

    public synchronized void setQueue(Map<Correspondence[], TreeSet<Buyer>> queue) {
        for(int i = 0; i < this.getComponentCount(); i++)
            this.remove(i);

        constraints.gridwidth = 1;
        constraints.gridheight = 1;

        for(Correspondence[] key: queue.keySet()) {
            constraints.gridx = this.getComponentCount() % 3;
            constraints.gridy = this.getComponentCount() / 3;

            this.add(new SeeMessageClass(key, queue.get(key), this, seeBuyers, dialog), constraints);
        }

        messagesList.updateUI();
        this.updateUI();
    }






    private static class SeeMessageClass extends JPanel {
        public SeeMessageClass(
                Correspondence[] correspondences,
                TreeSet<Buyer> buyers,
                JPanel messagesList,
                SeeBuyersForMessageClass seeBuyers,
                JDialog dialog) {


            this.setLayout(new GridBagLayout());

            JTextField title = new JTextField();
            title.setEditable(false);
            title.setPreferredSize(new Dimension(200, 25));

            JTextArea text = new JTextArea();
            text.setEditable(false);
            text.setPreferredSize(new Dimension(400, 200));

            JTextArea files = new JTextArea();
            files.setEditable(false);
            files.setPreferredSize(new Dimension(400, 50));


            // messages panel
            JPanel message = new JPanel(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.BOTH;
            constraints.insets = new Insets(5, 5, 5, 5);

            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 2;
            constraints.gridheight = 1;
            message.add(title, constraints);

            constraints.gridy = 1;
            message.add(text, constraints);

            constraints.gridy = 2;
            message.add(files, constraints);


            // set Text
            title.setText("Заголовок: " + correspondences[0].message());
            text.setText("Текст:\n" + correspondences[1].message());
            if(correspondences.length > 2) {
                StringBuilder sb = new StringBuilder();
                for(int i = 2; i < correspondences.length; i++)
                    sb.append(correspondences[i].file().getName()).append(", ");

                files.setText("Файлы:\n" + sb);
            }


            // buttons
            JButton clearAll = new JButton("Отменить все");
            JButton seeAll = new JButton("Посмотреть получателей");


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
                            MessageTypeToServer.GMAIL_MESSAGE_CANCEL,
                            correspondences,
                            buyers.toArray(new Buyer[0])
                    ));
                    messagesList.remove(this);
                    messagesList.updateUI();
                }
            });



            // add to this
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;

            this.add(message, constraints);


            constraints.gridy = 1;
            this.add(seeAll, constraints);

            constraints.gridy = 2;
            this.add(clearAll, constraints);

        }
    }
}
