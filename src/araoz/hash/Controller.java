package araoz.hash;

import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;

public class Controller {
    public TextArea texto_entrada;
    public TextArea texto_salida;
    public ToggleGroup tipo_entrada;
    public RadioButton codificacion_utf8;
    public RadioButton codificacion_hex;

    public void calculateSHA1(MouseEvent mouseEvent) {
        System.out.println("Radio HEX: " + codificacion_hex.isSelected());
        System.out.println("Radio UTF-8" + codificacion_utf8.isSelected());
        System.out.println("Entrada: " + texto_entrada.getText());
        String resultado = SHA1.run(texto_entrada.getText());
        texto_salida.setText(resultado);
    }
}
