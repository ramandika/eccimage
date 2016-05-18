package crypto.ecceg.logic;

import sun.security.util.ECUtil;

import java.math.BigInteger;

/**
 * Created by ramandika on 18/05/16.
 */
public class ECurve {
    private BigInteger[] equation;
    private BigInteger prime;
    private EllipticalCurve.Point basePoint;

    public BigInteger[] getEquation() {
        return equation;
    }

    public BigInteger getPrime() {
        return prime;
    }


    public EllipticalCurve.Point getBasePoint() {
        return basePoint;
    }

    public void toP192(){
            equation=new BigInteger[]{new BigInteger("1"), //x3
                    new BigInteger("0"), //x2
                    new BigInteger("-3"), //-x
                    new BigInteger("64210519e59c80e70fa7e9ab72243049feb8deecc146b9b1",16)}; //+16
            prime=new BigInteger("6277101735386680763835789423207666416083908700390324961279");
            basePoint= new EllipticalCurve.Point(new BigInteger("188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012",16),
                    new BigInteger("07192b95ffc8da78631011ed6b24cdd573f977a11e794811",16));
    }

    public void toPTest(){
            equation=new BigInteger[]{new BigInteger("1"), //x3
                    new BigInteger("0"), //x2
                    new BigInteger("-1"), //x
                    new BigInteger("16")};
            prime = new BigInteger("29");
            basePoint = new EllipticalCurve.Point(new BigInteger("5"),new BigInteger("7"));
    }
}
