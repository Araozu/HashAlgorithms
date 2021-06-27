package araoz.hash;

import java.nio.charset.StandardCharsets;
import static araoz.hash.Utils.ROTL;
import static araoz.hash.Utils.ROTR;
import static araoz.hash.Utils.SHR;

// https://datatracker.ietf.org/doc/html/rfc1321
public class MD5 {

    private final String input;

    public MD5(String input) {
        this.input = input;
    }

    private int[][] pad(byte[] rawBytes, boolean paddingAplicado) {
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

        // Colocar la cantidad de bits al final
        System.arraycopy(Utils.longAByteArr(numBytes * 8L), 0, bytes, (numBloques * 64) - 8, 8);

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

    int constante(int i) {
        return MD5Constantes.constantes[i];
    }

    int desplazamiento(int i) {
        return MD5Constantes.desplazamientos[i];
    }

    int F(int x, int y, int z) {
        return (x & y) | (~x & z);
    }

    int G(int x, int y, int z) {
        return (x & z) | (y & ~z);
    }

    int H(int x, int y, int z) {
        return x ^ y ^ z;
    }

    int I(int x, int y, int z) {
        return y ^ (x | (~z));
    }

    private void procesarBloque(int[] bloque, int[] H) {
        int a = H[0],
            b = H[1],
            c = H[2],
            d = H[3];

        for (int i = 0; i < 64; i++) {
            int f, g;
            if (i < 16) {
                f = (b & c) | ((~b) & d);
                g = i;
            } else if (i < 32) {
                f = (d & b) | ((~d) & c);
                g = (5 * i + 1) % 16;
            } else if (i < 48) {
                f = b ^ c ^ d;
                g = (3 * i + 5) % 16;
            } else {
                f = c ^ (b | (~d));
                g = (7 * i) % 16;
            }

            f = f + a + constante(i) + bloque[g];
            a = d;
            d = c;
            c = b;
            b = b + ROTL(f, desplazamiento(i));
        }

        H[0] += a;
        H[1] += b;
        H[2] += c;
        H[3] += d;
    }

    private String run(int[][] bloques) {
        int[] estadoActual = {
            0x67452301,
            0xefcdab89,
            0x98badcfe,
            0x10325476
        };
        for (int[] bloque : bloques) {
            procesarBloque(bloque, estadoActual);
        }

        StringBuilder s = new StringBuilder();
        for (int palabra : estadoActual) {
            s.append(Integer.toHexString(palabra));
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

        // Si quedó un caracter agregar '1000'
        boolean tamanoEntradaImpar = input.length() % 2 != 0;
        if (tamanoEntradaImpar) {
            rawInput[rawInput.length - 1] = Utils.hexStr2AByte(input.charAt(input.length() - 1) + "8");
        }

        int[][] bloques = pad(rawInput, tamanoEntradaImpar);
        return run(bloques);
    }

}
