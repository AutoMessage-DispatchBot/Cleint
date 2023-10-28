package gui;

import gui.windows.*;
import gate.Output;
import data.Buyer;
import data.Correspondence;
import senderData.MessageToServer;
import senderData.MessageTypeToServer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class GuiController extends JFrame {
    public static final int LOGIN_WINDOW = 0;
    public static final int MAIN_WINDOW = 1;
    public static final int SET_ICON = 100;
    
    // Окно входа
    private final LoginWindow loginWindow;

    // Окно выбора клиентов
    private ClientWindow clientWindow;

    // Окно очереди почты/вотсап
    private QueueGmailWindow queueEmailWindow;
    private QueueWhatsAppWindow queueWhatsAppWindow;

    // Верхнее меню
    private JTabbedPane menu;

    private final ImageIcon icon;

    public GuiController() {
        icon = new ImageIcon("icon.jpg");

        loginWindow = new LoginWindow(icon);

        this.setTitle("Рассылка сообщений");

        this.setIconImage(icon.getImage());
        this.setLayout(new BorderLayout());

        this.setSize(1080, 720);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public synchronized void setWindow(int window) {
        switch (window) {
            case LOGIN_WINDOW -> {
                removeAllComponents();

                this.add(loginWindow, BorderLayout.CENTER);

                loginWindow.repaint();
                this.revalidate();
            }

            case MAIN_WINDOW -> {
                removeAllComponents();

                this.add(menu, BorderLayout.CENTER);

                this.revalidate();

                Output.addMessage(new MessageToServer(
                        MessageTypeToServer.GIVE_CLIENT_LIST_WITH_FILTER,
                        null,
                        null
                ));

                Output.addMessage(new MessageToServer(
                        MessageTypeToServer.GIVE_MANAGERS_LIST,
                        null,
                        null
                ));

                Output.addMessage(new MessageToServer(
                        MessageTypeToServer.GMAIL_MESSAGE_LIST,
                        null,
                        null
                ));

                Output.addMessage(new MessageToServer(
                        MessageTypeToServer.WHATSAPP_MESSAGE_LIST,
                        null,
                        null
                ));
            }

            case SET_ICON -> {
                removeAllComponents();

                this.add(new JLabel(icon));

                this.revalidate();
            }
        }
    }

    public synchronized void updateGUIClientsList(List<Buyer> buyerList) {
        clientWindow.setNewData(buyerList);
    }

    public synchronized void updateGUIManagersList(List<String> managers) {
        clientWindow.updateManagersList(managers);
    }

    public synchronized void updateGUIAuthorization(boolean isAuthorized) {
        if(isAuthorized) {
            clientWindow = new ClientWindow();
            queueEmailWindow = new QueueGmailWindow();
            queueWhatsAppWindow = new QueueWhatsAppWindow();

            menu = new JTabbedPane();
            menu.addTab("Рассылка", clientWindow);
            menu.addTab("Очередь e-mail", queueEmailWindow);
            menu.addTab("Очередь WhatsApp", queueWhatsAppWindow);

            menu.addChangeListener(e -> {
                int selectedIndex = menu.getSelectedIndex();

                switch (selectedIndex) {
                    case 0 -> {
                        Output.addMessage(new MessageToServer(
                                MessageTypeToServer.GIVE_CLIENT_LIST_WITH_FILTER,
                                null,
                                null
                        ));

                        Output.addMessage(new MessageToServer(
                                MessageTypeToServer.GIVE_MANAGERS_LIST,
                                null,
                                null
                        ));
                    }

                    case 1 -> Output.addMessage(new MessageToServer(
                            MessageTypeToServer.GMAIL_MESSAGE_LIST,
                            null,
                            null
                    ));

                    case 2 -> Output.addMessage(new MessageToServer(
                            MessageTypeToServer.WHATSAPP_MESSAGE_LIST,
                            null,
                            null
                    ));
                }
            });

            this.setWindow(GuiController.MAIN_WINDOW);

        } else {
            this.setWindow(GuiController.LOGIN_WINDOW);

            loginWindow.setNotAuthorized();
        }
    }

    public synchronized void updateGUINoConnection() {
        this.setWindow(GuiController.LOGIN_WINDOW);

        loginWindow.setNotConnection();
    }

    public synchronized void updateGUIGmailMessageInQueue(Map<Correspondence[], TreeSet<Buyer>> queue) {
        queueEmailWindow.setQueue(queue);
    }

    public synchronized void updateGUIWhatsAppMessageInQueue(Map<Correspondence[], TreeSet<Buyer>> queue) {
        queueWhatsAppWindow.setQueue(queue);
    }

    public synchronized void updateGUIWhatsAppAuthorization(BufferedImage image) {
        queueWhatsAppWindow.setQrCode(image);
    }

    public synchronized void updateGUIWhatsAppIsAuthorize(boolean isAuthorized) {
        queueWhatsAppWindow.setAuthorized(isAuthorized);
    }

    private synchronized void removeAllComponents() {
        Component[] components = this.getContentPane().getComponents();

        for (Component component : components) {
            this.getContentPane().remove(component);
        }
    }
}
