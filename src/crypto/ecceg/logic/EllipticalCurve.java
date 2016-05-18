package crypto.ecceg.logic;

import javafx.util.Pair;

import java.math.BigInteger;

/**
 * Created by ramandika on 30/03/16.
 */
public class EllipticalCurve {

    //BigInteger[] eccEquation={new BigInteger("1"),new BigInteger("0"),new BigInteger("-1"),new BigInteger("16")};//y^2=x^3-x+16

    public BigInteger[] eccEquation;

    public BigInteger[] getEccEquation() {
        return eccEquation;
    }

    public BigInteger getP() {
        return p;
    }

    private BigInteger p; //Prime number as base

    public EllipticalCurve(ECurve curve){
        p = curve.getPrime();
        eccEquation = curve.getEquation();
    }

    public static class Point{
        BigInteger x,y; //Point (x,y)

        public boolean isInfinite(){ return x==null || y==null; }
        public Point(BigInteger x, BigInteger y){this.x=x;this.y=y;}
        public BigInteger getX() {return this.x;}
        public BigInteger getY() {return this.y;}
        public void setX(BigInteger x){this.x = x;}
        public void setY(BigInteger y){this.y = y;}

    }


    public Point add(Point P, Point Q){
        //System.out.println(P.getX().subtract(Q.getX()).toString()+" "+p.toString());
        Point resPoint;
        if(P.isInfinite()){
            resPoint=new Point(Q.getX(),Q.getY());
        }else if (Q.isInfinite()){
            resPoint=new Point(P.getX(),P.getY());
        }else if(P.getX().equals(Q.getX()) && P.getY().equals(Q.getY())){
            resPoint = doubling(P);
        }else if(P.getX().subtract(Q.getX()).equals(BigInteger.ZERO)){
            resPoint= new Point(null,null);
        }else{
            BigInteger pembilang= P.getY().subtract(Q.getY());
            BigInteger penyebut= P.getX().subtract(Q.getX()).modInverse(p);
            BigInteger lambda= pembilang.multiply(penyebut).mod(p);
            BigInteger xr= lambda.multiply(lambda).subtract(P.getX()).subtract(Q.getX()).mod(p);
            BigInteger yr=lambda.multiply(P.getX().subtract(xr)).subtract(P.getY()).mod(p);
            resPoint=new Point(xr,yr);
        }
        //System.out.println("asoy["+P.getX()+","+P.getY()+"]["+Q.getX()+","+Q.getY()+"]="+"["+resPoint.getX()+","+resPoint.getY()+"]");
        return resPoint;
    }

    public Point substract(Point P, Point Q){
        Q.setY(Q.getY().negate().mod(p));
        return add(P,Q);
    }

    public Point coefMultiply(BigInteger k,Point P){
        BigInteger dua=new BigInteger("2");
        while(k.compareTo(dua)>=0){
            if(k.mod(dua).signum()==1){ // k = 1 mod 2
                k=k.subtract(new BigInteger("1"));
                //if(k.compareTo(dua)==0) System.out.print("("+P.getX()+","+P.getY()+")+"+"("+coefMultiply(k,P).getX()+","+coefMultiply(k,P).getY()+")");
                P=add(P,coefMultiply(k,P));
            }else{ // k=0 mod 2
                k=k.divide(dua);
                P=coefMultiply(k,P);
                P=doubling(P);
            }
            k=new BigInteger("0");
        }
        return P;
    }

    public Point doubling(Point P){
        if(P.getX()== null || P.getY() == null) return new Point(null,null);
        BigInteger pembilang=P.getX().multiply(new BigInteger("3").multiply(P.getX())).add(eccEquation[2]);
        BigInteger penyebut=P.getY().multiply(new BigInteger("2")).modInverse(p);
        BigInteger lambda= pembilang.multiply(penyebut).mod(p);
        BigInteger xr=lambda.multiply(lambda).subtract(new BigInteger("2").multiply(P.getX())).mod(p);
        BigInteger yr=lambda.multiply(P.getX().subtract(xr)).subtract(P.getY()).mod(p);
        return new Point(xr,yr);
    }
}
