/*
 * Copyright (c) 2021
 * Fernando Enrique Araoz Morales.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package araoz.hash;

import araoz.hash.md.*;
import araoz.hash.sha.*;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class HMACController {
    public RadioButton codificacion_utf8;
    public RadioButton codificacion_hex;
    public TextArea texto_plano;
    public TextField clave;
    public TextArea texto_resultado;

    private final int[] ipad = {
        0x36363636, 0x36363636, 0x36363636, 0x36363636, 0x36363636, 0x36363636, 0x36363636, 0x36363636,
        0x36363636, 0x36363636, 0x36363636, 0x36363636, 0x36363636, 0x36363636, 0x36363636, 0x36363636
    };
    private final int[] opad = {
        0x5c5c5c5c, 0x5c5c5c5c, 0x5c5c5c5c, 0x5c5c5c5c, 0x5c5c5c5c, 0x5c5c5c5c, 0x5c5c5c5c, 0x5c5c5c5c,
        0x5c5c5c5c, 0x5c5c5c5c, 0x5c5c5c5c, 0x5c5c5c5c, 0x5c5c5c5c, 0x5c5c5c5c, 0x5c5c5c5c, 0x5c5c5c5c,
    };

    public void calculateMD4(MouseEvent mouseEvent) {
        // Texto plano en UTF-8 o HEX
        String m = texto_plano.getText();
        // Clave en HEX
        String K = clave.getText();
        // Si el texto plano es HEX
        boolean esHEX = codificacion_hex.isSelected();

        int[] Kprima;
        if (K.length() > 128) {
            Kprima = Utils.hexAIntArr(new MD4(K).runHEX());
        } else {
            Kprima = Utils.hexAIntArr(Utils.padKey(K));
        }

        // Primera parte
        String primeraParteHex = Utils.intArrAHex(Utils.intArrXOR2(opad, Kprima));

        // Segunda parte
        String interiorHex = Utils.intArrAHex(Utils.intArrXOR2(ipad, Kprima));
        String mHex = esHEX? m : Utils.strToHex(m);
        String segundaParteHex = new MD4(interiorHex + mHex).runHEX();

        // Hash final
        String hash = new MD4(primeraParteHex + segundaParteHex).runHEX();

        texto_resultado.setText(hash);
    }

    public void calculateMD5(MouseEvent mouseEvent) {
        // Texto plano en UTF-8 o HEX
        String m = texto_plano.getText();
        // Clave en HEX
        String K = clave.getText();
        // Si el texto plano es HEX
        boolean esHEX = codificacion_hex.isSelected();

        int[] Kprima;
        if (K.length() > 128) {
            Kprima = Utils.hexAIntArr(new MD5(K).runHEX());
        } else {
            Kprima = Utils.hexAIntArr(Utils.padKey(K));
        }

        // Primera parte
        String primeraParteHex = Utils.intArrAHex(Utils.intArrXOR2(opad, Kprima));

        // Segunda parte
        String interiorHex = Utils.intArrAHex(Utils.intArrXOR2(ipad, Kprima));
        String mHex = esHEX? m : Utils.strToHex(m);
        String segundaParteHex = new MD5(interiorHex + mHex).runHEX();

        // Hash final
        String hash = new MD5(primeraParteHex + segundaParteHex).runHEX();

        texto_resultado.setText(hash);
    }

    public void calculateSHA1(MouseEvent mouseEvent) {
        // Texto plano en UTF-8 o HEX
        String m = texto_plano.getText();
        // Clave en HEX
        String K = clave.getText();
        // Si el texto plano es HEX
        boolean esHEX = codificacion_hex.isSelected();

        int[] Kprima;
        if (K.length() > 128) {
            Kprima = Utils.hexAIntArr(new SHA1(K).runHEX());
        } else {
            Kprima = Utils.hexAIntArr(K);
        }

        // Primera parte
        String primeraParteHex = Utils.intArrAHex(Utils.intArrXOR2(opad, Kprima));

        // Segunda parte
        String interiorHex = Utils.intArrAHex(Utils.intArrXOR2(ipad, Kprima));
        String mHex = esHEX? m : Utils.strToHex(m);
        String segundaParteHex = new SHA1(interiorHex + mHex).runHEX();

        // Hash final
        String hash = new SHA1(primeraParteHex + segundaParteHex).runHEX();

        texto_resultado.setText(hash);
    }

    public void calculateSHA256(MouseEvent mouseEvent) {
        // Texto plano en UTF-8 o HEX
        String m = texto_plano.getText();
        // Clave en HEX
        String K = clave.getText();
        // Si el texto plano es HEX
        boolean esHEX = codificacion_hex.isSelected();

        int[] Kprima;
        if (K.length() > 128) {
            Kprima = Utils.hexAIntArr(new SHA256(K).runHEX());
        } else {
            Kprima = Utils.hexAIntArr(K);
        }

        // Primera parte
        String primeraParteHex = Utils.intArrAHex(Utils.intArrXOR2(opad, Kprima));

        // Segunda parte
        String interiorHex = Utils.intArrAHex(Utils.intArrXOR2(ipad, Kprima));
        String mHex = esHEX? m : Utils.strToHex(m);
        String segundaParteHex = new SHA256(interiorHex + mHex).runHEX();

        // Hash final
        String hash = new SHA256(primeraParteHex + segundaParteHex).runHEX();

        texto_resultado.setText(hash);
    }
}
