package interfaces;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.TextArea;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

//@SuppressWarnings("deprecation")
public class CompilerInterface {

	private JFrame frame;
	private JMenuBar toolsBar;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CompilerInterface window = new CompilerInterface();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public CompilerInterface() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame("Compilador");
		frame.setBounds(500, 500, 1100, 600);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.createToolsBar();
		this.createSplitPane();
		this.createStatusBar();
	}

	private void createToolsBar() {
		toolsBar = new JMenuBar();
		frame.setJMenuBar(toolsBar);
		toolsBar.setPreferredSize(new Dimension(900, 75));
		this.createToolsBarItems();
	}

	private void createToolsBarItems() {
		{
			JMenuItem newFileItem = new JMenuItem("Novo [ctrl + r]");
			Image newFileIcon = new ImageIcon(this.getClass().getResource("/new_file_icon.png")).getImage();
			toolsBar.add(newFileItem);
			newFileItem.setIcon(new ImageIcon(newFileIcon));
		}

		{
			JMenuItem openFileItem = new JMenuItem("Abrir [ctrl + o]");
			Image openFileIcon = new ImageIcon(this.getClass().getResource("/open_file_icon.png")).getImage();
			toolsBar.add(openFileItem);
			openFileItem.setIcon(new ImageIcon(openFileIcon));
		}

		{
			JMenuItem saveFileItem = new JMenuItem("Salvar [ctrl + s]");
			Image saveIcon = new ImageIcon(this.getClass().getResource("/save_icon.png")).getImage();
			toolsBar.add(saveFileItem);
			saveFileItem.setIcon(new ImageIcon(saveIcon));
		}

		{
			JMenuItem copyItem = new JMenuItem("Copiar [ctrl + c]");
			Image copyIcon = new ImageIcon(this.getClass().getResource("/copy_icon.png")).getImage();
			toolsBar.add(copyItem);
			copyItem.setIcon(new ImageIcon(copyIcon));
		}

		{
			JMenuItem pasteItem = new JMenuItem("Colar [ctrl + v]");
			Image pasteIcon = new ImageIcon(this.getClass().getResource("/paste_icon.png")).getImage();
			toolsBar.add(pasteItem);
			pasteItem.setIcon(new ImageIcon(pasteIcon));
		}

		{
			JMenuItem cutItem = new JMenuItem("Recortar [ctrl + x]");
			Image cutIcon= new ImageIcon(this.getClass().getResource("/cut_icon.png")).getImage();
			toolsBar.add(cutItem);
			cutItem.setIcon(new ImageIcon(cutIcon));
		}

		{
			JMenuItem compileItem = new JMenuItem("Compilar [F7]");
			Image compíleIcon = new ImageIcon(this.getClass().getResource("/compile_icon.png")).getImage();
			toolsBar.add(compileItem);
			compileItem.setIcon(new ImageIcon(compíleIcon));
		}

		{
			JMenuItem groupItem = new JMenuItem("Equipe [F1]");
			Image groupIcon = new ImageIcon(this.getClass().getResource("/group_icon.png")).getImage();
			toolsBar.add(groupItem);
			groupItem.setIcon(new ImageIcon(groupIcon));
		}
	}
	
	private void createSplitPane() {
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.createEditorArea(), this.createMessageArea());
		splitPane.setOneTouchExpandable(true);
		splitPane.setContinuousLayout(true);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
	}

	private JTextArea createEditorArea() {
		JTextArea editorArea = new JTextArea();
		JScrollPane scrollEditorPane = new JScrollPane(editorArea);
		TextLineNumber textLineNumber = new TextLineNumber(editorArea);
		editorArea.setPreferredSize(new Dimension(900, 300));
		scrollEditorPane.setRowHeaderView(textLineNumber);
		//TextArea editorTextArea = new TextArea(20, 10);
		return editorArea;
	}

	private TextArea createMessageArea() {
		TextArea messageTextArea = new TextArea("Aqui iriam as mensagens", 5, 10);
		messageTextArea.setEditable(false);
		return messageTextArea;
	}

	private void createStatusBar() {
		JPanel statusBarPanel = new JPanel();
		statusBarPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		frame.getContentPane().add(statusBarPanel, BorderLayout.SOUTH);
		JLabel statusBarLabel = new JLabel("aqui iria o caminho do file");
		statusBarLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusBarPanel.add(statusBarLabel);
		statusBarPanel.setPreferredSize(new Dimension(900, 25));
	}

	/*private String getCurrentDirectory() {
		return this.getClass().getClassLoader().getResource("").getPath();
	}*/
}