package araoz.hash;

import java.nio.charset.StandardCharsets;

import static araoz.hash.Utils.ROTL;

// https://link.springer.com/content/pdf/10.1007%2F3-540-38424-3_22.pdf
public class MD4 {
    private final String input;

    public MD4(String input) {
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

        return bloques;
    }

    int constante(int i) {
        return MD5Constantes.constantes[i - 1];
    }

    int F1(int a, int b, int c, int d, int x, int s, int[] bloque) {
        return ROTL(a + ((b & c) | (~b & d)) + bloque[x], s);
    }

    int F2(int a, int b, int c, int d, int x, int s, int[] bloque) {
        return ROTL(a + ((b & c) | (b & d) | (c & d)) + bloque[x] + 0x5a827999, s);
    }

    int F3(int a, int b, int c, int d, int x, int s, int[] bloque) {
        return ROTL(a + (b ^ c ^ d) + bloque[x] + 0x6ed9eba1, s);
    }

    private void procesarBloque(int[] bloque, int[] H) {
        int A = H[0],
            B = H[1],
            C = H[2],
            D = H[3];

        A = F1(A, B, C, D, 0, 3, bloque);
        D = F1(D, A, B, C, 1, 7, bloque);
        C = F1(C, D, A, B, 2, 11, bloque);
        B = F1(B, C, D, A, 3, 19, bloque);
        A = F1(A, B, C, D, 4, 3, bloque);
        D = F1(D, A, B, C, 5, 7, bloque);
        C = F1(C, D, A, B, 6, 11, bloque);
        B = F1(B, C, D, A, 7, 19, bloque);
        A = F1(A, B, C, D, 8, 3, bloque);
        D = F1(D, A, B, C, 9, 7, bloque);
        C = F1(C, D, A, B, 10, 11, bloque);
        B = F1(B, C, D, A, 11, 19, bloque);
        A = F1(A, B, C, D, 12, 3, bloque);
        D = F1(D, A, B, C, 13, 7, bloque);
        C = F1(C, D, A, B, 14, 11, bloque);
        B = F1(B, C, D, A, 15, 19, bloque);

        A = F2(A, B, C, D, 0, 3, bloque);
        D = F2(D, A, B, C, 4, 5, bloque);
        C = F2(C, D, A, B, 8, 9, bloque);
        B = F2(B, C, D, A, 12, 13, bloque);
        A = F2(A, B, C, D, 1, 3, bloque);
        D = F2(D, A, B, C, 5, 5, bloque);
        C = F2(C, D, A, B, 9, 9, bloque);
        B = F2(B, C, D, A, 13, 13, bloque);
        A = F2(A, B, C, D, 2, 3, bloque);
        D = F2(D, A, B, C, 6, 5, bloque);
        C = F2(C, D, A, B, 10, 9, bloque);
        B = F2(B, C, D, A, 14, 13, bloque);
        A = F2(A, B, C, D, 3, 3, bloque);
        D = F2(D, A, B, C, 7, 5, bloque);
        C = F2(C, D, A, B, 11, 9, bloque);
        B = F2(B, C, D, A, 15, 13, bloque);

        A = F3(A, B, C, D, 0, 3, bloque);
        D = F3(D, A, B, C, 8, 9, bloque);
        C = F3(C, D, A, B, 4, 11, bloque);
        B = F3(B, C, D, A, 12, 15, bloque);
        A = F3(A, B, C, D, 2, 3, bloque);
        D = F3(D, A, B, C, 10, 9, bloque);
        C = F3(C, D, A, B, 6, 11, bloque);
        B = F3(B, C, D, A, 14, 15, bloque);
        A = F3(A, B, C, D, 1, 3, bloque);
        D = F3(D, A, B, C, 9, 9, bloque);
        C = F3(C, D, A, B, 5, 11, bloque);
        B = F3(B, C, D, A, 13, 15, bloque);
        A = F3(A, B, C, D, 3, 3, bloque);
        D = F3(D, A, B, C, 11, 9, bloque);
        C = F3(C, D, A, B, 7, 11, bloque);
        B = F3(B, C, D, A, 15, 15, bloque);

        H[0] += A;
        H[1] += B;
        H[2] += C;
        H[3] += D;
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
            String hex = Integer.toHexString(palabra);

            // Si la representacion HEX no incluye ceros iniciales: 03425fab3
            if (hex.length() < 8) {
                int diferencia = 8 - hex.length();
                StringBuilder aux = new StringBuilder();
                for (int i = 0; i < diferencia; i++) {
                    aux.append(" ");
                }
                aux.append(hex);
                hex = aux.toString();
            }

            // Agregar los bytes invertidos, no se porque
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

        // Si quedó un caracter agregar '1000'
        boolean tamanoEntradaImpar = input.length() % 2 != 0;
        if (tamanoEntradaImpar) {
            rawInput[rawInput.length - 1] = Utils.hexStr2AByte(input.charAt(input.length() - 1) + "8");
        }

        int[][] bloques = pad(rawInput, tamanoEntradaImpar);
        return run(bloques);
    }

}
