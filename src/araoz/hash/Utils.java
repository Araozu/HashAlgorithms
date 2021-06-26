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

}
