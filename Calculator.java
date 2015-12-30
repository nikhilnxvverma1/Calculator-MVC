/**
 * Calculator : supports all type of operators: arithmetic, boolean and relational ,
 * with customizable precedence
 *
 * @author Nikhil Verma
 * @version 1.1: Calculator.java
 *          Revisions:
 *          Revision Removed dependency on Hashmaps 09/06/2015 1:18 PM Nikhil Verma
 *          Initial revision
 */
public class Calculator {

    public static void main(String[] args) {

        try {
            //create a test symbol list
            Operand sampleSymbolList = getSampleSymbolList();


            System.out.println("Simple Expression with default precedence");
            new ExpressionEvaluator("-1-2*-3.6").evaluate();//uses default precedence

//        System.out.println("Simple Expression with default precedence");
//        new ExpressionEvaluator("-3%2").evaluate();//supports negative numbers too

            System.out.println("Longer Expression including power operator with default precedence");
            new ExpressionEvaluator("-1+2*3^3").evaluate();//uses default precedence

            System.out.println("Simple Expression with custom precedence");
            MyMap customPrecedence= Calculator.getDefaultPrecedence();
            customPrecedence.put("/", 1);
            customPrecedence.put("*", 2);
            customPrecedence.put("%", 3);
            customPrecedence.put("-", 4);
            customPrecedence.put("+", 5);
            new ExpressionEvaluator("1+2*3",customPrecedence).evaluate();//make sure you supply custom precedence through constructor

            System.out.println("Expression with (possibly nested)brackets (default precedence)");
            new ExpressionEvaluator("((1+20)*3)+4/5").evaluate(true);

            System.out.println("Expression with binary operators (default precedence)");
            new ExpressionEvaluator("((1<20)>=3)==4<5").evaluate(true);

            System.out.println("Expression with (possibly nested)brackets and binary operators (default precedence)");
            new ExpressionEvaluator("10*(7+5*(67-8)+(50-53))<=5").evaluate();

            System.out.println("Expression with relational operators (default precedence)");
            new ExpressionEvaluator("((1<20)||(4>5))&&4<5").evaluate(true);

            System.out.println("Expression with relational, binary and arithmetic operators with nested brackets (default precedence)");
            new ExpressionEvaluator("((1<20)||(4>5))&&4<5&&(4+5)<(4*2)").evaluate(true);

            System.out.println("Add custom precedence to any of the above expressions as needed");

//        new ExpressionEvaluator(sampleSymbolList).evaluate(true);//uses raw symbol list
        } catch (SymbolParsingException e) {
            e.printStackTrace();
        }


    }

    public static MyMap getDefaultPrecedence(){
        MyMap defaultPrecedence=new MyMap();
        defaultPrecedence.put("+", 1);
        defaultPrecedence.put("-", 2);
        defaultPrecedence.put("%", 3);
        defaultPrecedence.put("*", 4);
        defaultPrecedence.put("/", 5);
        defaultPrecedence.put("^", 6);
        defaultPrecedence.put(" ++", 7);
        defaultPrecedence.put(" --", 7);
        defaultPrecedence.put("++ ", 7);
        defaultPrecedence.put("-- ", 7);

        defaultPrecedence.put("<", 9);
        defaultPrecedence.put("<=", 9);
        defaultPrecedence.put(">", 9);
        defaultPrecedence.put(">=", 9);
        defaultPrecedence.put("==", 9);
        defaultPrecedence.put("and", -10);
        defaultPrecedence.put("And", -10);
        defaultPrecedence.put("AND", -10);
        defaultPrecedence.put("&&", -10);
        defaultPrecedence.put("&", -10);
        defaultPrecedence.put("or", -10);
        defaultPrecedence.put("Or", -10);
        defaultPrecedence.put("OR", -10);
        defaultPrecedence.put("||", -10);
        defaultPrecedence.put("|", -10);
        defaultPrecedence.put("not", -10);
        defaultPrecedence.put("Not", -10);
        defaultPrecedence.put("NOT", -10);
        defaultPrecedence.put("!", -10);
        return defaultPrecedence;
    }

    private static Operand getSampleSymbolList() {
        // 10+20*3/4-5
        Operand ten=new Operand(10);
        Operator plus=new Operator("+");
        Operand twenty=new Operand(20);
        Operator into=new Operator("*");
        Operand three=new Operand(3);
        Operator by=new Operator("/");
        Operand four=new Operand(4);
        Operator minus=new Operator("-");
        Operand five=new Operand(5);

        //make a doubly linked list of the above
        ten.next = plus;
        plus.next = twenty;
        twenty.next = into;
        into.next = three;
        three.next = by;
        by.next = four;
        four.next = minus;
        minus.next = five;
        five.next = null;

        ten.previous = null;
        plus.previous = ten;
        twenty.previous = plus;
        into.previous = twenty;
        three.previous = into;
        by.previous = three;
        four.previous = by;
        minus.previous = four;
        five.previous = minus;
        return ten;
    }

    public double evaluateExpression(String expression) throws SymbolParsingException {
//        throw new SymbolParsingException(expression,0);
        return new ExpressionEvaluator(expression).evaluate(false);
    }
}

/**
 * Parser that takes in a string and parses it into a doubly linked list of symbols
 */
class SymbolParser{

    String expression;

    public SymbolParser(String expression) {
        this.expression = expression;
    }

    /**
     * parses the string
     * @return a doubly linked list of symbols
     */
    public Symbol parse() throws SymbolParsingException {//TODO exception thrown for incorrect grammer
        Symbol symbolStart  =null;
        char[] array=expression.toCharArray();
        Symbol lastSymbol=null;
        StringBuilder lastToken=new StringBuilder();
        SymbolType lastTokenSymbolType=null;
        boolean firstSymbolCharecter=true;
        boolean possiblyNegativeNumber=false;//on encountering negative charecter, make sure to pass this information around
        for (int i = 0; i < array.length; i++) {

            //ignore whitespaces
            if(array[i]==' '){
                continue;
            }

            //for first token infer the token explicitly
            if(lastTokenSymbolType==null){
                lastTokenSymbolType=inferSymbolType(array[i]);
                //first symbol might be a negative number(explicit check)
                if(array[i]=='-'){
                    lastTokenSymbolType= SymbolType.OPERAND;
                    possiblyNegativeNumber=true;
                }
                firstSymbolCharecter=true;
            }else{
                firstSymbolCharecter=false;
            }


            boolean newTokenRequired=addToTokenIfPossible(lastToken, lastTokenSymbolType, array[i], possiblyNegativeNumber);
            if((newTokenRequired)&&
                    (!(firstSymbolCharecter&&array[i]=='('))){//avoid repeating bracket if its the first symbol

                Symbol currentSymbol = createSymbol(lastToken, lastTokenSymbolType);

                //append the currently working symbol in list which is now finalized
                symbolStart = append(symbolStart, lastSymbol, currentSymbol);
                lastSymbol=currentSymbol;

                //for the new token now being made ,infer the token type
                lastToken=new StringBuilder(array[i]+"");

                //be mindful of negative numbers
                if(array[i]=='-'){
                    //an opening bracket might begin with a negative number(explicit check)
                    if(i>0&&array[i-1]=='('){
                        lastTokenSymbolType= SymbolType.OPERAND;
                        possiblyNegativeNumber=true;
                    }
                    //if last symbol was already an operator
                    //this must be a negative number
                    else if (lastTokenSymbolType== SymbolType.OPERATOR){
                        lastTokenSymbolType= SymbolType.OPERAND;
                        possiblyNegativeNumber=true;
                    }else{
                        lastTokenSymbolType= SymbolType.OPERATOR;
                        possiblyNegativeNumber=false;
                    }
                }else{
                    lastTokenSymbolType=inferSymbolType(array[i]);
                    possiblyNegativeNumber=false;
                }

            }

        }
        //for the last token ,create the symbol explicitly
        Symbol leftOutFromLoop=createSymbol(lastToken,lastTokenSymbolType);
        symbolStart = append(symbolStart, lastSymbol, leftOutFromLoop);
        return symbolStart;
    }

    private Symbol createSymbol(StringBuilder token, SymbolType symbolType) {
        Symbol currentSymbol=null;
        switch (symbolType){

            case OPERAND:
                currentSymbol=new Operand(Double.parseDouble(token.toString()));
                break;
            case OPERATOR:
                currentSymbol=new Operator(token.toString());
                break;
            case OPENING_BRACKET:
                currentSymbol=new BracketOpen();
                break;
            case CLOSING_BRACKET:
                currentSymbol=new BracketClose();
                break;
        }
        return currentSymbol;
    }

    private Symbol append(Symbol firstSymbol, Symbol lastSymbol, Symbol thisSymbol) {
        //append in list
        if(lastSymbol==null){
            firstSymbol=thisSymbol;
        }else{
            lastSymbol.next=thisSymbol;
            thisSymbol.previous=lastSymbol;
        }
        return firstSymbol;
    }

    private boolean addToTokenIfPossible(StringBuilder token, SymbolType tokenType, char a, boolean possiblyNegativeNumber){
        boolean newTokenRequired=false;
        if(Character.isDigit(a)||a=='.'||a=='-'){
            if(tokenType== SymbolType.OPERAND){
                //add to an existing number already being made
                if(a=='.'){
                    //make sure no decimal already exists
                    if(token.indexOf(a+"")==-1){
                        token.append(a);
                    }//else silently ignore(grammer is not being checked)
                }else if (a=='-'){
                    if(possiblyNegativeNumber&&token.length()==0) {//if incoming letter is '-' and the token is empty ,append it
                        token.append(a);
                    }else{
                        newTokenRequired=true;//otherwise it indicates that its an actual negative operator
                    }
                } else{
                    token.append(a);
                }
            }else{
                newTokenRequired=true;
            }
        }else if(a=='('){
            newTokenRequired=true;
        }else if(a==')'){
            newTokenRequired=true;
        }else{//assumed to be operator
            if(tokenType== SymbolType.OPERATOR){
                //a negative number should not be mixed with the last operator
                if(a=='-'){
                    if(token.length()>0&&token.indexOf("-")==-1){//if token for operator is there and - sign doesn't exist
                        newTokenRequired=true;//then a new token is required for a negative number
                    }else{
                        //allow single - operator and ,decrement operators
                        token.append(a);
                    }
                }else{
                    token.append(a);
                }
            }else{
                newTokenRequired=true;
            }
        }

        return newTokenRequired;
    }

    private SymbolType inferSymbolType(char a){
        SymbolType symbolType=null;

        if(Character.isDigit(a)||a=='.'){
            symbolType= SymbolType.OPERAND;
        }else if(a=='('){
            symbolType= SymbolType.OPENING_BRACKET;
        }else if(a==')'){
            symbolType= SymbolType.CLOSING_BRACKET;
        }else{//assumed to be operator
            symbolType= SymbolType.OPERATOR;
        }
        return symbolType;
    }


}

/**
 * Main class used to house the state and execution of an expression string
 * Note that this class does not check grammer
 */
class ExpressionEvaluator{
    /** currently known left operand relevant to the best operator found so far*/
    Operand leftOperand=null;
    /** currently known best operator found so far basis the precedence map */
    Operator bestSoFar=null;
    /** currently known right operand relevant to the best operator found so far*/
    Operand rightOperand=null;

    /** stores the last operand scanned so far*/
    Operand lastOperandScanned=null;

    /** head of the douly linked list of symbols*/
    Symbol symbolStart;

    /** precedence map that is used while evaluation this expression */
    MyMap precedence;

    /**
     * creates a Expression Evaluator for a string using the default precedence
     * @param expression the expression string
     */
    public ExpressionEvaluator(String expression) throws SymbolParsingException {
        this(expression, Calculator.getDefaultPrecedence());
    }

    /**
     * creates a Expression Evaluator for a string using a custom precedence
     * @param expression the expression string
     */

    public ExpressionEvaluator(String expression,MyMap precedence) throws SymbolParsingException {
        //parse the expression and create a symbol list
        this.symbolStart=new SymbolParser(expression).parse();
        this.precedence = precedence;
    }

    /**
     * creates a Expression Evaluator using a raw doubly linked list of symbols,default precedence
     * @param symbolStart the head of the doubly linked list of symbols
     */
    public ExpressionEvaluator(Symbol symbolStart){
        this(symbolStart, Calculator.getDefaultPrecedence());
    }

    /**
     * creates a Expression Evaluator using a raw doubly linked list of symbols,custom precedence
     * @param symbolStart the head of the doubly linked list of symbols
     * @param precedence a custom precedence
     */
    public ExpressionEvaluator(Symbol symbolStart,MyMap precedence) {
        this.symbolStart = symbolStart;
        this.precedence = precedence;
    }

    /**
     * evaluates the expression showing each step
     * @return the result of the expression
     */
    public double evaluate(){
        return evaluate(true);
    }

    /**
     * evaluates the expression
     * @param printEachStep if true prints each step along the way
     * @return the result of the expression
     */
    public double evaluate(boolean printEachStep){
        int sizeOfList=0;
        Symbol t;
        //start scanning the symbol list
        do{

            //print the entire list if needed.negative operands are shown in brackets
            if(printEachStep){
                printSymbolList();
            }

            //reset the state for a fresh iteration of the symbol list
            bestSoFar=null;
            leftOperand=null;
            rightOperand=null;
            lastOperandScanned=null;

            //starting traversing the symbol list
            t=symbolStart;
            boolean alreadyExecuted=true;
            while(t!=null){
                //let the symbol process itself using the current state of the expression evaluation
                alreadyExecuted=t.process(this);
                //break in case an operator already executed itself.(possible in case a closing bracket is encountered)
                if(alreadyExecuted){
                    break;
                }
                t=t.next;
            }

            //if an execution has'nt been made(read: no closing brackets encountered), execute the operator here
            if(bestSoFar!=null&&!alreadyExecuted){
                bestSoFar.execute(leftOperand, rightOperand,this);
            }

            //keep going till the size of the symbol list is less reaches 1
            sizeOfList=countSizeOfList();

        }while(sizeOfList>1);
        //print the last step
        printSymbolList();
        System.out.println("");
        System.out.println("");//new line for the next time

        //at this time size of the symbol list should be down to 1 operand
        double result=((Operand)symbolStart).value;
        return result;
    }

    /**
     * prints the symbol list
     */
    private void printSymbolList() {
        Symbol t;
        t=symbolStart;
        System.out.println("");//just to add newline
        while(t!=null){
            System.out.print(t.toString()+" ");
            t=t.next;
        }
    }

    /**
     * @return counts and returns the size of the symbol list
     */
    private int countSizeOfList(){
        int size=0;
        Symbol t=symbolStart;
        while(t!=null){
            size++;
            t=t.next;
        }
        return size;
    }
}

/**
 * Fours types of symbols exist
 */
enum SymbolType{
    OPERAND,
    OPERATOR,
    OPENING_BRACKET,
    CLOSING_BRACKET

}

/**
 * Symbol are individual elements that makes up an expression  such as, operators,operands and brackets
 */
abstract class Symbol{
    protected Symbol next;
    protected Symbol previous;
    protected Reduce reduce;//TODO remove if unused

    /**
     * while iterating through the symbol list, this method is called
     * proceses itself as a symbol using the current state of the expression
     * @param state the state of the evaluation, this state will change during execution
     * @return true indicates that an execution has taken place(only applicable for operators)
     */
    abstract public boolean process(ExpressionEvaluator state);

    /**
     * @return type of symbol
     */
    abstract SymbolType getType();
}

/**
 * Operands comprise of a value that can be negative
 * (Negative operands are shown in brackets)
 */
class Operand extends Symbol {
    double value;

    public Operand(double value) {
        this.value = value;
    }

    @Override
    public boolean process(ExpressionEvaluator state) {
        if(state.bestSoFar==null){
            state.leftOperand=this;
        }else if(state.rightOperand==null){
            state.rightOperand=this;
        }
        state.lastOperandScanned=this;
        return false;
    }

    @Override
    SymbolType getType() {
        return SymbolType.OPERAND;
    }

    @Override
    public String toString() {
        if(value>=0){
            return value+"";
        }else{
            return "("+value+")";//show negative numbers in brackets
        }
    }
}

/**
 *
 *
 */
class Operator extends Symbol {
    /**Operators comprise of a notation("+,-,*,/,^ etc..) and an operation strategy.*/
    String notation;
    /**Operation strategy makes it very easy(and extensible) to support any type of operations */
    Operation operationStrategy;
    /** On execution of a this operator,a reduce strategy reduces the size of the symbol list
     * based on the type of operator: binary operator, postfix operator , prefix operator */
    Reduce reduceStrategy;

    /**
     * finds the correct operation strategy to use for the supplied string argument
     * @param operatorNotation operator in string like "+,-,/,*
     * @return corresponding operation strategy
     */
    public static Operation getOperationForOperator(String operatorNotation){
        Operation operation=null;
        switch (operatorNotation){
            case "+":
                operation=new AddOperation();
                break;
            case "-":
                operation=new SubtractOperation();
                break;
            case "*":
                operation=new MultiplyOperation();
                break;
            case "/":
                operation=new DivideOperation();
                break;
            case "%":
                operation=new RemainderOperation();
                break;
            case "^":
                operation=new PowerOperation();
                break;
            case "<":
                operation=new LessThanOperation();
                break;
            case "<=":
                operation=new LessThanEqualToOperation();
                break;
            case ">":
                operation=new GreaterThanOperation();
                break;
            case ">=":
                operation=new GreaterThanEqualToOperation();
                break;
            case "==":
                operation=new EqualToOperation();
                break;
            case "not":
            case "Not":
            case "NOT":
            case "!":
                operation=new NotOperation();
                break;
            case "and":
            case "And":
            case "AND":
            case "&&":
            case "&":
                operation=new AndOperation();
                break;
            case "or":
            case "Or":
            case "OR":
            case "||":
            case "|":
                operation=new OrOperation();
                break;
            case " ++":
                operation=new PrefixIncrementOperation();
                break;
            case " --":
                operation=new PrefixDecrementOperation();
                break;
            case "++ ":
                operation=new PostfixIncrementOperation();
                break;
            case "-- ":
                operation=new PostfixDecrementOperation();
                break;
            default:
                //unrecognized

        }
        return operation;
    }

    /**
     * finds the correct reduce strategy based on the type of operator
     * @param operatorNotation operator in string like "+,-,/,*
     * @return the correct operation startegy
     */
    public static Reduce getReduceStrategyFor(String operatorNotation){
        Reduce reduce = null;
        switch (operatorNotation){

            //reduce strategy for PREFIX operators including "NOT"
            case " ++":
            case " --":
            case "not":
            case "Not":
            case "NOT":
            case "!":
                reduce=new PrefixOperationReduce();
                break;

            //reduce strategy for POSTFIX operators including "NOT"
            case "++ ":
            case "-- ":
                reduce=new PostfixOperationReduce();
                break;

            //reduce strategy for BINARY operators including "NOT"
            case "+":
            case "-":
            case "*":
            case "/":
            case "%":
            case "^":
            case "<":
            case "<=":
            case ">":
            case ">=":
            case "==":
            case "and":
            case "And":
            case "AND":
            case "&&":
            case "&":
            case "or":
            case "Or":
            case "OR":
            case "||":
            case "|":
//                reduce=new BinaryOperationStandAloneReduce();
                reduce=new BinaryOperationReduce();
                break;

            default:
                //unrecoginzed

        }
        return reduce;
    }


    public Operator(String notation) {
        this(notation, getOperationForOperator(notation),getReduceStrategyFor(notation));
    }

    public Operator(String notation, Operation operationStrategy) {
        this(notation, operationStrategy,getReduceStrategyFor(notation));
    }

    public Operator(String notation, Operation operationStrategy, Reduce reduceStrategy) {
        this.notation = notation;
        this.operationStrategy = operationStrategy;
        this.reduceStrategy = reduceStrategy;
    }

    @Override
    public String toString() {
        return notation;
    }

    @Override
    public boolean process(ExpressionEvaluator state) {
        if(state.bestSoFar==null){
            state.bestSoFar=this;
        }else{
            //check precedence using expression evaluator
            int precedenceOfThis=state.precedence.get(notation);
            int precedenceOfBest=state.precedence.get(state.bestSoFar.notation);
            if(precedenceOfThis>precedenceOfBest){
                state.bestSoFar=this;
                state.leftOperand=state.lastOperandScanned;//this becomes the "relevant" left operand for this operation now
                state.rightOperand=null;//start looking for right operand again,going forward
            }
        }
        return false;
    }

    /**
     * using the operation strategy ,finds the result for the supplied operands
     * @param left the left operand
     * @param right the right operand
     * @param state the state of the expresion evaluator
     * @return the result in a new Operand
     */
    public Operand execute(Operand left, Operand right, ExpressionEvaluator state){
        Operand result= operationStrategy.execute(left,right);
        Symbol newStart= reduceStrategy.reduce(this,result,state.symbolStart);
        state.symbolStart=newStart;
        return result;
    }

    @Override
    SymbolType getType() {
        return SymbolType.OPERATOR;
    }

    /**
     * uses the reduce strategy to reduce the symbol list.
     * for ex: if "this" operator is "+" reduction will
     * replace ...4+3... with ...7...
     * @param replaceWith the symbol replacing this operator
     * @param start the current start of the symbol list
     * @return the new start of the list of symbols in case it changes
     */
    public Symbol reduce(Operand replaceWith, Symbol start){
        return  reduceStrategy.reduce(this,replaceWith,start);
    }
}

/**
 * Opening brackets which resets the best operator "so far" to execute
 */
class BracketOpen extends Symbol {
    @Override
    public String toString() {
        return "(";
    }

    @Override
    public boolean process(ExpressionEvaluator state) {
        //reset the best so far
        state.bestSoFar=null;//start afresh when the bracket begins
        state.rightOperand=null;
        return false;
    }

    @Override
    SymbolType getType() {
        return SymbolType.OPENING_BRACKET;
    }
}

/**
 * Closing brackets indicate an enclosing expression.
 * on encountering this symbol, the best operator so far is executed
 */
class BracketClose extends Symbol {
    @Override
    public String toString() {
        return ")";
    }

    @Override
    public boolean process(ExpressionEvaluator state) {
        if(state.bestSoFar!=null){
            Operand result=state.bestSoFar.execute(state.leftOperand, state.rightOperand, state);
            //after executing the bracket last operand becomes the result
            state.lastOperandScanned=result;//does not make any difference,the main loop breaks
            return true;
        }
        return false;
    }

    @Override
    SymbolType getType() {
        return SymbolType.CLOSING_BRACKET;
    }
}

/**
 * Reduce interface is used to reduce the size of the
 * symbol list.
 * Having a strategy for reduction allows us to
 * support several types of operators at once:
 * Binary operators,
 * Prefix operators,
 * Postfix operators
 *
 */
interface Reduce {
    public Symbol reduce(Symbol origin, Symbol replaceWith, Symbol start);
}

/**
 * Base class that provides part of the functionality to reduce the
 * list
 */
abstract class ReduceList implements Reduce {

    public abstract Symbol reduce(Symbol origin, Symbol replaceWith, Symbol start);

    protected Symbol replaceSublist(Symbol sublistStart, Symbol sublistEnd, Symbol replaceWith, Symbol originalStart){
        Symbol revisedStart=originalStart;
        if(sublistStart.previous==null){//sublistStart is first node
            revisedStart=replaceWith;
            if(sublistEnd.next==null){//sublistEnd is last node
                revisedStart.next=null;
                revisedStart.previous=null;
            }else{                      //sublistEnd is in the middle
                sublistEnd.next.previous=revisedStart;
                revisedStart.next=sublistEnd.next;
            }
        }else{//sublistStart is in the middle first node
            if(sublistEnd.next==null){//sublistEnd is last node
                sublistStart.previous.next=replaceWith;
                replaceWith.previous=sublistStart.previous;
                replaceWith.next=null;
            }else{                      //sublistEnd is in the middle
                sublistStart.previous.next=replaceWith;
                replaceWith.previous=sublistStart.previous;
                replaceWith.next=sublistEnd.next;
                sublistEnd.next.previous=replaceWith;
            }
        }
        return revisedStart;
    }
}

/**
 * Binary Operators make use of this strategy which deletes
 * operand nodes from both before and after from the current operator node.
 * also taking care of brackets if encountered.
 * So 53+54-(34/17)+5 becomes
 * 53+54-2+5
 */
class BinaryOperationReduce extends ReduceList {

    @Override
    public Symbol reduce(Symbol origin, Symbol replaceWith, Symbol start) {

        if(origin==null){
            return null;
        }

        Symbol fromBehind=origin;
        Symbol t=origin;

        do{
            fromBehind=t;
            t=t.previous;
        }while(t!=null&&//keep going back
                (fromBehind.getType()!= SymbolType.OPERAND||t.getType()== SymbolType.OPENING_BRACKET)&&//break if brackets dont exist before
                fromBehind.getType()!= SymbolType.OPENING_BRACKET&&//break after finding the first bracket
                (t.getType()== SymbolType.OPERAND||t.getType()== SymbolType.OPENING_BRACKET));//go back till you see operands and brackets

        Symbol fromAhead=origin;
        t=origin;

        do{
            fromAhead=t;
            t=t.next;
        }while(t!=null&&//keep going forward
                (fromAhead.getType()!= SymbolType.OPERAND||t.getType()== SymbolType.CLOSING_BRACKET)&&//break if brackets dont exist after
                fromAhead.getType()!= SymbolType.CLOSING_BRACKET&&//break after finding the first operand or bracket
                (t.getType()== SymbolType.OPERAND||t.getType()== SymbolType.CLOSING_BRACKET));//go forward till you see operands and brackets


        //check for cases like (3*4+4) ,where the number of operands may be more than 2
        //if sublist end is a closing bracket, ensure sublist start is also an opening bracket
        if(fromAhead.getType()== SymbolType.CLOSING_BRACKET){
            //if its not revise sublist end
            if(fromBehind.getType()!= SymbolType.OPENING_BRACKET){
                fromAhead=fromAhead.previous;//go back one node
            }
        }
        //similarly for sublist start
        if (fromBehind.getType() == SymbolType.OPENING_BRACKET) {
            if (fromAhead.getType() != SymbolType.CLOSING_BRACKET) {
                fromBehind=fromBehind.next;
            }
        }

        return replaceSublist(fromBehind,fromAhead,replaceWith,start);
    }
}

/**
 * Prefix Operators make use of this strategy which deletes
 * operand nodes from only after from the current operator node.
 * also taking care of brackets if encountered.
 * So 53+54-(++17)+5 becomes
 * 53+54-18+5
 */
class PrefixOperationReduce extends ReduceList {

    @Override
    public Symbol reduce(Symbol origin, Symbol replaceWith, Symbol start) {
        if(origin==null){
            return null;
        }

        Symbol fromBehind=origin;
        Symbol t=origin;

        do{
            fromBehind=t;
            t=t.previous;
        }while(t!=null&&//keep going back
                (t.getType()== SymbolType.OPENING_BRACKET));//go back till you see the first opening brackets

        Symbol fromAhead=origin;
        t=origin;

        do{
            fromAhead=t;
            t=t.next;
        }while(t!=null&&//keep going forward
                (t.getType()== SymbolType.OPERAND||t.getType()== SymbolType.CLOSING_BRACKET));//go forward till you see operands and brackets

        //check for cases like (3*4+4) ,where the number of operands may be more than 2
        //if sublist end is a closing bracket, ensure sublist start is also an opening bracket
        if(fromAhead.getType()== SymbolType.CLOSING_BRACKET){
            //if its not revise sublist end
            if(fromBehind.getType()!= SymbolType.OPENING_BRACKET){
                fromAhead=fromAhead.previous;//go back one node
            }
        }
        //similarly for sublist start
        if (fromBehind.getType() == SymbolType.OPENING_BRACKET) {
            if (fromAhead.getType() != SymbolType.CLOSING_BRACKET) {
                fromBehind=fromBehind.next;
            }
        }
        return replaceSublist(fromBehind,fromAhead,replaceWith,start);

    }
}

/**
 * Postfix Operators make use of this strategy which deletes
 * operand nodes from only before from the current operator node.
 * also taking care of brackets if encountered.
 * So 53+54-(17++)+5 becomes
 * 53+54-18+5
 */
class PostfixOperationReduce extends ReduceList {

    @Override
    public Symbol reduce(Symbol origin, Symbol replaceWith, Symbol start) {

        if(origin==null){
            return null;
        }

        Symbol fromBehind=origin;
        Symbol t=origin;

        do{
            fromBehind=t;
            t=t.previous;
        }while(t!=null&&//keep going back
                (t.getType()== SymbolType.OPERAND||t.getType()== SymbolType.OPENING_BRACKET));//go back till you see operands and brackets

        Symbol fromAhead=origin;
        t=origin;

        do{
            fromAhead=t;
            t=t.next;
        }while(t!=null&&//keep going forward
                (t.getType()== SymbolType.CLOSING_BRACKET));//go forward till you see operands and brackets

        //check for cases like (3*4+4) ,where the number of operands may be more than 2
        //if sublist end is a closing bracket, ensure sublist start is also an opening bracket
        if(fromAhead.getType()== SymbolType.CLOSING_BRACKET){
            //if its not revise sublist end
            if(fromBehind.getType()!= SymbolType.OPENING_BRACKET){
                fromAhead=fromAhead.previous;//go back one node
            }
        }
        //similarly for sublist start
        if (fromBehind.getType() == SymbolType.OPENING_BRACKET) {
            if (fromAhead.getType() != SymbolType.CLOSING_BRACKET) {
                fromBehind=fromBehind.next;
            }
        }
        return replaceSublist(fromBehind,fromAhead,replaceWith,start);
    }
}

//Operations implementations

/**
 * A simple functional interface that allows an operator node
 * to support any kind of operation. Making it very easy to
 * provide any type of operation (even custom ones!)
 */
interface Operation{
    public Operand execute(Operand left, Operand right);
}

class AddOperation implements Operation {
    @Override
    public Operand execute(Operand left, Operand right) {
        return new Operand(left.value+right.value);
    }
}

class SubtractOperation implements Operation {
    @Override
    public Operand execute(Operand left, Operand right) {
        return new Operand(left.value-right.value);
    }
}

class MultiplyOperation implements Operation {
    @Override
    public Operand execute(Operand left, Operand right) {
        return new Operand(left.value*right.value);
    }
}

class DivideOperation implements Operation {
    @Override
    public Operand execute(Operand left, Operand right) {
        return new Operand(left.value/right.value);
    }
}

class RemainderOperation implements Operation {
    @Override
    public Operand execute(Operand left, Operand right) {
        return new Operand(left.value%right.value);
    }
}

class PowerOperation implements Operation {

    @Override
    public Operand execute(Operand left, Operand right) {
        //find the power of left raised to the right
        return new Operand(Math.pow(left.value,right.value));
    }
}

class LessThanOperation implements Operation {
    @Override
    public Operand execute(Operand left, Operand right) {
        return new Operand(left.value<right.value?1:0);
    }
}

class LessThanEqualToOperation implements Operation {
    @Override
    public Operand execute(Operand left, Operand right) {
        return new Operand(left.value<=right.value?1:0);
    }
}

class GreaterThanOperation implements Operation {
    @Override
    public Operand execute(Operand left, Operand right) {
        return new Operand(left.value>right.value?1:0);
    }
}

class GreaterThanEqualToOperation implements Operation {
    @Override
    public Operand execute(Operand left, Operand right) {
        return new Operand(left.value>=right.value?1:0);
    }
}

class EqualToOperation implements Operation {
    @Override
    public Operand execute(Operand left, Operand right) {
        return new Operand(left.value==right.value?1:0);
    }
}

class AndOperation implements Operation {
    @Override
    public Operand execute(Operand left, Operand right) {
        return new Operand(left.value!=0&&right.value!=0?1:0);
    }
}

class OrOperation implements Operation {
    @Override
    public Operand execute(Operand left, Operand right) {
        return new Operand(left.value==1||right.value==1?1:0);
    }
}

class NotOperation implements Operation {
    @Override
    public Operand execute(Operand left, Operand right) {
        return new Operand(right.value!=0?0:1);
    }
}

/**
 * eg ++i or ++4
 * acts on right operand
 */
class PrefixIncrementOperation implements Operation {
    @Override
    public Operand execute(Operand left, Operand right) {
        return new Operand(right.value+1);
    }
}

/**
 * eg --i or --4
 * acts on right operand
 */
class PrefixDecrementOperation implements Operation {
    @Override
    public Operand execute(Operand left, Operand right) {
        return new Operand(right.value-1);
    }
}

/**
 * eg i++ or 4++
 * acts on left operand
 */
class PostfixIncrementOperation implements Operation {
    @Override
    public Operand execute(Operand left, Operand right) {
        return new Operand(left.value+1);
    }
}

/**
 * eg i-- or 4--
 * acts on left operand
 */
class PostfixDecrementOperation implements Operation {
    @Override
    public Operand execute(Operand left, Operand right) {
        return new Operand(left.value-1);
    }
}

//=============================================================================================
//Custom Map
//=============================================================================================


/**
 * Single key value pair node of a Map.
 * Keys are strings.
 * Values are integers.
 */
class MapItem{
    String key;
    Integer value;

    public MapItem(String key, Integer value) {
        this.key = key;
        this.value = value;
    }
}

/**
 * Very simple custom map that can hold atmost 100 map items
 */
class MyMap{
    private static final int MAX_CAPACITY = 100;
    MapItem[] mapItems=new MapItem[MAX_CAPACITY];
    int totalElements=0;

    /**
     * gets the value for given key
     * @param key key to look for
     * @return the integer value if exists ,otherwise null
     */
    public Integer get(String key){
        MapItem mapItem=getMapItem(key);
        if(mapItem!=null){
            return mapItem.value;
        }
        return null;
    }

    /**
     * puts the value for given key.If the key already exists,
     * it modifies its value otherwise it adds a new map item
     * @param key the key to add or modify
     * @param value the value to put for that key
     * @return true if the insertion was successful,false
     *          if the the map is full and insertion was
     *          not successful,because it reached capacity
     */
    public boolean put(String key,Integer value){
        boolean mapFull=false;
        MapItem mapItem=getMapItem(key);
        if(mapItem!=null){
            mapItem.value=value;
        }else{
            if(totalElements<MAX_CAPACITY){
                mapItems[totalElements++]=new MapItem(key,value);
            }else{
                mapFull=true;
            }
        }
        return mapFull;
    }

    /**
     * gets the complete map item for given key
     * @param key key to look for
     * @return the map item if it exists ,otherwise null
     */
    public MapItem getMapItem(String key){
        //search the map items for the key
        for (int i = 0; i < totalElements; i++) {
            if(mapItems[i].key.equalsIgnoreCase(key)){
                //key found so return the item
                return mapItems[i];
            }
        }
        return null;
    }

}

