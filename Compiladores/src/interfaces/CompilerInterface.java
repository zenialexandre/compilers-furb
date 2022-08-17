package interfaces;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

//@SuppressWarnings("deprecation")
public class CompilerInterface {

	private JFrame frame;
	private JMenuBar toolsBar;
	private JTextPane editorPanel;
	private JTextArea editorArea;
	private TextArea messageTextArea;
	private JLabel statusBarLabel;
	
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
			this.openClickAction(openFileItem);
			this.openKeyboardAction();
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

	private JScrollPane createEditorArea() {
		editorPanel = new JTextPane();
		editorArea = new JTextArea();
		editorPanel.add(editorArea);
		TextLineNumber textLineNumber = new TextLineNumber(editorPanel, 1);
		textLineNumber.setCurrentLineForeground(Color.CYAN);
		JScrollPane scrollEditorPane = new JScrollPane(editorPanel);
		scrollEditorPane.setPreferredSize(new Dimension(900, 300));
		scrollEditorPane.setRowHeaderView(textLineNumber);
		scrollEditorPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollEditorPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		return scrollEditorPane;
	}

	private TextArea createMessageArea() {
		messageTextArea = new TextArea("...", 5, 10);
		messageTextArea.setEditable(false);
		return messageTextArea;
	}

	private void createStatusBar() {
		JPanel statusBarPanel = new JPanel();
		statusBarPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		frame.getContentPane().add(statusBarPanel, BorderLayout.SOUTH);
		statusBarLabel = new JLabel("...");
		statusBarLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusBarPanel.add(statusBarLabel);
		statusBarPanel.setPreferredSize(new Dimension(900, 25));
	}

	private void openClickAction(JMenuItem openFileItem) {
		openFileItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				openFileExplorer();
			}
		});
	}
	
	private void openKeyboardAction() {
		InputMap inputMap = toolsBar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		String openKeyStroke = "control O";
		
		Action action = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				openFileExplorer();	
			}
		};
		KeyStroke keyStroke = KeyStroke.getKeyStroke(openKeyStroke);
		inputMap.put(keyStroke, openKeyStroke);
		toolsBar.getActionMap().put(openKeyStroke, action);
	}
	
	public void openFileExplorer() {
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter filterTxt = new FileNameExtensionFilter("TEXT FILES","txt", "text");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(filterTxt);
		
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			this.fillStatusBar(selectedFile);
			this.fillEditorPanel(selectedFile);
			this.clearMessageArea();
		}
	}
	
	private void clearMessageArea() {
		messageTextArea.setText("");
	}
	
	private void clearEditorPanel() {
		editorPanel.setText("");
	}
	
	private void fillEditorPanel(File selectedFile) {
		try {
			this.clearEditorPanel();
			try (BufferedReader bufferReader = new BufferedReader(new FileReader(selectedFile))) {
				String lines = bufferReader.readLine();
				
				while (lines != null) {
					editorPanel.setText(editorPanel.getText() + lines + "\n");
					lines = bufferReader.readLine();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void fillStatusBar(File selectedFile) {
		statusBarLabel.setText(selectedFile.getParentFile().getName() + "\\" + selectedFile.getName());
	}
}
