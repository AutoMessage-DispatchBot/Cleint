package gui.windows;

import gate.Output;
import senderData.MessageToServer;
import senderData.MessageTypeToServer;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JPanel {
    private final JPasswordField passwordField;
    private final JButton ok;
    private final JButton exit;
    private final ImageIcon brickIcon;

    private final JTextField notAuthorizedOrConnection;

    public LoginWindow(ImageIcon brickIcon) {
        this.passwordField = new JPasswordField(20);

        this.exit = new JButton("Exit");
        this.ok = new JButton("OK");
        this.brickIcon = brickIcon;
        this.notAuthorizedOrConnection = new JTextField();
        this.notAuthorizedOrConnection.setEditable(false);

        this.setLayout(new GridBagLayout());
        this.setOpaque(false); // Прозрачный фон панели

        // Добавляем поле для ввода пароля
        GridBagConstraints gbcPasswordField = new GridBagConstraints();
        gbcPasswordField.gridx = 0;
        gbcPasswordField.gridy = 0;
        gbcPasswordField.insets = new Insets(10, 10, 10, 10);
        gbcPasswordField.anchor = GridBagConstraints.CENTER;
        gbcPasswordField.fill = GridBagConstraints.HORIZONTAL;
        this.add(passwordField, gbcPasswordField);

        // Добавляем кнопку "OK"
        GridBagConstraints gbcOk = new GridBagConstraints();
        gbcOk.gridx = 0;
        gbcOk.gridy = 1;
        gbcOk.insets = new Insets(0, -168, 0, 0);
        gbcOk.anchor = GridBagConstraints.CENTER;
        this.add(ok, gbcOk);

        // Добавляем кнопку "Exit"
        GridBagConstraints gbcExit = new GridBagConstraints();
        gbcExit.gridx = 1;
        gbcExit.gridy = 1;
        gbcExit.insets = new Insets(0, -75, 0, 0);
        gbcExit.anchor = GridBagConstraints.CENTER;
        this.add(exit, gbcExit);

        // Обработчик для кнопки "OK"
        ok.addActionListener(e -> {
            String s = new String(passwordField.getPassword());
            if(s.length() > 0)
                Output.addMessage(new MessageToServer(MessageTypeToServer.AUTHORIZATION_PASSWORD, s, null));

            passwordField.setText("");
        });

        // Обработчик для кнопки "Exit"
        exit.addActionListener(e -> System.exit(0));
    }

    public synchronized void setNotAuthorized() {
        notAuthorizedOrConnection.setText("Введен неправильный пароль!");

        setNotAuthorizedOrConnection();
        this.updateUI();
    }

    public synchronized void setNotConnection() {
        notAuthorizedOrConnection.setText("Сервер недоступен. Свяжитесь с администратором.");

        setNotAuthorizedOrConnection();

        this.remove(passwordField);
        this.remove(ok);

        this.updateUI();
    }

    private synchronized void setNotAuthorizedOrConnection() {
        GridBagConstraints authorized = new GridBagConstraints();
        authorized.gridx = 0;
        authorized.gridy = 1;
        authorized.insets = new Insets(0, 0, -50, 0);
        authorized.anchor = GridBagConstraints.CENTER;
        this.add(notAuthorizedOrConnection, authorized);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (brickIcon != null) {
            int x = (getWidth() - brickIcon.getIconWidth()) / 2;
            int y = (getHeight() - brickIcon.getIconHeight()) / 2;
            brickIcon.paintIcon(this, g, x, y);
        }
    }
}
