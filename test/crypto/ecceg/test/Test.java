/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crypto.ecceg.test;
import crypto.ecceg.logic.ECCEG;
import crypto.ecceg.logic.ECurve;
import crypto.ecceg.logic.EllipticalCurve;
import crypto.ecceg.utils.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael
 */
public class Test {

    private static String toBinary( byte[] bytes )
    {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for( int i = 0; i < Byte.SIZE * bytes.length; i++ )
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        return sb.toString();
    }

    private static byte[] eraseLeadingZero(byte[] input){
        byte[] noLeadingZero;
        if((input[0] & 0xFF) == 0){
            noLeadingZero = new byte[input.length-1];
            System.arraycopy(input,1,noLeadingZero,0,input.length-1);
        }else noLeadingZero = input;
        return noLeadingZero;
    }

    public static double getShannonEntropy_Image(BufferedImage actualImage){
        List<String> values= new ArrayList<String>();
        int n = 0;
        Map<Integer, Integer> occ = new HashMap<>();
        for(int i=0;i<actualImage.getHeight();i++){
            for(int j=0;j<actualImage.getWidth();j++){
                int pixel = actualImage.getRGB(j, i);
                int alpha = (pixel >> 24) & 0xff;
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;
                //0.2989 * R + 0.5870 * G + 0.1140 * B greyscale conversion
//System.out.println("i="+i+" j="+j+" argb: " + alpha + ", " + red + ", " + green + ", " + blue);
                int d= (int)Math.round(0.2989 * red + 0.5870 * green + 0.1140 * blue);
                if(!values.contains(String.valueOf(d)))
                    values.add(String.valueOf(d));
                if (occ.containsKey(d)) {
                    occ.put(d, occ.get(d) + 1);
                } else {
                    occ.put(d, 1);
                }
                ++n;
            }
        }
        double e = 0.0;
        for (Map.Entry<Integer, Integer> entry : occ.entrySet()) {
            int cx = entry.getKey();
            double p = (double) entry.getValue() / n;
            e += p * Math.log(p)/Math.log(2);
        }
        return -e;
    }

    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) {
/*        int[] pixels =new int[]{
                0xFF, 0xF1, 0xF2, 0xF3, 0xF4,
                0x01, 0x02, 0x03, 0x04, 0x05,
                0x05, 0x04, 0x03, 0x01, 0x02,
                0xD1, 0xE2, 0x12, 0x22, 0x33,
        };*/
        Image img = new Image("sky.png");
        BigInteger[] resultBigInt = img.convertToBigInt(192);
        System.out.println("Plain");
        for(BigInteger bi : resultBigInt) System.out.println(bi);
        ArrayList<BigInteger> messages= new ArrayList<BigInteger>(Arrays.asList(resultBigInt)); //testArrayInput();
        System.out.println("=======================");
        ECurve eCurve = new ECurve();
        eCurve.toP192();
        ECCEG elgamal=new ECCEG(eCurve);
        List<EllipticalCurve.Point> keyPoint = new ArrayList<>();
        List<BigInteger> cipherPixels = new ArrayList<>();
        ArrayList<ECCEG.CipherPair> result=elgamal.encrypt(messages);
        for(ECCEG.CipherPair pairpoint:result){
            EllipticalCurve.Point p1=pairpoint.getP1();
            keyPoint.add(p1);
            EllipticalCurve.Point p2=pairpoint.getP2();
            cipherPixels.add(p2.getY());
            //System.out.println("[("+p2.getX()+","+p2.getY()+")]");
            //System.out.println("[("+p1.getX()+","+p1.getY()+")"+", ("+p2.getX()+","+p2.getY()+")]");
        }
        System.out.println("=======================");
        //List<BigInteger> bi=elgamal.decrypt(result);
        //for(BigInteger b : bi) System.out.println(b);
        img.setPixels(img.convertToPixels(cipherPixels,6));
        try{
            img.saveImage("cipher4.png");
        }catch(Exception e){ e.printStackTrace(); }


/*        byte[] arr = { (byte)0x00,0x01, 0x02, 0x03, (byte)0x04 };
        arr = eraseLeadingZero(arr);
        ByteBuffer wrapped = ByteBuffer.wrap(arr);
        int temp = wrapped.getInt();
        System.out.println(toBinary(arr));
        System.out.println(temp);*/
/*        int temp = 2;
        int temp2 = 3;
        byte[] bytes1 = ByteBuffer.allocate(4).putInt(temp).array();
        byte[] bytes2 = ByteBuffer.allocate(4).putInt(temp2).array();
        byte[] combined = new byte[1+bytes1.length + bytes2.length];
        System.arraycopy(bytes1,0,combined,1,bytes1.length);
        System.arraycopy(bytes2,0,combined,1+bytes1.length,bytes2.length);
        BigInteger bi = new BigInteger(combined);
        System.out.println(bi);*/
/*        System.out.println("Plain");
        Image gambar = new Image("image1.jpg");
        ArrayList<BigInteger> messages= ; //testArrayInput();
        System.out.println("=======================");
        ECurve eCurve = new ECurve();
        eCurve.toP192();
        ECCEG elgamal=new ECCEG(eCurve);
        ArrayList<ECCEG.CipherPair> result=elgamal.encrypt(messages);
        for(ECCEG.CipherPair pairpoint:result){
            EllipticalCurve.Point p1=pairpoint.getP1();
            EllipticalCurve.Point p2=pairpoint.getP2();
            System.out.println("[("+p1.getX()+","+p1.getY()+")"+", ("+p2.getX()+","+p2.getY()+")]");
        }
        System.out.println("Decrypt-print plain text");
        List<BigInteger> plain=elgamal.decrypt(result);
        for(BigInteger pm:plain){
            System.out.println(pm);
        }*/
/*        ECurve eCurve = new ECurve();
        eCurve.toP192();
        EllipticalCurve ecc = new EllipticalCurve(eCurve);
        Scanner scan = new Scanner(System.in);
        BigInteger multiplier = scan.nextBigInteger();
        BigInteger bigNum = new BigInteger("10000");
        while(multiplier.compareTo(bigNum)<=0){
            EllipticalCurve.Point result = ecc.coefMultiply(multiplier,eCurve.getBasePoint());
            //System.out.println(multiplier+"-->"+"["+result.getX()+","+result.getY()+"]");
            multiplier = multiplier.add(BigInteger.ONE);
            //multiplier = scan.nextBigInteger();
        }*/
    }

    public static void testIO() {
        String filepath = "C:\\Users\\Michael\\Documents\\GitHub\\Crypto-ECCEG\\testfile.mkv";
        String outputPath = "C:\\Users\\Michael\\Documents\\GitHub\\Crypto-ECCEG\\testfile2.mkv";
        BigInteger data;
        try {
            data = IOUtils.getData(filepath);
            //System.out.println(data.toString(16));
            BigInteger kali = new BigInteger("32156546876354687435746876543138576546354313854354");
            data = data.multiply(kali);
            IOUtils.writeData(outputPath, data);
            data = data.divide(kali);
            IOUtils.writeData(outputPath+"x", data);
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void testUtils() {
        int digit = 2;
        BigInteger res = Utils.generateP(2);
        System.out.println(res.toString(16));
        BigInteger ran = Utils.generateK(res);
        System.out.println(ran.toString(16));
    }
    
    public static ArrayList<BigInteger> testArrayInput() {
        ArrayList<BigInteger> result=new ArrayList<>();
        String filepath = "testfile.txt";
        ArrayList<BigInteger> data;
        try {
            data = IOUtils.getDataArray(filepath);
            //System.out.println(data.toString(16));
            for(BigInteger bi : data) {
                result.add(bi);
                System.out.println(bi);
            }
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
