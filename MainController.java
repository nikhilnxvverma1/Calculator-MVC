import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Created by NikhilVerma on 01/10/15.
 */
public class MainController {
    @FXML
    private HBox displayBox;

    @FXML
    private GridPane grid;

    @FXML
    private Text display;

    @FXML
    private Button equalTo;
    private double initialX;
    private double initialY;

    private StringBuilder expression=new StringBuilder();

    @FXML
    void displayGridPressed(MouseEvent mouseEvent) {
        initialX = mouseEvent.getSceneX();
        initialY = mouseEvent.getSceneY();
    }

    @FXML
    void displayGridDragged(MouseEvent mouseEvent) {
        displayBox.getScene().getWindow().setX(mouseEvent.getScreenX() - initialX);
        displayBox.getScene().getWindow().setY(mouseEvent.getScreenY() - initialY);

    }

    @FXML
    void dividePressed(ActionEvent event) {
        addSymbol("/");
    }

    @FXML
    void multiplyPressed(ActionEvent event) {
        addSymbol("*");
    }

    @FXML
    void minusPressed(ActionEvent event) {
        addSymbol("-");
    }

    @FXML
    void plusPressed(ActionEvent event) {
        addSymbol("+");
    }

    @FXML
    void ninePressed(ActionEvent event) {
        addSymbol("9");
    }

    @FXML
    void sixPressed(ActionEvent event) {
        addSymbol("6");
    }

    @FXML
    void threePressed(ActionEvent event) {
        addSymbol("3");
    }

    @FXML
    void equalToPressed(ActionEvent event) {
        evaluateExpression();
    }

    @FXML
    void eightPressed(ActionEvent event) {
        addSymbol("8");
    }

    @FXML
    void fivePressed(ActionEvent event) {
        addSymbol("5");
    }

    @FXML
    void twoPressed(ActionEvent event) {
        addSymbol("2");
    }

    @FXML
    void pointPressed(ActionEvent event) {
        addSymbol(".");
    }

    @FXML
    void sevenPressed(ActionEvent event) {
        addSymbol("7");
    }

    @FXML
    void fourPressed(ActionEvent event) {
        addSymbol("4");
    }

    @FXML
    void onePressed(ActionEvent event) {
        addSymbol("1");
    }

    @FXML
    void zeroPressed(ActionEvent event) {
        addSymbol("0");
    }

    @FXML
    void allClearPressed(ActionEvent event) {
        clearEverything();
    }

    @FXML
    void lessThanEqualToPressed(ActionEvent event) {
        addSymbol("<=");
    }

    @FXML
    void greaterThanEqualToPressed(ActionEvent event) {
        addSymbol(">=");
    }

    @FXML
    void lessThanPressed(ActionEvent event) {
        addSymbol("<");
    }

    @FXML
    void greaterThanPressed(ActionEvent event) {
        addSymbol(">");
    }

    @FXML
    void equalsPressed(ActionEvent event) {
        addSymbol("==");
    }

    @FXML
    void openBracketPressed(ActionEvent event) {
        addSymbol("(");
    }

    @FXML
    void closeBracketPressed(ActionEvent event) {
        addSymbol(")");
    }

    @FXML
    void powerPressed(ActionEvent event) {
        addSymbol("^");
    }

    @FXML
    void orPressed(ActionEvent event) {
        addSymbol("or");
    }

    @FXML
    void andPressed(ActionEvent event) {
        addSymbol("and");
    }

    @FXML
    void notPressed(ActionEvent event) {
        addSymbol("not");
    }

    @FXML
    void percentPressed(ActionEvent event) {
        addSymbol("%");
    }

    @FXML
    void clearPressed(ActionEvent event) {
        deleteLastSymbol();
    }

    private void clearEverything(){
        expression.delete(0,expression.length());
        expression.append("0");
        display.setFill(Color.BLACK);//incase it was error earlier
        display.setText(expression.toString());
    }

    private void addSymbol(String symbol){
        if(expression.length()<=1&&expression.toString().equals("0")){
            expression.replace(0,1,symbol);
        }else{
            expression.append(symbol);
        }
        display.setFill(Color.BLACK);//incase it was error earlier
        display.setText(expression.toString());
    }

    private void evaluateExpression(){
        try {
            //use calculator to evaluate expression
            double result=new Calculator().evaluateExpression(expression.toString());
            expression.replace(0,expression.length(),result+"");
            display.setText(expression.toString());
        } catch (SymbolParsingException e) {
            expression.delete(0,expression.length());
            display.setText("Error");
            display.setFill(Color.RED);
        }

    }
    private void deleteLastSymbol(){
        int length=expression.length();
        if(length>1){
            expression.deleteCharAt(length-1);
        }else if(length==1){
            expression.replace(0,1,"0");
        }

        display.setText(expression.toString());
    }
}
