/**
 * Created by NikhilVerma on 02/10/15.
 */
public class SymbolParsingException extends Exception {
    private String expression;
    private int index;
    public SymbolParsingException(String expression,int index) {
        super("Exception during parsing expression at index"+expression+" at column index"+index);
        this.expression=expression;
        this.index=index;
    }

    public String getExpression() {
        return expression;
    }

    public int getIndex() {
        return index;
    }
}
