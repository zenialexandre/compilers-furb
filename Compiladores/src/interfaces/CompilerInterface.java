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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

//@SuppressWarnings("deprecation")
public class CompilerInterface {

	private JFrame frame;
	private JMenuBar toolsBar;
	private JTextPane editorPanel;
	private JTextArea editorArea;
	private TextArea messageTextArea;
	private JLabel statusBarLabel;
	private File currentFile;
	
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
		InputMap inputMap = toolsBar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		{
			JMenuItem newFileItem = new JMenuItem("Novo [ctrl + r]");
			Image newFileIcon = new ImageIcon(this.getClass().getResource("/new_file_icon.png")).getImage();
			toolsBar.add(newFileItem);
			newFileItem.setIcon(new ImageIcon(newFileIcon));
			this.clearClickAction(newFileItem);
			this.clearKeyboardAction(inputMap);
		}

		{
			JMenuItem openFileItem = new JMenuItem("Abrir [ctrl + o]");
			Image openFileIcon = new ImageIcon(this.getClass().getResource("/open_file_icon.png")).getImage();
			toolsBar.add(openFileItem);
			openFileItem.setIcon(new ImageIcon(openFileIcon));
			this.openClickAction(openFileItem);
			this.openKeyboardAction(inputMap);
		}

		{
			JMenuItem saveFileItem = new JMenuItem("Salvar [ctrl + s]");
			Image saveIcon = new ImageIcon(this.getClass().getResource("/save_icon.png")).getImage();
			toolsBar.add(saveFileItem);
			saveFileItem.setIcon(new ImageIcon(saveIcon));
			this.saveClickAction(saveFileItem);
			this.saveKeyboardAction(inputMap);
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
			this.compileClickAction(compileItem);
			this.compileKeyboardAction(inputMap);
		}

		{
			JMenuItem groupItem = new JMenuItem("Equipe [F1]");
			Image groupIcon = new ImageIcon(this.getClass().getResource("/group_icon.png")).getImage();
			toolsBar.add(groupItem);
			groupItem.setIcon(new ImageIcon(groupIcon));
			this.groupClickAction(groupItem);
			this.groupKeyboardAction(inputMap);
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
		//TextLineNumber textLineNumber = new TextLineNumber(editorPanel, 1);
		//textLineNumber.setCurrentLineForeground(Color.CYAN);
		JScrollPane scrollEditorPane = new JScrollPane(editorPanel);
		scrollEditorPane.setPreferredSize(new Dimension(900, 300));
		//scrollEditorPane.setRowHeaderView(textLineNumber);
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
	
	private void clearClickAction(JMenuItem newFileItem) {
		newFileItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				clearTextFields();
			}
		});
	}
	
	private void groupClickAction(JMenuItem groupItem) {
		groupItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				fillWithGroup();
			}
		});
	}
	
	private void saveClickAction(JMenuItem saveFileItem) {
		saveFileItem.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					saveFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
	private void compileClickAction(JMenuItem compileItem) {
		compileItem.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				compile();
			}
		}); 
	}

	private void openKeyboardAction(InputMap inputMap) {
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
	
	private void groupKeyboardAction(InputMap inputMap) {
		String groupKeyStroke = "F1";
		
		Action action = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				fillWithGroup();
			}
		};
		KeyStroke keyStroke = KeyStroke.getKeyStroke(groupKeyStroke);
		inputMap.put(keyStroke, groupKeyStroke);
		toolsBar.getActionMap().put(groupKeyStroke, action);
	}
	
	private void clearKeyboardAction (InputMap inputMap) {
		String clearKeyStroke = "ctrl R";
		Action action = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				clearTextFields();
			}
		};
		KeyStroke keyStroke = KeyStroke.getKeyStroke(clearKeyStroke);
		inputMap.put(keyStroke, clearKeyStroke);
		toolsBar.getActionMap().put(clearKeyStroke, action);
	}
	
	private void saveKeyboardAction(InputMap inputMap) {
		String saveKeyStroke = "ctrl S";
		Action action = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					saveFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
		KeyStroke keyStroke = KeyStroke.getKeyStroke(saveKeyStroke);
		inputMap.put(keyStroke, saveKeyStroke);
		toolsBar.getActionMap().put(saveKeyStroke, action);
	}
	
	private void compileKeyboardAction(InputMap inputMap) {
		String compileKeyStroke = "F7";
		Action action = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				compile();
			}
		};
		KeyStroke keyStroke = KeyStroke.getKeyStroke(compileKeyStroke);
		inputMap.put(keyStroke, compileKeyStroke);
		toolsBar.getActionMap().put(compileKeyStroke, action);
	}
	
	public void openFileExplorer() {
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter filterTxt = new FileNameExtensionFilter("TEXT FILES","txt", "text");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(filterTxt);
		
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			currentFile = fileChooser.getSelectedFile();
			this.fillStatusBar(currentFile);
			this.fillEditorPanel(currentFile);
			this.clearMessageArea();
		}
	}
	
	public void saveFile() throws IOException {
		if (currentFile == null) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			FileNameExtensionFilter filterTxt = new FileNameExtensionFilter("TEXT FILES","txt", "text");
			fileChooser.setFileFilter(filterTxt);
			
			if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
				currentFile = fileChooser.getSelectedFile();
				if (!currentFile.getName().endsWith(".txt")) {
					File renamedFile = new File(currentFile.getAbsolutePath() + ".txt");
					currentFile = renamedFile;
				}
				this.fillStatusBar(currentFile);
				this.clearMessageArea();
				PrintWriter out = new PrintWriter(currentFile);
				out.println(editorPanel.getText());
				out.close();
			};
		} else {
			this.clearMessageArea();
			PrintWriter out = new PrintWriter(currentFile);
			out.println(editorPanel.getText());
			out.close();
		}
	}
	
	public void compile() {
		this.messageTextArea.setText("compilação de programas ainda não foi implementada");
	}
	
	public void fillWithGroup() {
		messageTextArea.setText("Alexandre Zeni e Joshua Patrick Loesch Alves");
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
	
	private void clearTextFields() {
		currentFile = null;
		editorPanel.setText(null);
		messageTextArea.setText(null);
		statusBarLabel.setText(null);
	}
}
