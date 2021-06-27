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

    /*
    01100111010001010010001100000001
    10011000101110101101110011111110
    10011000101110101101110011111111
    10011000101110101101110011111110
    */

    public static void main(String[] args) {
        int i = 0x67452301;
        int j = ~0x67452301;
        System.out.println(Integer.toBinaryString(i));
        System.out.println(Integer.toBinaryString(j));
        System.out.println("\n----------------------------------\n");
        launch(args);
    }

}
