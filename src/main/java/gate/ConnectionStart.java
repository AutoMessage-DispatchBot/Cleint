package gate;

import gui.GuiController;

import java.io.IOException;
import java.net.Socket;

public class ConnectionStart extends Thread {
    private final GuiController controller;

    public ConnectionStart(GuiController controller) {
        this.controller = controller;
    }


    public static Boolean isConnected = false;

    public Input input;
    public Output output;

    @Override
    public synchronized void run() {
        try {
//            Socket socket = new Socket("92.63.177.69", 40500);
            Socket socket = new Socket("127.0.0.1", 40500);
            isConnected = true;

            output = new Output(socket.getOutputStream(), this);
            input = new Input(socket.getInputStream(), this, controller);

            output.start();
            input.start();

        } catch (IOException e) {
            setConnectedFalse();
        }
    }

    public synchronized void setConnectedFalse() {
        isConnected = false;
        controller.updateGUINoConnection();
    }
}
