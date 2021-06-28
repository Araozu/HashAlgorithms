package araoz.hash.sha;

import araoz.hash.Utils;

import static araoz.hash.Utils.ROTL;

public class SHA1 extends SHA {
    private final String input;

    public SHA1(String input) {
        this.input = input;
    }

    /**
     * Secure Hash Standard - Section 5: Functions used
     *
     * @param B        palabra B
     * @param C        palabra C
     * @param D        palabra D
     * @param numRonda el número de ronda, valor entre 0 y 79 inclusive
     * @return una palabra resultado de una función según el número de ronda.
     */
    int funcionLineal(int B, int C, int D, int numRonda) {
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

    int constanteRonda(int numRonda) {
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
     * Ejecuta las 80 rondas de un bloque de SHA-1. (Secure Hash Algorithm, Section 8)
     *
     * @param bloque El bloque de 64 bytes (512 bits)
     * @param H      Array de 5 int (estado). Este método modifica el array directamente.
     */
    private void procesarBloque(byte[] bloque, int[] H) {
        // Las 16 palabras del texto plano
        int[] W = Utils.dividirBloqueAPalabras32(bloque);

        int A = H[0],
            B = H[1],
            C = H[2],
            D = H[3],
            E = H[4];

        int MASK = 0x0000000F;

        for (int i = 0; i < 80; i++) {
            int s = i & MASK;

            if (i >= 16) {
                int aux = W[(s + 13) & MASK] ^ W[(s + 8) & MASK] ^ W[(s + 2) & MASK] ^ W[s];
                W[s] = ROTL(aux, 1);
            }

            int TEMP = ROTL(A, 5) + funcionLineal(B, C, D, i) + E + W[s] + constanteRonda(i);
            E = D;
            D = C;
            C = ROTL(B, 30);
            B = A;
            A = TEMP;
        }

        H[0] += A;
        H[1] += B;
        H[2] += C;
        H[3] += D;
        H[4] += E;
    }

    protected String run(byte[][] bloques) {
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
            s.append(Integer.toHexString(palabra));
        }

        return s.toString();
    }

    public String runHEX() {
        return run(Utils.dividirHex(input, 64));
    }

    public String runUTF8() {
        return run(Utils.dividirString(input, 64));
    }

}
