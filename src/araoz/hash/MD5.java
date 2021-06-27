package araoz.hash;

public class MD5 {

    private final String entrada;

    public MD5(String entrada) {
        this.entrada = entrada;
    }

    private String run(byte[][] bytes) {
        return "";
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

}
