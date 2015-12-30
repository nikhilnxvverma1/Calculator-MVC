import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by NikhilVerma on 01/10/15.
 */
public class Main extends Application {
    private static final double WIDTH = 500;
    private static final double HEIGHT = 300;
    private static final String MAIN_VIEW_LOCATION = "calc-view.fxml";
    private static final String TITLE = "Calculator";
    public static final String STYLESHEET = "warm.css";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.initStyle(StageStyle.UNDECORATED);
        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getClassLoader().getResource(MAIN_VIEW_LOCATION));
        Parent root= (Parent) fxmlLoader.load();
        root.getStylesheets().add(STYLESHEET);
        Scene scene=new Scene(root,WIDTH,HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();//layout containers wont be initialized until primary stage is shown

    }
}
