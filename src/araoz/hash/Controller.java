package araoz.hash;

import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

public class Controller {
    public TextArea texto_entrada;
    public TextArea texto_salida;

    public void calculateSHA1(MouseEvent mouseEvent) {
        System.out.println("Entrada: " + texto_entrada.getText());
        String resultado = SHA1.run(texto_entrada.getText());
        texto_salida.setText(resultado);
    }
}
