package interfaces;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
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
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Utilities;

import interfaces.lexic.LexicalError;
import interfaces.lexic.Lexico;
import interfaces.lexic.Token;

import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

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
		frame.setBounds(500, 500, 1150, 600);
		frame.setMinimumSize(new Dimension(910, 600));
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.createToolsBar();
		this.createSplitPane();
		this.createStatusBar();
	}

	private void createToolsBar() {
		toolsBar = new JMenuBar();
		toolsBar.setMinimumSize(new Dimension(900, 70));
		frame.setJMenuBar(toolsBar);
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
			this.copyClickAction(copyItem);
			this.copyKeyboardAction(inputMap);
		}

		{
			JMenuItem pasteItem = new JMenuItem("Colar [ctrl + v]");
			Image pasteIcon = new ImageIcon(this.getClass().getResource("/paste_icon.png")).getImage();
			toolsBar.add(pasteItem);
			pasteItem.setIcon(new ImageIcon(pasteIcon));
			this.pasteClickAction(pasteItem);
			
		}

		{
			JMenuItem cutItem = new JMenuItem("Recortar [ctrl + x]");
			Image cutIcon= new ImageIcon(this.getClass().getResource("/cut_icon.png")).getImage();
			toolsBar.add(cutItem);
			cutItem.setIcon(new ImageIcon(cutIcon));
			this.cutClickAction(cutItem);
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
		TextLineNumber textLineNumber = new TextLineNumber(editorPanel, 1);
		textLineNumber.setCurrentLineForeground(Color.ORANGE);
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
		statusBarPanel.setMinimumSize(new Dimension(900, 25));
		statusBarLabel = new JLabel("...");
		statusBarLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusBarPanel.add(statusBarLabel);
		frame.getContentPane().add(statusBarPanel, BorderLayout.SOUTH);
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
				try {
					compile();
				} catch (BadLocationException err) {
					err.printStackTrace();
				}
			}
		}); 
	}
	
	private void copyClickAction(JMenuItem copyItem) {
		copyItem.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				copy();
			}
		});
	}
	
	private void pasteClickAction(JMenuItem pasteItem) {
		pasteItem.addMouseListener(new MouseAdapter() {
		
			@Override
			public void mouseClicked(MouseEvent e) {
				paste();
			}
		});
	}
	
	private void cutClickAction(JMenuItem cutItem) {
		cutItem.addMouseListener(new MouseAdapter() {
		
			@Override
			public void mouseClicked(MouseEvent e) {
				cut();
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
				try {
					compile();
				} catch (BadLocationException err) {
					err.printStackTrace();
				}
			}
		};
		KeyStroke keyStroke = KeyStroke.getKeyStroke(compileKeyStroke);
		inputMap.put(keyStroke, compileKeyStroke);
		toolsBar.getActionMap().put(compileKeyStroke, action);
	}
	
	private void copyKeyboardAction(InputMap inputMap) {
		String copyKeyStroke = "CTRL + c";
		Action action = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				copy();
			}
		};
		KeyStroke keyStroke = KeyStroke.getKeyStroke(copyKeyStroke);
		inputMap.put(keyStroke, copyKeyStroke);
		toolsBar.getActionMap().put(copyKeyStroke, action);
	}
	
	private void openFileExplorer() {
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
	
	private void saveFile() throws IOException {
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
				out.println(getEditorText());
				out.close();
			};
		} else {
			this.clearMessageArea();
			PrintWriter out = new PrintWriter(currentFile);
			out.println(getEditorText());
			out.close();
		}
	}
	
	private void compile() throws BadLocationException {
		this.clearMessageArea();
		Lexico lexic = new Lexico();
		lexic.setInput(getEditorText());
		String msg = "linha" + String.format("%12s", "classe") + String.format("%35s", "lexema\n");

		try {
			Token token = null;
			while ((token = lexic.nextToken()) != null) {
				msg += this.getLinePosition(token.getPosition()) + String.format("%25s", token.getIdClass()) + String.format("%28s", token.getLexeme()) + "\n";
				messageTextArea.setText(msg);
			}
			messageTextArea.setText(messageTextArea.getText() + "\nprograma compilado com sucesso");
		} catch (LexicalError err) {
			if (("simbolo invalido").equalsIgnoreCase(err.getMessage())) {
				messageTextArea.setText("Erro na linha " + this.getLinePosition(err.getPosition()) 
										+ " - " + this.getTextAtLine(err.getPosition()) + " " + err.getMessage());
			} else {
				messageTextArea.setText("Erro na linha " + this.getLinePosition(err.getPosition()) + " - " + err.getMessage());
			}
		}
	}
	
	private void copy() {
		String selectedText = editorPanel.getSelectedText();
		StringSelection strToClipboard = new StringSelection(selectedText);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		if (selectedText != null) clipboard.setContents(strToClipboard, null);
	}
	
	private void paste() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(null);
		if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				editorPanel.replaceSelection((String) contents.getTransferData(DataFlavor.stringFlavor));
			} catch (UnsupportedFlavorException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void cut() {
		copy();
		editorPanel.replaceSelection("");
	}
	
	private void fillWithGroup() {
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
					editorPanel.setText(getEditorText() + lines + "\n");
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
		this.clearEditorPanel();
		this.clearMessageArea();
		statusBarLabel.setText(null);
	}
	
	private int getLinePosition(int position) {
		editorPanel.setCaretPosition(position);
		Element root = editorPanel.getDocument().getDefaultRootElement();
		return root.getElementIndex(editorPanel.getCaretPosition()) + 1;
	}
	
	private String getTextAtLine(int position) throws BadLocationException {
		editorPanel.setCaretPosition(position);
		int start = Utilities.getWordStart(editorPanel, editorPanel.getCaretPosition());
		int end = Utilities.getWordEnd(editorPanel, editorPanel.getCaretPosition());
		return editorPanel.getText(start, end - start);
	}
	
	private String getEditorText() {
		return this.editorPanel.getText().replace("\r\n", "\n");
	}
}
