package gate;

import database.Database;
import gui.GuiController;
import data.Buyer;
import data.Correspondence;
import senderData.MessageToClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.*;

public class Input extends Thread {
    private final ObjectInputStream is;
    private final ConnectionStart connection;
    private final GuiController controller;


    public Input(InputStream is, ConnectionStart connection, GuiController controller) throws IOException {
        this.is = new ObjectInputStream(is);
        this.connection = connection;
        this.controller = controller;
    }

    @Override
    public void run() {
        while (ConnectionStart.isConnected) {
            try {
                MessageToClient message = (MessageToClient) is.readObject();
                if(message != null)
                    System.out.println(message);

                switch (message.type()) {
                    case CLIENTS_LIST -> {
                        List<Buyer> buyerList = new LinkedList<> (Arrays.asList((Buyer[]) message.message()));
                        Database.setBuyersList(buyerList);

                        controller.updateGUIClientsList(buyerList);
                    }

                    case AUTHORIZATION -> {
                        boolean isAuthorize = (boolean) message.message();

                        controller.updateGUIAuthorization(isAuthorize);
                    }

                    case MANAGERS_LIST -> {
                        List<String> managersList = new LinkedList<> (Arrays.asList((String[]) message.message()));
                        Database.setManagersList(managersList);

                        controller.updateGUIManagersList(managersList);
                    }

                    case GMAIL_MESSAGE_IN_QUEUE -> {
                        byte[] mapBytes = (byte[]) message.message();

                        Map<Correspondence[], TreeSet<Buyer>> queueEmail = null;
                        try (ByteArrayInputStream bais = new ByteArrayInputStream(mapBytes)) {
                            ObjectInputStream ois = new ObjectInputStream(bais);
                            queueEmail = (Map<Correspondence[], TreeSet<Buyer>>) ois.readObject();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        controller.updateGUIGmailMessageInQueue(queueEmail);
                    }

                    case WHATSAPP_AUTHORIZATION -> {
                        byte[] imageBytes = (byte[]) message.message();

                        BufferedImage image = null;
                        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
                            image = ImageIO.read(bais);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        controller.updateGUIWhatsAppAuthorization(image);
                    }

                    case WHATSAPP_MESSAGE_IN_QUEUE -> {
                        byte[] mapBytes = (byte[]) message.message();

                        Map<Correspondence[], TreeSet<Buyer>> queueWhatsApp = null;
                        try (ByteArrayInputStream bais = new ByteArrayInputStream(mapBytes)) {
                            ObjectInputStream ois = new ObjectInputStream(bais);
                            queueWhatsApp = (Map<Correspondence[], TreeSet<Buyer>>) ois.readObject();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        controller.updateGUIWhatsAppMessageInQueue(queueWhatsApp);
                    }

                    case WHATSAPP_NEED_AUTHORIZATION -> {
                        controller.updateGUIWhatsAppIsAuthorize(false);
                    }

                    default -> connection.setConnectedFalse();
                }
            } catch (IOException | ClassNotFoundException e) {
                connection.setConnectedFalse();
                break;

            } catch (NullPointerException ignored) {}
        }
    }
}
