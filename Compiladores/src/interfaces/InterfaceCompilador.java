package interfaces;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JMenuItem;

@SuppressWarnings("deprecation")
public class InterfaceCompilador {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InterfaceCompilador window = new InterfaceCompilador();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public InterfaceCompilador() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(500, 500, 910, 600);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.createStatusBar(frame);
		this.createToolsBar(frame);
	}

	private void createStatusBar(JFrame frame) {
		String directoryName = this.getCurrentDirectory();
		JMenuBar statusBar = new JMenuBar();
		JMenu statusBarMenu = new JMenu();
		statusBarMenu.setLabel(directoryName);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
		statusBar.add(statusBarMenu);
		statusBar.setPreferredSize(new Dimension(900, 25));
	}

	private void createToolsBar(JFrame frame) {
		JMenuBar toolsBar = new JMenuBar();
		frame.setJMenuBar(toolsBar);
		toolsBar.setPreferredSize(new Dimension(900, 75));
		this.createToolsBarItems(toolsBar);
	}

	private void createToolsBarItems(JMenuBar toolsBar) {
		JMenuItem newFile = new JMenuItem("Abrir [ctrl + n]");
		newFile.setHorizontalAlignment(SwingConstants.LEFT);
		Image newFileIcon = new ImageIcon(this.getClass().getResource("/new_file_icon.png")).getImage();
		toolsBar.add(newFile);
		newFile.setIcon(new ImageIcon(newFileIcon));
		newFile.setPreferredSize(new Dimension(10, 10));
	}

	private String getCurrentDirectory() {
		return this.getClass().getClassLoader().getResource("").getPath();
	}
}
