package araoz.hash;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class SHA1 {

    /**
     * Secure Hash Standard - Section 5: Functions used
     *
     * @param B        palabra B
     * @param C        palabra C
     * @param D        palabra D
     * @param numRonda el número de ronda, valor entre 0 y 79 inclusive
     * @return una palabra resultado de una función según el número de ronda.
     */
    private static int funcionLineal(int B, int C, int D, int numRonda) {
        if (numRonda >= 0 && numRonda <= 19) {
            return (B & C) | (~B & D);
        } else if (numRonda >= 20 && numRonda <= 39) {
            return B ^ C ^ D;
        } else if (numRonda >= 40 && numRonda <= 59) {
            return (B & C) | (B & D) | (C & D);
        } else if (numRonda >= 51 && numRonda <= 79) {
            return B ^ C ^ D;
        } else {
            throw new RuntimeException("SHA1 - numRonda invalido: " + numRonda);
        }
    }

    private static int constanteRonda(int numRonda) {
        if (numRonda >= 0 && numRonda <= 19) {
            return 0x5A827999;
        } else if (numRonda >= 20 && numRonda <= 39) {
            return 0x6ED9EBA1;
        } else if (numRonda >= 40 && numRonda <= 59) {
            return 0x8F1BBCDC;
        } else if (numRonda >= 51 && numRonda <= 79) {
            return 0xCA62C1D6;
        } else {
            throw new RuntimeException("SHA1 - numRonda invalido: " + numRonda);
        }
    }

    /**
     * Secure Hash Standard - Section 3 point c
     *
     * @param X Palabra a rotar
     * @param n cantidad de bits a rotar
     * @return X rotado n bits a la izquierda
     */
    private static int rotacionCircularIzq(int X, int n) {
        return (X << n) | (X >>> (32 - n));
    }

    /**
     * Ejecuta las 80 rondas de un bloque de SHA-1. (Secure Hash Algorithm, Section 8)
     *
     * @param bloque El bloque de 64 bytes (512 bits)
     * @param estado Array de 5 int. Este método modifica el estado directamente.
     */
    private static void procesarBloque(byte[] bloque, int[] estado) {
        // Las 16 palabras del texto plano
        int[] W = Utils.dividirBloqueAPalabras32(bloque);

        int A = estado[0],
            B = estado[1],
            C = estado[2],
            D = estado[3],
            E = estado[4];

        int MASK = 0x0000000F;

        for (int i = 0; i < 80; i++) {
            int s = i & MASK;

            if (i >= 16) {
                int aux = W[(s + 13) & MASK] ^ W[(s + 8) & MASK] ^ W[(s + 2) & MASK] ^ W[s];
                W[s] = rotacionCircularIzq(aux, 1);
            }

            int TEMP = rotacionCircularIzq(A, 5) + funcionLineal(B, C, D, i) + E + W[s] + constanteRonda(i);
            E = D;
            D = C;
            C = rotacionCircularIzq(B, 30);
            B = A;
            A = TEMP;

            // FIXME
            System.out.println(Integer.toHexString(A) + "\t"
                + Integer.toHexString(B) + "\t"
                + Integer.toHexString(C) + "\t"
                + Integer.toHexString(D) + "\t"
                + Integer.toHexString(E) + "\t"
            );
        }

        estado[0] += A;
        estado[1] += B;
        estado[2] += C;
        estado[3] += D;
        estado[4] += E;
    }

    private static String run(byte[][] bloques) {
        // Estado inicial, a modificarse en cada bloque
        int[] estadoActual = {
            0x67452301,
            0xEFCDAB89,
            0x98BADCFE,
            0x10325476,
            0xC3D2E1F0
        };
        for (byte[] bloque : bloques) {
            procesarBloque(bloque, estadoActual);
        }

        StringBuilder s = new StringBuilder();
        for (int palabra : estadoActual) {
            // Codificar en UTF-8
            // byte[] bytes = ByteBuffer.allocate(4).putInt(palabra).array();
            // s.append(new String(bytes, StandardCharsets.UTF_8));
            s.append(Integer.toHexString(palabra));
        }

        return s.toString();
    }

    public static String runHEX(String hex) {
        return run(Utils.dividirHex(hex, 64));
    }

    public static String runUTF8(String input) {
        return run(Utils.dividirString(input, 64));
    }

}
