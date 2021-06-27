package araoz.hash;

import java.nio.charset.StandardCharsets;

public class Utils {

    public static byte[][] dividirString(String input, int tamanoBloque) {
        byte[] rawInput = input.getBytes(StandardCharsets.UTF_8);

        int cantidadBloques = (int) Math.ceil(rawInput.length / (tamanoBloque + 1.0));

        byte[][] bloques = new byte[cantidadBloques][tamanoBloque];

        // Copiar todos los bloques menos el ultimo
        for (int i = 0; i < cantidadBloques - 2; i++) {
            System.arraycopy(rawInput, i * tamanoBloque, bloques[i], 0, tamanoBloque);
        }

        // Aplicar padding al ultimo bloque y almacenarlo en ultimoBloque
        // TODO: AL final del padding 64 bits se usan para almacenar la cantidad de bits del bloque. Arreglar
        byte[] ultimoBloque = new byte[tamanoBloque];
        if (rawInput.length % tamanoBloque != 0) {
            int modulo = rawInput.length % tamanoBloque;
            int bytesFaltantes = tamanoBloque - modulo;

            System.arraycopy(rawInput, (cantidadBloques - 1) * tamanoBloque, ultimoBloque, 0, modulo);

            // Ya que java no tiene unsigned byte, -128 representa 10000000
            ultimoBloque[modulo] = -128;

            // El resto de bloques de padding con valor 00000000
            for (int i = 0; i < bytesFaltantes - 2; i++) {
                ultimoBloque[modulo + i + 1] = 0;
            }
        } else {
            System.arraycopy(rawInput, rawInput.length / tamanoBloque - 1, ultimoBloque, 0, tamanoBloque);
        }

        // Asignar el bloque con padding
        bloques[bloques.length - 1] = ultimoBloque;

        return bloques;
    }

    public static int[] dividirBloqueAPalabras32(byte[] bloque) {
        int[] palabras = new int[bloque.length / 4];
        for (int i = 0; i < bloque.length / 4; i++) {
            int posicionBase = i * 4;
            palabras[i] = bloque[posicionBase]
                | bloque[posicionBase + 1] << 8
                | bloque[posicionBase + 2] << 16
                | bloque[posicionBase + 3] << 24;
        }
        return palabras;
    }

    public static long[] dividirBloqueAPalabras64(byte[] bloque) {
        long[] palabras = new long[bloque.length / 8];
        for (int i = 0; i < bloque.length / 8; i++) {
            int posicionBase = i * 8;
            palabras[i] = bloque[posicionBase]
                | bloque[posicionBase << 8]
                | bloque[posicionBase << 16]
                | bloque[posicionBase << 24]
                | bloque[posicionBase << 32]
                | bloque[posicionBase << 40]
                | bloque[posicionBase << 48]
                | bloque[posicionBase << 56];
        }
        return palabras;
    }

    public static int sumarMod32(int a, int b) {
        return 0;
    }

}
