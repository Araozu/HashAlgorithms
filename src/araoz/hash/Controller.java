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
        boolean esHex = codificacion_hex.isSelected();
        String entrada = texto_entrada.getText();
        String resultado = esHex? SHA1.runHEX(entrada) : SHA1.runUTF8(entrada);
        texto_salida.setText(resultado);
    }
}
