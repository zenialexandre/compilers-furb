package interfaces.lexic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Semantico implements Constants {
	private String operador = "";
	public String code = "";
	//int codigo = 0;
	private Stack pilha_tipos = new Stack();
	private Stack tipo_var = new Stack();
	private String lista_id = "";
	private Stack pilha_rotulos = new Stack();
	private HashMap<String, String> tabela_simbolos = new HashMap<>();
	
    public void executeAction(int action, Token token)	throws SemanticError {  	
        System.out.println("Acao #"+action+", Token: "+ token);
        String lexeme = "";
        if (token != null) {
        	lexeme = token.getLexeme();	
        }
        
		boolean isConstant = lexeme.contains("d");

        
        switch (action) {
        	case 1:
        		break;
        	case 2:
        		break;
        	case 3:
        		break;
        	case 4:
        		break;
        	case 5:
        		pilha_tipos.add("int64");
        		int valorTotal = 0;

        		if (isConstant) {
        			int a = Integer.parseInt(lexeme.substring(0, lexeme.indexOf("d")));
        			int b = Integer.parseInt(lexeme.substring(lexeme.indexOf("d") + 1));
        			
        			valorTotal =  a * (int) (Math.pow(10, b));
        		}
        		
        		code += "\n\tldc.i8 " + (isConstant ? valorTotal : lexeme) + "\n";
        		code += "\tconv.r8";
        		break;
        	case 6:
        		pilha_tipos.add("float64");
        		double valorTotalFloat = 0f;
        		
        		if (isConstant) {
        			double a = Double.parseDouble(lexeme.substring(0, lexeme.indexOf("d")));
        			double b = Double.parseDouble(lexeme.substring(lexeme.indexOf("d") + 1));
        			
        			valorTotalFloat =  a * (Math.pow(10, b));
        		}
        		
        		code += "\n\tldc.r8 " + (isConstant ? valorTotalFloat : lexeme);        		
        		break;
        	case 7:
        		break;
        	case 8:
        		break;
        	case 9:
        		break;
        	case 10:
        		break;
        	case 11:
        		break;
        	case 12:
        		break;
        	case 13:
        		break;
        	case 14:
        		break;
        	case 15:
        		code += ".assembly extern mscorlib {}\n"
        				+ ".assembly _codigo_objeto{}\n"
        				+ ".module _codigo_objeto.exe\n\n"
        				+ ".class public _UNICA{\n"
        				+ ".method static public void _principal() {\n"
        				+ "\t.entrypoint";
        		break;
        	case 16:
        		code += "\n\tret\n"
        				+ "\t}\n"
        				+ "}";
        		break;
        	case 17:
        		break;
        	case 18:
        		break;
        	case 19:
        		break;
        	case 20:
        		break;
        	case 21:
        		break;
        	case 22:
        		break;
        	case 23:
        		break;
        	case 24:
        		break;
        	case 25:
        		break;
        	default:
        		break;
        }
    }
}
