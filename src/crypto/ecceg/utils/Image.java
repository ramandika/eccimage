package crypto.ecceg.utils;

/**
 * Created by ramandika on 18/05/16.
 */
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ramandika on 18/02/16.
 */
public class Image {

    public static class RGB{
        //Property
        private int red=255;
        private int green=255;
        private int blue=255;

        //Method
        public void setRed(int r){this.red=r;}
        public void setGreen(int g){this.green=g;}
        public void setBlue(int b){this.blue=b;}
        public int getRed(){return this.red;}
        public int getGreen(){return this.green;}
        public int getBlue(){return this.blue;}
    }
    //Property
    private int[] pixels;
    private int width;
    private int heigth;
    private RGB[] pixelsRGB;
    private BufferedImage image;
    private boolean hasAlpha;

    //Method
    public Image(int pixels[],int row, int col){
        this.width=col;
        this.heigth=row;
        this.pixels=pixels;
    }
    public Image(String path) {
        try {
            //System.out.println(path);
            image = ImageIO.read(new FileInputStream(path));
            setWidth(image.getWidth());
            setHeigth(image.getHeight());
            setPixels(image.getRGB(0, 0, image.getWidth(), image.getHeight(), getPixels(), 0,image.getWidth()));
            setPixelsRGB(convertToRGB());
            hasAlpha = isAlpha();
        } catch (IOException ex) {
            Logger.getLogger(Image.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int[] getPixels() {
        return pixels;
    }

    public void setPixels(int[] pixels) {
        this.pixels = pixels;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeigth() {
        return heigth;
    }

    public void setHeigth(int heigth) {
        this.heigth = heigth;
    }

    public RGB[] getPixelsRGB() {
        return pixelsRGB;
    }

    public void setPixelsRGB(RGB[] pixelsRGB) {
        this.pixelsRGB = pixelsRGB;
    }

    private boolean isAlpha()
    {
        boolean isAlpha = true;
        for(int x = 0; x < width && isAlpha ; x++)
        {
            for(int y = 0; y < heigth && isAlpha; y++)
            {
                isAlpha= ((image.getRGB(x,y) & 0xFF000000) == 0xFF000000);
            }
        }
        return isAlpha;
    }

    public RGB[] convertToRGB() throws NullPointerException{
        RGB[] rgb=null;
        int size=width*heigth;
        rgb=new RGB[size];
        for(int i=0;i<size;i++){
            RGB temp=new RGB();
            temp.setRed((pixels[i] >> 16) & 0xff);
            temp.setGreen((pixels[i] >> 8) & 0xff);
            temp.setBlue(pixels[i] & 0xff);
            rgb[i]=temp;
        }
        return rgb;
    }

    private byte[] appendByteArray(byte[] bytes1,byte[] bytes2){
        byte[] combined = new byte[bytes1.length + bytes2.length];
        System.arraycopy(bytes1,0,combined,0,bytes1.length);
        System.arraycopy(bytes2,0,combined,bytes1.length,bytes2.length);
        return combined;
    }

    private static byte[] eraseLeadingZero(byte[] input){
        byte[] noLeadingZero;
        if((input[0] & 0xFF) == 0){
            noLeadingZero = new byte[input.length-1];
            System.arraycopy(input,1,noLeadingZero,0,input.length-1);
        }else noLeadingZero = input;
        return noLeadingZero;
    }

/*    public int[] convertToPixels(BigInteger[] bigInt, int groupSize){
        for(int i=0;i<bigInt.length;i++){
            byte[] byteArray = bigInt[i].toByteArray();
            byteArray = eraseLeadingZero(byteArray);
            byte[] integer = new byte[];
            for(int j=0;j<byteArray.length;j++){

            }
        }
    }*/

    private String toBinary( byte[] bytes )
    {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for( int i = 0; i < Byte.SIZE * bytes.length; i++ )
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        return sb.toString();
    }

    public int[] convertToPixels(List<BigInteger> bi, int numMember){
        int[] arrInt = new int[bi.size()*numMember];
        int iterator =0;
        for(int i=0;i<bi.size();i++){
            byte[] byteArray = bi.get(i).toByteArray(); //each byteArray contains numMeber of pixel
            int complete = 4 - byteArray.length %4;
            byte[] addition = new byte[complete+byteArray.length];
            System.arraycopy(byteArray,0,addition,complete,byteArray.length);
            for(int idx=0;idx<numMember;idx++){
                byte[] temp = new byte[4];
                System.arraycopy(addition,4*idx,temp,0,4);
                ByteBuffer wrapped = ByteBuffer.wrap(temp);
                int pixelVal = wrapped.getInt();
                System.out.println(pixelVal);
                arrInt[iterator] = pixelVal;
                iterator++;
            }
        }
        return arrInt;
    }

    public BigInteger[] convertToBigInt(int bits){
        int size=4;
        int groupMember;
/*        if(isAlpha()) size = 4;
        else size = 3;*/
        groupMember =  (bits/8)/size;
        BigInteger[] result = new BigInteger[(int)Math.ceil((double)pixels.length/(double)groupMember)];
        int idx=0;
        for(int i=0;i < pixels.length;i+=groupMember){
            int j = i;
            byte[] groupPixels=new byte[1];
            Arrays.fill( groupPixels, (byte) 0 );
            do{ //combine n pixels together
                if(j<pixels.length) {
                    byte[] tempbytes = ByteBuffer.allocate(size).putInt(pixels[j]).array();
                    groupPixels = appendByteArray(groupPixels,tempbytes);
                    String binary = toBinary(groupPixels);
                    System.out.print("");
                }else {
                    byte[] tempbytes = new byte[4];
                    Arrays.fill( tempbytes, (byte) 0 );
                    groupPixels = appendByteArray(groupPixels,tempbytes);
                }
                j++;
            }while(j%groupMember!=0);
            result[idx]= new BigInteger(groupPixels);
            idx++;
        }
        return result;
    }

    public int[] convertToPixel() throws NullPointerException{
        int[] pixels=null;
        int size=width*heigth;
        pixels=new int[size];
        for(int i=0;i<size;i++){
            pixels[i]=pixelsRGB[i].getRed() << 16;
            pixels[i]+=pixelsRGB[i].getGreen() << 8;
            pixels[i]+=pixelsRGB[i].getBlue();
        }
        return pixels;
    }

    public void saveImage(String filename) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(width, heigth, BufferedImage.TYPE_INT_RGB);
        bufferedImage.setRGB(0,0,width,heigth,pixels,0,width);
        File outputfile = new File(filename);
        String format=filename.substring(filename.indexOf('.')+1,filename.length());
        ImageIO.write(bufferedImage, format, outputfile);
    }
}