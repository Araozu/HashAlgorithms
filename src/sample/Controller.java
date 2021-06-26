package sample;

import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

public class Controller {
    public TextArea texto_entrada;
    public TextArea texto_salida;

    public void calculateSHA1(MouseEvent mouseEvent) {
        System.out.println("texto_entrada es: `" + texto_entrada.getText() + "` D:");
        texto_salida.setText(texto_entrada.getText());
    }
}
