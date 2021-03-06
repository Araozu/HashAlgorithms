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

package araoz.hash.md;

import araoz.hash.Utils;

import java.nio.charset.StandardCharsets;

public abstract class MD {
    final String input;

    public MD(String input) {
        this.input = input;
    }

    abstract void procesarBloque(int[] bloque, int[] H);

    abstract String run(int[][] bloques);

    String run(int[][] bloques, int[] estadoActual) {
        for (int[] bloque : bloques) {
            procesarBloque(bloque, estadoActual);
        }

        StringBuilder s = new StringBuilder();
        for (int palabra : estadoActual) {
            String hex = Integer.toHexString(palabra);

            // Si la representacion HEX no incluye ceros iniciales: 03425fab3
            if (hex.length() < 8) {
                int diferencia = 8 - hex.length();
                StringBuilder aux = new StringBuilder();
                for (int i = 0; i < diferencia; i++) {
                    aux.append("0");
                }
                aux.append(hex);
                hex = aux.toString();
            }

            // Agregar los bytes invertidos
            s.append(hex, 6, 8);
            s.append(hex, 4, 6);
            s.append(hex, 2, 4);
            s.append(hex, 0, 2);
        }

        return s.toString();
    }

    public String runUTF8() {
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        int[][] bloques = pad(bytes, false);
        return run(bloques);
    }

    public String runHEX() {
        byte[] rawInput = new byte[(int) Math.ceil(input.length() / 2.0)];

        for (int i = 0; i < input.length() / 2; i++) {
            rawInput[i] = Utils.hexStr2AByte(input.substring(i * 2, i * 2 + 2));
        }

        // Si qued?? un caracter agregar '1000'
        boolean tamanoEntradaImpar = input.length() % 2 != 0;
        if (tamanoEntradaImpar) {
            rawInput[rawInput.length - 1] = Utils.hexStr2AByte(input.charAt(input.length() - 1) + "8");
        }

        int[][] bloques = pad(rawInput, tamanoEntradaImpar);
        return run(bloques);
    }

    int[][] pad(byte[] rawBytes, boolean paddingAplicado) {
        int numBloques;
        int numBytes = rawBytes.length;
        if (numBytes % 64 == 0) {
            // 1 bloque adicional de padding
            numBloques = numBytes / 64 + 1;
        } else if (numBytes % 64 < 56) {
            // 1 bloque adicional para compensar la division de int
            numBloques = numBytes / 64 + 1;
        } else {
            // 1 bloque para division de int, 1 bloque para padding
            numBloques = numBytes / 64 + 2;
        }

        byte[] bytes = new byte[numBloques * 64];
        System.arraycopy(rawBytes, 0, bytes, 0, numBytes);

        if (!paddingAplicado) {
            // Agregar '1000'
            bytes[numBytes] = -128;
        }

        // Agregar ceros
        for (int i = numBytes + (paddingAplicado ? 0 : 1); i < (numBloques * 64) - 8; i++) {
            bytes[i] = 0;
        }

        int bytesFaltantesParaInt = 4 - ((numBytes + 1) % 4);
        int indice = (numBytes + bytesFaltantesParaInt + 1) / 4;
        for (int i = 0; i < indice; i++) {
            byte b1 = bytes[i * 4];
            byte b2 = bytes[i * 4 + 1];
            byte b3 = bytes[i * 4 + 2];
            byte b4 = bytes[i * 4 + 3];
            bytes[i * 4] = b4;
            bytes[i * 4 + 1] = b3;
            bytes[i * 4 + 2] = b2;
            bytes[i * 4 + 3] = b1;
        }

        // Colocar la cantidad de bits al final
        {
            byte[] cantidadbits = Utils.longAByteArr(numBytes * 8L);
            System.arraycopy(cantidadbits, 4, bytes, (numBloques * 64) - 8, 4);
            System.arraycopy(cantidadbits, 0, bytes, (numBloques * 64) - 4, 4);
        }

        // Dividir los bytes en bloques de palabras de 32 bits. 16 palabras por bloque (512 bits)
        int[][] bloques = new int[numBloques][];
        for (int i = 0; i < numBloques; i++) {
            bloques[i] = new int[16];
            for (int j = 0; j < 16; j++) {
                int posicionBase = i * 64 + j * 4;
                bloques[i][j] = Utils.lShiftToInt(bytes[posicionBase], 24) |
                    Utils.lShiftToInt(bytes[posicionBase + 1], 16) |
                    Utils.lShiftToInt(bytes[posicionBase + 2], 8) |
                    Utils.lShiftToInt(bytes[posicionBase + 3], 0);
            }
        }

        return bloques;
    }

}
