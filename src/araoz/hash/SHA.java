package araoz.hash;

public abstract class SHA {

    abstract int constanteRonda(int numRonda);

    /**
     * Rotacion circular izquierda
     *
     * @param X Palabra a rotar
     * @param n cantidad de bits a rotar
     * @return X rotado n bits a la izquierda circularmente
     */
    int ROTL(int X, int n) {
        return (X << n) | (X >>> (32 - n));
    }

    /**
     * Rotacion circular derecha
     *
     * @param X Palabra a rotar
     * @param n cantidad de bits a rotar
     * @return X rotado n bits a la derecha circularmente
     */
    int ROTR(int X, int n) {
        return (X >>> n) | (X << (32 - n));
    }

    /**
     * Rotacion derecha
     * @param X Palabra a rotar
     * @param n cantidad de bits a rotar
     * @return X rotado n bits a la derecha. Los bits a la derecha se pierden.
     */
    int SHR(int X, int n) {
        return X >>> n;
    }

    abstract String run(byte[][] bytes);

    public abstract String runHEX();

    public abstract String runUTF8();

}
