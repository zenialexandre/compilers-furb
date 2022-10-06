package interfaces.lexic;

import java.util.ArrayList;

public class Token
{
    private int id;
    private String lexeme;
    private int position;
    
    public Token(int id, String lexeme, int position)
    {
        this.id = id;
        this.lexeme = lexeme;
        this.position = position;
    }

    public final int getId()
    {
        return id;
    }
    
    public final String getIdClass() {
    	String classMsg = "";
    	int id = this.getId();
    	ArrayList<Integer> reservedWordsIds = new ArrayList<>();
    	ArrayList<Integer> specialSymbolsIds = new ArrayList<>();
    	
    	for (int i = 7; i <= 26; i++) reservedWordsIds.add(i);
    	for (int i = 27; i <= 48; i++) specialSymbolsIds.add(i);
    	if (id == 2) {
    		classMsg += "identificador";
    	} else if (id == 3) {
    		classMsg += "constante int";
    	} else if (id == 4) {
    		classMsg += "constante float";
    	} else if (id == 5) {
    		classMsg += "constante char";
    	} else if (id == 6) {
    		classMsg += "constante string";
    	} else if (reservedWordsIds.contains(id)) {
    		classMsg += "palavra reservada";
    	} else if (specialSymbolsIds.contains(id)) {
    		classMsg += "simbolo especial";
    	}
    	return classMsg;
    }

    public final String getLexeme()
    {
        return lexeme;
    }

    public final int getPosition()
    {
        return position;
    }

    public String toString()
    {
        return id+" ( "+lexeme+" ) @ "+position;
    };
}