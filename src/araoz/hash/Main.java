package araoz.hash;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Controller.fxml"));
        primaryStage.setTitle("Funciones Hash");
        Scene s = new Scene(root, 800, 700);
        primaryStage.setScene(s);
        primaryStage.show();
    }

    public static void main(String[] args) {
        byte b = (byte) Integer.parseInt("FF", 16);

        System.out.println(b);
        System.out.println(Integer.toBinaryString((b & 0xFF) + 0x100).substring(1));
        launch(args);
    }

}
