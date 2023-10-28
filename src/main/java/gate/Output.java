package gate;

import senderData.MessageToServer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class Output extends Thread {
    private static final List<MessageToServer> messagesQueue = new LinkedList<>();

    private final ObjectOutputStream os;
    private final ConnectionStart connection;

    public Output(OutputStream os, ConnectionStart connection) throws IOException {
        this.os = new ObjectOutputStream(os);
        this.connection = connection;
    }

    public static synchronized void addMessage(MessageToServer message) {
        messagesQueue.add(message);
        synchronized (messagesQueue) {
            messagesQueue.notifyAll();
        }
    }

    private synchronized MessageToServer getMessage() {
        return messagesQueue.remove(0);
    }

    private synchronized boolean isNewMessages() {
        return messagesQueue.size() > 0;
    }

    @Override
    public void run() {
        while (ConnectionStart.isConnected) {
            while (!isNewMessages()) {
                synchronized (messagesQueue) {
                    try {
                        messagesQueue.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }


            try {
                MessageToServer message = getMessage();
                if(message != null)
                    System.out.println(message);

                os.writeObject(message);
            } catch (IOException e) {
                connection.setConnectedFalse();
                try {
                    os.close();
                }
                catch (IOException ignored) {}
            }
        }

        try {
            os.close();
        }
        catch (IOException ignored) {}
    }
}
