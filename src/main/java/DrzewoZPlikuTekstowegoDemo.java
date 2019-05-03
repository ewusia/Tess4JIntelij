import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;

/**
 * @author engineervix
 */
public class DrzewoZPlikuTekstowegoDemo {

    private static DrzewoZPlikuTekstowego tr = new DrzewoZPlikuTekstowego();

    public static void main(String[] args) {
        JFrame frame = new JFrame("Demo | Creating JTree From File.txt");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container content = frame.getContentPane();

        JTree t = tr.getTree();

        content.add(new JScrollPane(t), BorderLayout.CENTER);
        frame.setSize(275, 300);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

    }
}