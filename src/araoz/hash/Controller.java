package araoz.hash;

import araoz.hash.md.MD4;
import araoz.hash.md.MD5;
import araoz.hash.sha.SHA1;
import araoz.hash.sha.SHA256;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {
    public TextArea texto_entrada;
    public TextArea texto_salida;
    public ToggleGroup tipo_entrada;
    public RadioButton codificacion_utf8;
    public RadioButton codificacion_hex;

    public void calculateSHA1(MouseEvent mouseEvent) {
        boolean esHex = codificacion_hex.isSelected();
        String entrada = texto_entrada.getText();
        if (entrada.isEmpty()) {
            texto_salida.setText("");
            return;
        }
        SHA1 sha1 = new SHA1(entrada);
        String resultado = esHex ? sha1.runHEX() : sha1.runUTF8();
        texto_salida.setText(resultado);
    }

    public void calculateSHA256(MouseEvent mouseEvent) {
        boolean esHex = codificacion_hex.isSelected();
        String entrada = texto_entrada.getText();
        if (entrada.isEmpty()) {
            texto_salida.setText("");
            return;
        }
        SHA256 sha256 = new SHA256(entrada);
        String resultado = esHex ? sha256.runHEX() : sha256.runUTF8();
        texto_salida.setText(resultado);
    }

    public void calculateMD5(MouseEvent mouseEvent) {
        boolean esHex = codificacion_hex.isSelected();
        String entrada = texto_entrada.getText();
        if (entrada.isEmpty()) {
            texto_salida.setText("");
            return;
        }
        MD5 md5 = new MD5(entrada);
        String resultado = esHex ? md5.runHEX() : md5.runUTF8();
        texto_salida.setText(resultado);
    }

    public void calculateMD4(MouseEvent mouseEvent) {
        boolean esHex = codificacion_hex.isSelected();
        String entrada = texto_entrada.getText();
        if (entrada.isEmpty()) {
            texto_salida.setText("");
            return;
        }
        MD4 md5 = new MD4(entrada);
        String resultado = esHex ? md5.runHEX() : md5.runUTF8();
        texto_salida.setText(resultado);
    }

    public void cambiarAHMAC(MouseEvent mouseEvent) throws Exception {
        Stage stage = (Stage) texto_entrada.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("HMAC.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }
}
