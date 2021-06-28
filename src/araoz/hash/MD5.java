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

        /*
        abcdef
        61 62 63 64 65 66

        64636261
        00806665

        coloca cada 4 bytes al reves

        b1 b2 b3 b4 b5 b6 b7 b8
        b4 b3 b2 b1 b8 b7 b6 b5
         */

        /*
        Modificar bytes y bit de padding en el orden de MD5: cada 4 bytes al reves
        b1 b2 b3 b4 b5 b6 b7 b8 -> b4 b3 b2 b1 b8 b7 b6 b5
         */
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

        System.out.println(Integer.toHexString(bloques[0][0]));
        return bloques;
    }

    int constante(int i) {
        return MD5Constantes.constantes[i - 1];
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

    /* Let [abcd k s i] denote the operation
          a = b + ((a + F(b,c,d) + X[k] + T[i]) <<< s).
     Do the following 16 operations. */
    int F1(int a, int b, int c, int d, int k, int s, int i, int[] bloque) {
        return b + ROTL((a + F(b, c, d) + bloque[k] + constante(i)), s);
    }

    /* Let [abcd k s i] denote the operation
          a = b + ((a + G(b,c,d) + X[k] + T[i]) <<< s).
     Do the following 16 operations. */
    int F2(int a, int b, int c, int d, int k, int s, int i, int[] bloque) {
        return b + ROTL((a + G(b, c, d) + bloque[k] + constante(i)), s);
    }

    /* Let [abcd k s t] denote the operation
          a = b + ((a + H(b,c,d) + X[k] + T[i]) <<< s).
     Do the following 16 operations. */
    int F3(int a, int b, int c, int d, int k, int s, int i, int[] bloque) {
        return b + ROTL((a + H(b, c, d) + bloque[k] + constante(i)), s);
    }

    /* Let [abcd k s t] denote the operation
          a = b + ((a + I(b,c,d) + X[k] + T[i]) <<< s).
     Do the following 16 operations. */
    int F4(int a, int b, int c, int d, int k, int s, int i, int[] bloque) {
        return b + ROTL((a + I(b, c, d) + bloque[k] + constante(i)), s);
    }

    private void procesarBloque(int[] bloque, int[] H) {
        int A = H[0],
            B = H[1],
            C = H[2],
            D = H[3];

        int AA = A,
            BB = B,
            CC = C,
            DD = D;

        for (int i = 0; i < 16; i++) {
            A = F1(A, B, C, D, 0, 7, 1, bloque);
            D = F1(D, A, B, C, 1, 12, 2, bloque);
            C = F1(C, D, A, B, 2, 17, 3, bloque);
            B = F1(B, C, D, A, 3, 22, 4, bloque);
            A = F1(A, B, C, D, 4, 7, 5, bloque);
            D = F1(D, A, B, C, 5, 12, 6, bloque);
            C = F1(C, D, A, B, 6, 17, 7, bloque);
            B = F1(B, C, D, A, 7, 22, 8, bloque);
            A = F1(A, B, C, D, 8, 7, 9, bloque);
            D = F1(D, A, B, C, 9, 12, 10, bloque);
            C = F1(C, D, A, B, 10, 17, 11, bloque);
            B = F1(B, C, D, A, 11, 22, 12, bloque);
            A = F1(A, B, C, D, 12, 7, 13, bloque);
            D = F1(D, A, B, C, 13, 12, 14, bloque);
            C = F1(C, D, A, B, 14, 17, 15, bloque);
            B = F1(B, C, D, A, 15, 22, 16, bloque);
        }

        for (int i = 16; i < 32; i++) {
            A = F2(A, B, C, D, 1, 5, 17, bloque);
            D = F2(D, A, B, C, 6, 9, 18, bloque);
            C = F2(C, D, A, B, 11, 14, 19, bloque);
            B = F2(B, C, D, A, 0, 20, 20, bloque);
            A = F2(A, B, C, D, 5, 5, 21, bloque);
            D = F2(D, A, B, C, 10, 9, 22, bloque);
            C = F2(C, D, A, B, 15, 14, 23, bloque);
            B = F2(B, C, D, A, 4, 20, 24, bloque);
            A = F2(A, B, C, D, 9, 5, 25, bloque);
            D = F2(D, A, B, C, 14, 9, 26, bloque);
            C = F2(C, D, A, B, 3, 14, 27, bloque);
            B = F2(B, C, D, A, 8, 20, 28, bloque);
            A = F2(A, B, C, D, 13, 5, 29, bloque);
            D = F2(D, A, B, C, 2, 9, 30, bloque);
            C = F2(C, D, A, B, 7, 14, 31, bloque);
            B = F2(B, C, D, A, 12, 20, 32, bloque);
        }

        for (int i = 32; i < 48; i++) {
            A = F3(A, B, C, D, 5, 4, 33, bloque);
            D = F3(D, A, B, C, 8, 11, 34, bloque);
            C = F3(C, D, A, B, 11, 16, 35, bloque);
            B = F3(B, C, D, A, 14, 23, 36, bloque);
            A = F3(A, B, C, D, 1, 4, 37, bloque);
            D = F3(D, A, B, C, 4, 11, 38, bloque);
            C = F3(C, D, A, B, 7, 16, 39, bloque);
            B = F3(B, C, D, A, 10, 23, 40, bloque);
            A = F3(A, B, C, D, 13, 4, 41, bloque);
            D = F3(D, A, B, C, 0, 11, 42, bloque);
            C = F3(C, D, A, B, 3, 16, 43, bloque);
            B = F3(B, C, D, A, 6, 23, 44, bloque);
            A = F3(A, B, C, D, 9, 4, 45, bloque);
            D = F3(D, A, B, C, 12, 11, 46, bloque);
            C = F3(C, D, A, B, 15, 16, 47, bloque);
            B = F3(B, C, D, A, 2, 23, 48, bloque);
        }

        for (int i = 48; i < 64; i++) {
            A = F4(A, B, C, D, 0, 6, 49, bloque);
            D = F4(D, A, B, C, 7, 10, 50, bloque);
            C = F4(C, D, A, B, 14, 15, 51, bloque);
            B = F4(B, C, D, A, 5, 21, 52, bloque);
            A = F4(A, B, C, D, 12, 6, 53, bloque);
            D = F4(D, A, B, C, 3, 10, 54, bloque);
            C = F4(C, D, A, B, 10, 15, 55, bloque);
            B = F4(B, C, D, A, 1, 21, 56, bloque);
            A = F4(A, B, C, D, 8, 6, 57, bloque);
            D = F4(D, A, B, C, 15, 10, 58, bloque);
            C = F4(C, D, A, B, 6, 15, 59, bloque);
            B = F4(B, C, D, A, 13, 21, 60, bloque);
            A = F4(A, B, C, D, 4, 6, 61, bloque);
            D = F4(D, A, B, C, 11, 10, 62, bloque);
            C = F4(C, D, A, B, 2, 15, 63, bloque);
            B = F4(B, C, D, A, 9, 21, 64, bloque);
        }

        H[0] += A;
        H[1] += B;
        H[2] += C;
        H[3] += D;

        A += AA;
        B += BB;
        C += CC;
        D += DD;
    }

    private String run(int[][] bloques) {
        int[] estadoActual = {
            0x67452301,
            0xEFCDAB89,
            0x98BADCFE,
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
