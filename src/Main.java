import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("ui/personal_finance_assistant_GUI.fxml"));
            root.getStylesheets().add(getClass().getResource("ui/piechart.css").toExternalForm());
            stage.setScene(new Scene(root));
            stage.setTitle("Personal Finance Assistant");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
