import gui.GuiController;
import gate.ConnectionStart;

public class Main {
    public static void main(String[] args) {
        GuiController controller = new GuiController();
        controller.setWindow(GuiController.LOGIN_WINDOW);

        new ConnectionStart(controller).start();
    }
}
