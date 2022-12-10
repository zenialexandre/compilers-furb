package interfaces.lexic;

import interfaces.lexic.SemanticError;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

public class Semantico implements Constants {
	private String operador = "";
	public String code = "";
	//int codigo = 0;
	private Stack<String> pilha_tipos = new Stack<>();
	private String tipo_var = "";
	private ArrayList<String> lista_id = new ArrayList<>();
	private Stack<String> pilha_rotulos = new Stack<>();
	private HashMap<String, String> tabela_simbolos = new HashMap<>();
	
    public void executeAction(int action, Token token)	throws SemanticError {  	
        System.out.println("Acao #"+action+", Token: "+ token);
        
        String lexeme = this.getLexemeFromToken(token);
		boolean isConstant = lexeme.contains("d");
        
        switch (action) {
        	case 1:
        		String actionUmTipoUm = this.pilha_tipos.pop();
        		String actionUmTipoDois = this.pilha_tipos.pop();
        		String[] actionUmTiposAceitos = new String[] {"int64", "float64"};
        		
        		if (!Arrays.asList(actionUmTiposAceitos).contains(actionUmTipoUm)
        				|| !Arrays.asList(actionUmTiposAceitos).contains(actionUmTipoDois)) {
        			throw new SemanticError("Erro na linha " + token.getPosition() + " - tipos incompatíveis em expressão aritmética");
        		}
        		
        		if (actionUmTipoUm.equals("float64") || actionUmTipoDois.equals("float64")) {
        			this.pilha_tipos.add("float64");
        		} else {
        			this.pilha_tipos.add("int64");
        		}
        		
        		code += "add\n";
        		
        		break;
        	case 2:
        		String actionDoisTipoUm = this.pilha_tipos.pop();
        		String actionDoisTipoDois = this.pilha_tipos.pop();
        		String[] actionDoisTiposAceitos = new String[] {"int64", "float64"};
        		
        		if (!Arrays.asList(actionDoisTiposAceitos).contains(actionDoisTipoUm)
        				|| !Arrays.asList(actionDoisTiposAceitos).contains(actionDoisTipoDois)) {
        			throw new SemanticError("Erro na linha " + token.getPosition() + " - tipos incompatíveis em expressão aritmética");
        		}
        		
        		if (actionDoisTipoUm.equals("float64") || actionDoisTipoDois.equals("float64")) {
        			this.pilha_tipos.add("float64");
        		} else {
        			this.pilha_tipos.add("int64");
        		}
        		
        		code += "sub\n";
        		break;
        	case 3:
        		String actionTresTipoUm = this.pilha_tipos.pop();
        		String actionTresTipoDois = this.pilha_tipos.pop();
        		String[] actionTresTiposAceitos = new String[] {"int64", "float64"};
        		
        		if (!Arrays.asList(actionTresTiposAceitos).contains(actionTresTipoUm)
        				|| !Arrays.asList(actionTresTiposAceitos).contains(actionTresTipoDois)) {
        			throw new SemanticError("Erro na linha " + token.getPosition() + " - tipos incompatíveis em expressão aritmética");
        		}
        		
        		if (actionTresTipoUm.equals("float64") || actionTresTipoDois.equals("float64")) {
        			this.pilha_tipos.add("float64");
        		} else {
        			this.pilha_tipos.add("int64");
        		}
        		
        		code += "mul\n";
        		break;
        	case 4:
        		String actionQuatroTipoUm = this.pilha_tipos.pop();
        		String actionQuatroTipoDois = this.pilha_tipos.pop();
        		String[] actionQuatroTiposAceitos = new String[] {"int64", "float64"};
        		
        		if (!Arrays.asList(actionQuatroTiposAceitos).contains(actionQuatroTipoUm)
        				|| !Arrays.asList(actionQuatroTiposAceitos).contains(actionQuatroTipoDois)) {
        			throw new SemanticError("Erro na linha " + token.getPosition() + " - tipos incompatíveis em expressão aritmética");
        		}
        		
        		if (actionQuatroTipoUm.equals(actionQuatroTipoDois)) {
        			this.pilha_tipos.add(actionQuatroTipoUm);
        		} else {
        			throw new SemanticError("Erro na linha " + token.getPosition() + " - tipos incompatíveis em expressão aritmética");
        		}
        		
        		code += "div\n";
        		
        		break;
        	case 5:
        		pilha_tipos.push("int64");
        		int valorTotal = 0;

        		if (isConstant) {
        			int a = Integer.parseInt(lexeme.substring(0, lexeme.indexOf("d")));
        			int b = Integer.parseInt(lexeme.substring(lexeme.indexOf("d") + 1));
        			
        			valorTotal =  a * (int) (Math.pow(10, b));
        		}
        		
        		code += "\n\tldc.i8 " + (isConstant ? valorTotal : lexeme) + "\n";
        		code += "\tconv.r8\n";
        		break;

        	case 6:
        		pilha_tipos.push("float64");
        		double valorTotalFloat = 0f;
        		
        		if (isConstant) {
        			double a = Double.parseDouble(lexeme.substring(0, lexeme.indexOf("d")));
        			double b = Double.parseDouble(lexeme.substring(lexeme.indexOf("d") + 1));
        			
        			valorTotalFloat =  a * (Math.pow(10, b));
        		}
        		
        		code += "\n\tldc.r8 " + (isConstant ? valorTotalFloat : lexeme) + "\n";        		
        		break;

        	case 7:
        		String actionSevenType = this.pilha_tipos.pop();
        		String[] actionSevenSupportedTypes = new String[] {"float64", "int64"};
        		if (Arrays.asList(actionSevenSupportedTypes).contains(actionSevenType)) {
        			this.pilha_tipos.add(actionSevenType);
        		} else {
        			throw new SemanticError("Erro na linha " + token.getPosition() + " - tipo(s) incompatível(is) em expressão aritmética");
        		}
        		
        		break;
        	case 8:
        		String actionEightType = this.pilha_tipos.pop();
        		String[] actionEightSupportedTypes = new String[] {"float64", "int64"};
        		if (Arrays.asList(actionEightSupportedTypes).contains(actionEightType)) {
        			this.pilha_tipos.add(actionEightType);
        		} else {
        			throw new SemanticError("Erro na linha " + token.getPosition() + " - tipo(s) incompatível(is) em expressão aritmética");
        		}
        		
        		code += "ldc.i8 -1\n";
        		code += "conv.r8\n";
        		code += "mul\n";
        		
        		break;
        	case 9:
        		this.operador = token.getLexeme();
        		break;

        	case 10:
        		
        		break;

        	case 11:
        		this.pilha_tipos.push("bool");
        		code += "\n\tldc.i4.1\n";
        		break;

        	case 12:
        		this.pilha_tipos.push("bool");
        		code += "\n\tldc.i4.0\n";
        		break;

        	case 13:
        		String typeFor13 = this.pilha_tipos.pop();
        		
        		if ("bool".equalsIgnoreCase(typeFor13)) {
        			this.pilha_tipos.push("bool");
        		} else {
        			throw new SemanticError("Erro na linha " + token.getPosition() + " - tipo(s) incompatível(is) em expressão lógica");
        		}
        		
        		code += "\n\tldc.i4.1\n";
        		code += "\n\txor\n";
        		break;

        	case 14:
        		// VERIFICAR
        		
        		String typeFor14 = this.pilha_tipos.pop();
        		
        		if ("int64".equalsIgnoreCase(typeFor14)) {
        			code += "\n\tconv.i8\n";
        		}
        		
        		code += ("\n\tcall void [mscorlib]System.Console::Write(" + ("char".equalsIgnoreCase(typeFor14) ? "string" : typeFor14) + ")\n");
        		break;

        	case 15:
        		code += ".assembly extern mscorlib {}\n"
        				+ ".assembly _codigo_objeto{}\n"
        				+ ".module _codigo_objeto.exe\n\n"
        				+ ".class public _UNICA{\n"
        				+ ".method static public void _principal() {\n"
        				+ "\t.entrypoint\n";
        		break;

        	case 16:
        		code += "\n\tret\n"
        				+ "\t}\n"
        				+ "}\n";
        		break;
        	case 17:
        		code += "ldstr \"\n\"\n";
        		code += "call void [mscorlib]System.Console::Write(string)\n";
        		break;
        	case 18:
        		String actionEighteenTypeOne = pilha_tipos.pop();
        		String actionEighteenTypeTwo = pilha_tipos.pop();
        		
        		if (!"bool".equalsIgnoreCase(actionEighteenTypeOne)
        				|| !"bool".equalsIgnoreCase(actionEighteenTypeTwo)) {
        			throw new SemanticError("Erro na linha " + token.getPosition() + " - tipo(s) incompatível(is) em expressão lógica");
        		}
        		
        		pilha_tipos.add("bool");
        		code += "and\n";
        		break;
        	case 19:
        		String actionNineteenTypeOne = pilha_tipos.pop();
        		String actionNineteenTypeTwo = pilha_tipos.pop();
        		
        		if (!"bool".equalsIgnoreCase(actionNineteenTypeOne)
        				|| !"bool".equalsIgnoreCase(actionNineteenTypeTwo)) {
        			throw new SemanticError("Erro na linha " + token.getPosition() + " - tipo(s) incompatível(is) em expressão lógica");
        		}
        		
        		pilha_tipos.add("bool");
        		code += "or\n";
        		break;
        	case 20:
        		String actionTwentyTypeOne = this.pilha_tipos.pop();
        		String actionTwentyTypeTwo= this.pilha_tipos.pop();
        		
        		if (!"int64".equalsIgnoreCase(actionTwentyTypeOne)
        				|| !"int64".equalsIgnoreCase(actionTwentyTypeTwo)) {
        			throw new SemanticError("Erro na linha " + token.getPosition() + " - tipos incompatíveis em expressão aritmética");
        		}
        		
        		pilha_tipos.add("int64");
        		
        		code += "div.un\n";
        		break;
        	case 21:
        		pilha_tipos.push("string");
        		
        		if ("\s".equalsIgnoreCase(lexeme)) {
        			lexeme = " ";
        		}
        		
        		code += "ldstr " + lexeme + "\n";
        		
        		break;
        	case 22:
        		pilha_tipos.push("string");
        		
        		code += "ldstr " + lexeme + "\n";
        		break;
        	case 23:
        		break;
        	case 24:
        		break;
        	case 25:
        		break;
        	case 30:
        		switch (lexeme) {
        		case "int":
        			tipo_var = "int64";
        			break;
        		case "real":
        			tipo_var = "float64";
        			break;
        		default:
        			tipo_var = "";
        			break;
        		}
        		
        		break;
        	case 31:
        		for (String id : lista_id) {
        			if (tabela_simbolos.containsKey(id)) {
        				throw new SemanticError("Erro na linha " + token.getPosition() + " - identificador repetido");
        			}
        			tabela_simbolos.put(id, tipo_var);
        			code += ".locals(" + tipo_var + " " + id + ")\n";
        		}
        		lista_id.clear();
        		break;
        	case 32: 
        		lista_id.add(lexeme);
        		break;
        	case 33:
        		String action33Id = lexeme;
        		if (!tabela_simbolos.containsKey(action33Id)) {
        			throw new SemanticError("Erro na linha " + token.getPosition() + " - identificador não encontrado");
        		}
        		String action33IdType = tabela_simbolos.get(action33Id);
        		pilha_tipos.add(action33IdType);
        		
        		code += "ldloc " + action33Id + "\n";
        		if ("int64".equalsIgnoreCase(action33IdType)) {
        			code += "conv.r8\n";
        		}
        		
        		break;
        	case 34:
        		String action34Id = lista_id.remove(lista_id.size() - 1);
        		if (!tabela_simbolos.containsKey(action34Id)) {
        			throw new SemanticError("Erro na linha " + token.getPosition() + " - identificador não encontrado");
        		}
        		String action34IdType = tabela_simbolos.get(action34Id);
        		String action34ExpType = pilha_tipos.pop();
        		if (!action34IdType.equals(action34ExpType)) {
        			throw new SemanticError("Erro na linha " + token.getPosition() + " - tipos de identificador e expressão diferentes");
        		}
        		if ("int64".equalsIgnoreCase(action34IdType)) {
        			code += "conv.i8\n";
        		}
        		
        		code += "stloc " + action34Id + "\n";
        		break;
        	case 35:
        		for (String id : lista_id) {
        			if (!tabela_simbolos.containsKey(id)) {
        				throw new SemanticError("Erro na linha " + token.getPosition() + " - identificador não encontrado");
        			}
        			
        			String action35IdType = tabela_simbolos.get(id);
        			String action35Class = "";
        			
        			switch (action35IdType) {
        			case "int64":
        				action35Class = "Int64";
        				break;
        			case "float64":
        				action35Class = "Double";
        				break;
        			default:
        				action35Class = "";
        				break;
        			}
        			
        			code += "call string [mscorlib]System.Console::ReadLine()\n";
        			code += "call " + action35IdType + " [mscorlib]System." + action35Class + "::Parse(string)\n";
        			code += "stloc " + id + "\n";
        		}
        		
        		lista_id.clear();
        		break;
        	default:
        		break;
        }
    }
    
    private String getLexemeFromToken(Token token) {
    	if (token != null) {
        	return token.getLexeme();	
        }
    	return "";
    }
}
