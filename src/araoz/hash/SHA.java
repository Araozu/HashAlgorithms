package araoz.hash;

public abstract class SHA {

    abstract int constanteRonda(int numRonda);

    abstract String run(byte[][] bytes);

    public abstract String runHEX();

    public abstract String runUTF8();

}
