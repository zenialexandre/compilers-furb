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
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Utilities;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import interfaces.lexic.LexicalError;
import interfaces.lexic.Lexico;
import interfaces.lexic.ParserConstants;
import interfaces.lexic.SemanticError;
import interfaces.lexic.Semantico;
import interfaces.lexic.Sintatico;
import interfaces.lexic.SyntaticError;

import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

//@SuppressWarnings("deprecation")
@SuppressWarnings("serial")
public class CompilerInterface implements ParserConstants {

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
		this.createUndoManager();
		return scrollEditorPane;
	}
	
	private void createUndoManager() {
		final UndoManager undo = new UndoManager();
		
		editorPanel.getDocument().addUndoableEditListener(new UndoableEditListener() {
			@Override
			public void undoableEditHappened(UndoableEditEvent evt) {
				undo.addEdit(evt.getEdit());
			}
		});
		this.undoKeyboardAction(undo);
		this.redoKeyboardAction(undo);
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
			@Override
			public void actionPerformed(ActionEvent e) {
				copy();
			}
		};
		KeyStroke keyStroke = KeyStroke.getKeyStroke(copyKeyStroke);
		inputMap.put(keyStroke, copyKeyStroke);
		toolsBar.getActionMap().put(copyKeyStroke, action);
	}
	
	private void undoKeyboardAction(UndoManager undo) {		
		editorPanel.getActionMap().put("Undo", new AbstractAction("Undo") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (undo.canUndo()) {
						undo.undo();
					}
				} catch(CannotUndoException err) {
					err.getMessage();
				}
			}
		});
		editorPanel.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
	}
	
	private void redoKeyboardAction(UndoManager undo) {
		editorPanel.getActionMap().put("Redo", new AbstractAction("Redo") {
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					if (undo.canRedo()) {
						undo.redo();
					}
				} catch(CannotRedoException err) {
					err.getMessage();
				}
			}
		});
		editorPanel.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
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
		ArrayList<String> tableLines = new ArrayList<>(Arrays.asList(
				"0",
				"1",
				"2",
				"3",
				"4",
				"5",
				"6",
				"7",
				"8",
				"9",
				"10",
				"11",
				"13",
				"14",
				"15",
				"16",
				"17",
				"18",
				"19",
				"20",
				"21",
				"28"));

		Lexico lexic = new Lexico();
		Sintatico syntatic = new Sintatico();
		Semantico semantic = new Semantico();
		lexic.setInput(getEditorText());
		
		try {
			syntatic.parse(lexic, semantic);
			messageTextArea.setText("programa compilado com sucesso");
		} catch (LexicalError err) {
			if (("simbolo invalido").equalsIgnoreCase(err.getMessage())) {
				messageTextArea.setText("Erro na linha " + this.getLinePosition(err.getPosition()) 
										+ " - " + this.getTextAtLine(err.getPosition()) + " " + err.getMessage());
			} else {
				messageTextArea.setText("Erro na linha " + this.getLinePosition(err.getPosition()) + " - " + err.getMessage());
			}
		} catch (SyntaticError err) {
			if (tableLines.contains(err.getMessage())) {
				int errTableLine = Integer.parseInt(err.getMessage());
				messageTextArea.setText("Erro na linha " + this.getLinePosition(err.getPosition())
				+ " - encontrado " + this.getTextAtLine(err.getPosition()) + " - " + "esperado " + this.getMsgFromParserTable(errTableLine));
			} else {
				messageTextArea.setText("Erro na linha " + this.getLinePosition(err.getPosition()) 
				+ " - encontrado " + this.getTextAtLine(err.getPosition()) + " - " + err.getMessage());
			}
		} catch (SemanticError err) {
			// Trata erros semanticos.
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
	
	private void clearMessageArea() {
		messageTextArea.setText("");
	}

	private void clearEditorPanel() {
		editorPanel.setText("");
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
	
	private String getMsgFromParserTable(int tableLine) {
    	int[][] parserConstTable = ParserConstants.PARSER_TABLE;
    	var wrapper = new Object() { String columnMsg = ""; };
    	
    	IntStream.range(tableLine, parserConstTable[tableLine].length).forEach(columnIndex -> {
    		if (!(parserConstTable[tableLine][columnIndex] < 0)) {
    			wrapper.columnMsg += " " + this.createSyntaticErrMsg(columnIndex) + " ";
    		}
    	});
    	return wrapper.columnMsg;
    }

    private String createSyntaticErrMsg(int columnIndex) {
    	String msg = "";
    	
    	if (columnIndex == 0) {
			msg += "EOF";
		} else if (columnIndex == 1) {
			msg += "id";
		} else if (columnIndex == 2) {
			msg += "intw";
		} else if (columnIndex == 3) {
			msg += "flt"; 
		} else if (columnIndex == 4) {
			msg += "chr";
		} else if (columnIndex == 5) {
			msg += "str";
		} else if (columnIndex == 6) {
			msg += "boolean";
		} else if (columnIndex == 7) {
			msg += "break";
		} else if (columnIndex == 8) {
			msg += "char";
		} else if (columnIndex == 9) {
			msg += "do";
		} else if (columnIndex == 10) {
			msg += "else";
		} else if (columnIndex == 11) {
			msg += "end";
		} else if (columnIndex == 12) {
			msg += "false";
		} else if (columnIndex == 13) {
			msg += "float";
		} else if (columnIndex == 14) {
			msg += "fun";
		} else if (columnIndex == 15) {
			msg += "if";
		} else if (columnIndex == 16) {
			msg += "int";
		} else if (columnIndex == 17) {
			msg += "main";
		} else if (columnIndex == 18) {
			msg += "print";
		} else if (columnIndex == 19) {
			msg += "println";
		} else if (columnIndex == 20) {
			msg += "readln";
		} else if (columnIndex == 21) {
			msg += "string";
		} else if (columnIndex == 22) {
			msg += "true";
		} else if (columnIndex == 23) {
			msg += "val";
		} else if (columnIndex == 24) {
			msg += "var";
		} else if (columnIndex == 25) {
			msg += "while";
		} else if (columnIndex == 26) {
			msg += ":";
		} else if (columnIndex == 27) {
			msg += ",";
		} else if (columnIndex == 28) {
			msg += ";";
		} else if (columnIndex == 29) {
			msg += "=";
		} else if (columnIndex == 30) {
			msg += ")";
		} else if (columnIndex == 31) {
			msg += "(";
		} else if (columnIndex == 32) {
			msg += "}";
		} else if (columnIndex == 33) {
			msg += "{";
		} else if (columnIndex == 34) {
			msg += "==";
		} else if (columnIndex == 35) {
			msg += "!=";
		} else if (columnIndex == 36) {
			msg += "<";
		} else if (columnIndex == 37) {
			msg += ">";
		} else if (columnIndex == 38) {
			msg += "+";
		} else if (columnIndex == 39) {
			msg += "-";
		} else if (columnIndex == 40) {
			msg += "*";
		} else if (columnIndex == 41) {
			msg += "/";
		} else if (columnIndex == 42) {
			msg += "%";
		} else if (columnIndex == 43) {
			msg += "&&";
		} else if (columnIndex == 44) {
			msg += "||";
		} else if (columnIndex == 45) {
			msg += "!";
		}
    	return msg;
    }
}
