package araoz.hash.sha;

import araoz.hash.Utils;

import static araoz.hash.Utils.ROTR;
import static araoz.hash.Utils.SHR;

/**
 * Bloques de 512 bits, palabras de 32 bits, hash de 256 bits
 */
public class SHA256 extends SHA {
    private final String input;

    public SHA256(String input) {
        this.input = input;
    }

    int constanteRonda(int numRonda) {
        if (numRonda >= 0 && numRonda <= 79) {
            return SHA256Constantes.constantes[numRonda];
        } else {
            throw new RuntimeException("SHA256 - numRonda invalido: " + numRonda);
        }
    }

    int K(int x) {
        return constanteRonda(x);
    }

    int Ch(int x, int y, int z) {
        return (x & y) ^ (~x & z);
    }

    int Maj(int x, int y, int z) {
        return (x & y) ^ (x & z) ^ (y & z);
    }

    int Sum0(int x) {
        return ROTR(x, 2) ^ ROTR(x, 13) ^ ROTR(x, 22);
    }

    int Sum1(int x) {
        return ROTR(x, 6) ^ ROTR(x, 11) ^ ROTR(x, 25);
    }

    int Delta0(int x) {
        return ROTR(x, 7) ^ ROTR(x, 18) ^ SHR(x, 3);
    }

    int Delta1(int x) {
        return ROTR(x, 17) ^ ROTR(x, 19) ^ SHR(x, 10);
    }

    /**
     * Procesa el bloque
     *
     * @param bloque array de 64 bytes (512 bits)
     * @param H      array de 8 ints (el estado). Este metodo modifica el array directamente.
     */
    private void procesarBloque(byte[] bloque, int[] H) {
        int[] W = new int[64];
        System.arraycopy(Utils.dividirBloqueAPalabras32(bloque), 0, W, 0, 16);
        for (int t = 16; t < 64; t++) {
            W[t] = Delta1(W[t - 2]) + W[t - 7] + Delta0(W[t - 15]) + W[t - 16];
        }

        int a = H[0],
            b = H[1],
            c = H[2],
            d = H[3],
            e = H[4],
            f = H[5],
            g = H[6],
            h = H[7];

        for (int t = 0; t < 64; t++) {
            int T1 = h + Sum1(e) + Ch(e, f, g) + K(t) + W[t];
            int T2 = Sum0(a) + Maj(a, b, c);
            h = g;
            g = f;
            f = e;
            e = d + T1;
            d = c;
            c = b;
            b = a;
            a = T1 + T2;
        }

        H[0] += a;
        H[1] += b;
        H[2] += c;
        H[3] += d;
        H[4] += e;
        H[5] += f;
        H[6] += g;
        H[7] += h;
    }

    @Override
    String run(byte[][] bloques) {
        int[] estadoActual = {
            0x6a09e667,
            0xbb67ae85,
            0x3c6ef372,
            0xa54ff53a,
            0x510e527f,
            0x9b05688c,
            0x1f83d9ab,
            0x5be0cd19
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
