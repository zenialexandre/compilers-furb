package interfaces.lexic;

import java.util.Map;
import java.util.Stack;

@SuppressWarnings("serial")
public class Sintatico implements Constants, ParserConstants
{
    private Stack stack = new Stack();
    private Token currentToken;
    private Token previousToken;
    private Lexico scanner;
    private Semantico semanticAnalyser;
    
    /*public Map<String, String> syntaticExpressions = Map.of(
    			"<program>", 0,
    			"<lst_instrucoes>", 1,
    			"<lst_instrucoes_>", 2,
    			"<decl_const>", 3,
    			"<decl_constvar>", 4,
    		    "<decl_constvar_>", 5,
    		    "<decl_var>", 6,
    		    "<comando>", 7,
    		    "<lst_id>", 8,
    		    "<lst_id_>", 9,
    		    "<tipo>", 10,
    		    "<valor>", 11,
    		    "<expressao>", 12,
    		    "<lst_comandos>", 13,
    		    "<lst_comandos_>", 14,
    		    "<cmd_atrib>", 15,
    		    "<cmd_input>", 16,
    		    "<cmd_input_>", 17,
    		    "<cmd_output>", 18,
    		    "<cmd_select>", 19,
    		    "<cmd_select_>", 20,
    		    "<cmd_loop>", 21,
    		    "<lst_expressoes>", 22,
    		    "<lst_expressoes_>", 23,
    		    "<expressao_>", 24,
    		    "<elemento>", 25,
    		    "<relacional>", 26,
    		    "<relacional_>", 27,
    		    "<operador_relacional>", 28,
    		    "<aritmetica>", 29,
    		    "<aritmetica_>", 30,
    		    "<termo>", 31,
    		    "<termo_>", 32,
    		    "<fator>", 33
    		);*/

    private static final boolean isTerminal(int x)
    {
        return x < FIRST_NON_TERMINAL;
    }

    private static final boolean isNonTerminal(int x)
    {
        return x >= FIRST_NON_TERMINAL && x < FIRST_SEMANTIC_ACTION;
    }

    private static final boolean isSemanticAction(int x)
    {
        return x >= FIRST_SEMANTIC_ACTION;
    }

    private boolean step() throws LexicalError, SyntaticError, SemanticError
    {
        if (currentToken == null)
        {
            int pos = 0;
            if (previousToken != null)
                pos = previousToken.getPosition()+previousToken.getLexeme().length();

            currentToken = new Token(DOLLAR, "$", pos);
        }

        int x = ((Integer)stack.pop()).intValue();
        int a = currentToken.getId();

        if (x == EPSILON)
        {
            return false;
        }
        else if (isTerminal(x))
        {
            if (x == a)
            {
                if (stack.empty())
                    return true;
                else
                {
                    previousToken = currentToken;
                    currentToken = scanner.nextToken();
                    return false;
                }
            }
            else
            {
                throw new SyntaticError(PARSER_ERROR[x], currentToken.getPosition());
            }
        }
        else if (isNonTerminal(x))
        {
            if (pushProduction(x, a))
                return false;
            else
                throw new SyntaticError(PARSER_ERROR[x], currentToken.getPosition());
        }
        else // isSemanticAction(x)
        {
            semanticAnalyser.executeAction(x-FIRST_SEMANTIC_ACTION, previousToken);
            return false;
        }
    }

    private boolean pushProduction(int topStack, int tokenInput)
    {
        int p = PARSER_TABLE[topStack-FIRST_NON_TERMINAL][tokenInput-1];
        if (p >= 0)
        {
            int[] production = PRODUCTIONS[p];
            //empilha a produ��o em ordem reversa
            for (int i=production.length-1; i>=0; i--)
            {
                stack.push(new Integer(production[i]));
            }
            return true;
        }
        else
            return false;
    }

    public void parse(Lexico scanner, Semantico semanticAnalyser) throws LexicalError, SyntaticError, SemanticError
    {
        this.scanner = scanner;
        this.semanticAnalyser = semanticAnalyser;

        stack.clear();
        stack.push(new Integer(DOLLAR));
        stack.push(new Integer(START_SYMBOL));

        currentToken = scanner.nextToken();

        while ( ! step() )
            ;
    }
    
  public String getMsgFromParserTable(int tableLine) {
    	int[][] parserConstTable = ParserConstants.PARSER_TABLE;
    	String finalMsg = "";
    	
    	for (int i = 0; i < parserConstTable.length; i++) {
    		for (int j = 0; j < parserConstTable[0].length; j++) {
    			if (i == 0) {
    				if (parserConstTable[i][j] > 0) {
    					finalMsg += parserConstTable[i][j];
    				}
    			}
    		}
    	}
    	
    	
    	return finalMsg;
    }
}
