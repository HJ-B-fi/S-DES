import ui.SDESGUI;
import javax.swing.SwingUtilities;

/**
 * S-DES加解密程序主入口
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SDESGUI gui = new SDESGUI();
            gui.setVisible(true);
        });
    }
}