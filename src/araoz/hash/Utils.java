package araoz.hash;

import java.nio.charset.StandardCharsets;

public class Utils {

    private static byte hexStr2AByte(String s) {
        return (byte) Integer.parseInt(s, 16);
    }

    private static byte[] longAByteArr(long l) {
        byte[] arr = new byte[8];
        for (int i = 0; i < 8; i++) {
            arr[i] = (byte) ((l << i * 8) >>> 56);
        }
        return arr;
    }

    /**
     * Divide un array de bytes en bloques
     *
     * @param rawInput        los bytes
     * @param tamanoBloque    el tamaño de un bloque en bytes
     * @param paddingAplicado si el ultimo byte de rawInput ya tiene 1000
     * @return Un array de array de bytes. El primer nivel divide bloques, el segundo nivel contiene los bytes.
     */
    private static byte[][] dividir(byte[] rawInput, int tamanoBloque, boolean paddingAplicado) {
        int cantidadBloques = (int) Math.ceil(rawInput.length / (tamanoBloque + 1.0));

        byte[][] bloques = new byte[cantidadBloques][tamanoBloque];

        // Copiar todos los bloques menos el ultimo
        for (int i = 0; i < cantidadBloques - 2; i++) {
            System.arraycopy(rawInput, i * tamanoBloque, bloques[i], 0, tamanoBloque);
        }

        long cantidadDeBits = rawInput.length * 8L - (paddingAplicado ? 4 : 0);

        // Aplicar padding al ultimo bloque. TODO: Crear un nuevo bloque si no hay espacio para el padding?
        byte[] ultimoBloque = new byte[tamanoBloque];
        if (rawInput.length % tamanoBloque != 0) {
            int modulo = rawInput.length % tamanoBloque;
            int bytesFaltantes = tamanoBloque - modulo;

            System.arraycopy(rawInput, (cantidadBloques - 1) * tamanoBloque, ultimoBloque, 0, modulo);

            // Padding
            if (!paddingAplicado) {
                // Ya que java no tiene unsigned byte, -128 representa 10000000
                ultimoBloque[modulo] = -128;
            }

            // Asumir que siempre quedan al menos 8 bytes al final

            // El resto de bloques de padding con valor 00000000
            int bytesEnBlanco = bytesFaltantes - 1 - (paddingAplicado ? 0 : 1) - 8;
            for (int i = 0; i < bytesEnBlanco - 2; i++) {
                ultimoBloque[modulo + i + 1] = 0;
            }

            // Colocar la cantidad de bits al final
            System.arraycopy(longAByteArr(cantidadDeBits), 0, ultimoBloque, ultimoBloque.length - 8, 8);
        } else {
            System.arraycopy(rawInput, rawInput.length / tamanoBloque - 1, ultimoBloque, 0, tamanoBloque);
        }

        // Asignar el bloque con padding
        bloques[bloques.length - 1] = ultimoBloque;

        return bloques;
    }

    public static byte[][] dividirString(String input, int tamanoBloque) {
        byte[] rawInput = input.getBytes(StandardCharsets.UTF_8);
        return dividir(rawInput, tamanoBloque, false);
    }

    public static byte[][] dividirHex(String hex, int tamanoBloque) {
        byte[] rawInput = new byte[(int) Math.ceil(hex.length() / 2.0)];

        for (int i = 0; i < hex.length() / 2; i++) {
            rawInput[i] = hexStr2AByte(hex.substring(i * 2, i * 2 + 2));
        }

        // Si quedó un caracter agregar '1000'
        boolean tamanoEntradaImpar = hex.length() % 2 != 0;
        if (tamanoEntradaImpar) {
            rawInput[rawInput.length - 1] = hexStr2AByte(hex.charAt(hex.length() - 1) + "8");
        }

        return dividir(rawInput, tamanoBloque, tamanoEntradaImpar);
    }

    /**
     * Convierte 'b' a int y desplaza 'n' bits a la izquierda.
     * Al castear byte a int se rellena con '1', ejm: AC -> FFFFFFAC,
     * lo cual causa problemas con el operador OR. Por ello primero se desplaza 24 bits a la izq. :
     * FFFFFFAC -> AC000000 y luego se desplaza a la derecha para compensar.
     *
     * @param b byte a desplazar
     * @param n nro de bits a desplazar
     * @return un int con los bits desplazados, y el resto de bits son 0
     */
    public static int lShiftToInt(byte b, int n) {
        return (b << 24) >>> (24 - n);
    }

    public static int[] dividirBloqueAPalabras32(byte[] bloque) {
        int[] palabras = new int[bloque.length / 4];
        for (int i = 0; i < bloque.length / 4; i++) {
            int posicionBase = i * 4;
            palabras[i] = lShiftToInt(bloque[posicionBase], 24) |
                lShiftToInt(bloque[posicionBase + 1], 16) |
                lShiftToInt(bloque[posicionBase + 2], 8) |
                lShiftToInt(bloque[posicionBase + 3], 0);
        }
        return palabras;
    }

    public static long lShiftToLong(byte b, int n) {
        return ((long) b << 56L) >>> (56L - n);
    }

    public static long[] dividirBloqueAPalabras64(byte[] bloque) {
        long[] palabras = new long[bloque.length / 8];
        for (int i = 0; i < bloque.length / 8; i++) {
            int posicionBase = i * 8;
            palabras[i] = lShiftToLong(bloque[posicionBase], 56) |
                lShiftToLong(bloque[posicionBase + 1], 48) |
                lShiftToLong(bloque[posicionBase + 2], 40) |
                lShiftToLong(bloque[posicionBase + 3], 32) |
                lShiftToLong(bloque[posicionBase + 4], 24) |
                lShiftToLong(bloque[posicionBase + 5], 16) |
                lShiftToLong(bloque[posicionBase + 6], 8) |
                lShiftToLong(bloque[posicionBase + 7], 0);
        }
        return palabras;
    }

}
