package interfaces;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
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
		frame.setBounds(200, 200, 910, 600);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.createStatusBar(frame);
	}

	private void createStatusBar(JFrame frame) {
		String directoryName = this.getCurrentDirectory();
		JMenuBar statusBar = new JMenuBar();
		JMenu statusBarMenu = new JMenu();
		statusBarMenu.setLabel(directoryName);
		frame.setLayout(new BorderLayout());
		frame.add(statusBar, BorderLayout.SOUTH);
		statusBar.add(statusBarMenu);
	}
	
	private String getCurrentDirectory() {
		return this.getClass().getClassLoader().getResource("").getPath();
	}
}
