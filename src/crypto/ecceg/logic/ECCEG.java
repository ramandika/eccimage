/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crypto.ecceg.logic;

import crypto.ecceg.utils.Utils;

import javax.swing.text.html.parser.Entity;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 *
 * @author Michael
 */
public class ECCEG {
    BigInteger privateKey,prime;
    EllipticalCurve.Point publicKey,basePoint;
    EllipticalCurve ecc;

    public BigInteger getKstatic() {
        return kstatic;
    }

    BigInteger kstatic=new BigInteger("1000");

    public ECCEG(ECurve input){
        ecc = new EllipticalCurve(input);
        prime = ecc.getP();
        basePoint = input.getBasePoint();
    }

    public EllipticalCurve.Point encodeMessage(BigInteger input){
        boolean found=false;
        BigInteger satu=new BigInteger("1");
        BigInteger dua=new BigInteger("2");
        BigInteger empat=new BigInteger("4");
        BigInteger x=null,y=null,iterator;
        iterator=BigInteger.ZERO;
        while(!found){
            iterator=iterator.add(satu);
            x=input.multiply(kstatic).add(iterator).mod(prime);
            BigInteger a= x.pow(3).multiply(ecc.eccEquation[0]).
                    add(x.pow(2).multiply(ecc.eccEquation[1])).
                    add(x.multiply(ecc.eccEquation[2])).
                    add(ecc.eccEquation[3]).mod(prime);
            //Find Y
            if(a.modPow(prime.subtract(satu).divide(dua),prime).compareTo(satu)==0){//Ada solusi Y
                y=a.modPow(prime.add(satu).divide(empat),prime);
                found=true;
            }
        }
        return new EllipticalCurve.Point(x,y);
    }

    public BigInteger decodeMessage(EllipticalCurve.Point p, BigInteger k){
        return p.getX().subtract(new BigInteger("1")).divide(k);
    }

    public static class CipherPair{
        EllipticalCurve.Point p1,p2;
        public CipherPair(EllipticalCurve.Point p1,EllipticalCurve.Point p2){this.p1=p1;this.p2=p2;}
        public EllipticalCurve.Point getP1(){return p1;}
        public EllipticalCurve.Point getP2(){return p2;}
    }

    public ArrayList<CipherPair> encrypt(ArrayList<BigInteger> messages) {
        ArrayList<CipherPair> result = new ArrayList<>();
        //Pilih suatu kb [0,P-1]
        privateKey=Utils.generateK(prime);
        publicKey= ecc.coefMultiply(privateKey,basePoint);
        System.out.println("private-key:"+privateKey+"\n"+"public-key:("+publicKey.getX()+","+publicKey.getY()+")");
        //convert each message to point
        System.out.println("Encrypted");
        BigInteger k=Utils.generateK(prime);
        int counter =0 ;
        for(BigInteger m : messages){
            EllipticalCurve.Point pm = encodeMessage(m);
            System.out.println(counter++);
            //System.out.println(m+"-->"+"("+pm.getX()+","+pm.getY()+")");
            result.add(new CipherPair(ecc.coefMultiply(k,basePoint),ecc.add(pm,ecc.coefMultiply(k,publicKey))));
        }
        return result;
    }
    
    public ArrayList<BigInteger> decrypt(ArrayList<CipherPair> cipher) {
        ArrayList<BigInteger> result=new ArrayList<>();
        for(CipherPair c:cipher){
            EllipticalCurve.Point bi=ecc.coefMultiply(privateKey,c.getP1());
            EllipticalCurve.Point m=ecc.substract(c.getP2(),bi);
            //System.out.println("("+m.getX()+","+m.getY()+")");
            result.add(decodeMessage(m,kstatic).mod(prime));
        }
        return result;
    }

    
}
