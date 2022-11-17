package interfaces.lexic;

import java.util.Stack;

public class Semantico implements Constants {
    public void executeAction(int action, Token token)	throws SemanticError {
    	String operador = "";
    	int codigo = 0;
    	Stack pilha_tipos = new Stack();
    	
        System.out.println("A��o #"+action+", Token: "+token);
    }	
}
