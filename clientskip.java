// code inspired by BurrisJavaCrypto

import java.util.*;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import java.security.*;
import java.security.spec.*;
import java.io.*;
import java.net.Socket;

// key set up done, add file read, encrypt, decrypt, and send
public class clientskip{
    //file stuff
    PrintWriter pw;

    //keystuff
    byte[] barr;
    KeyPairGenerator kpg;
    KeyFactory kf;
    KeyPair kp;
    PublicKey pk;
    X509EncodedKeySpec spec509;
    KeyAgreement secretK;

    // filekey stuff
    DESKeySpec dks;
    SecretKeyFactory skf;
    SecretKey skDES;
    FileOutputStream fos;
    FileInputStream fis;

    //Server stuff
    DataOutputStream dos;

    // key exchange client/server
    public KeyPair createClientKey(){
        try {
            //create server key pair 
            kpg=KeyPairGenerator.getInstance("DH");
            kpg.initialize(1024);
            kp=kpg.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return kp;
    }// done
    public byte[] getSecretSessionKey(KeyPair clientkeypair, PublicKey pk){
        try {
            secretK=KeyAgreement.getInstance("DH");
            try {
                secretK.init(clientkeypair.getPrivate());
                secretK.doPhase(pk,true);
                barr=secretK.generateSecret();
            } catch (InvalidKeyException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return barr;
    }// done

    // file encryption and transmission
    public SecretKey getDeskey(byte[] secret){
        try {
            dks=new DESKeySpec(secret);
            try {
                skf=SecretKeyFactory.getInstance("DES");
                try {
                    skDES=skf.generateSecret(dks);
                } catch (InvalidKeySpecException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return skDES;
    }// done
    public void writeFileOut(SecretKey skDES, DataOutputStream dos){// starts transmission, encrypt, sends
        try {
            fis=new FileInputStream("serverfile.txt");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            
            Cipher des=Cipher.getInstance("DES/CBC/PKCS5Padding");
            des.init(Cipher.ENCRYPT_MODE, skDES);

            //Start writing file out
            barr=des.getIV();
            try {
                dos.writeInt(barr.length);
                dos.write(barr);

                byte[] input=new byte[64];
                while(true){
                    int byteRead=fis.read(input);
                    if(byteRead==-1){
                        break; // EOF
                    }
                    byte[] out=des.update(input,0, byteRead);
                    if(out != null){
                        dos.write(out);
                    }
                }
                byte[] out=des.doFinal();
                if(out!=null){
                    dos.write(out);
                }
            } catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch(InvalidKeyException e){
            e.printStackTrace();
        }
    }
    public void readFileOut(SecretKey skDES, DataInputStream dis, DataOutputStream dos){ // accept transmission, decrypt, save to file
        
    }
    public String getRandomValue() {
        String out="";
        String source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        Random rnd = new Random();
        int index = (int) (rnd.nextFloat() * source.length()); // get index of random value in source
        out=out+source.charAt(index); // return the value
        return out;
    }
    public void createFile(){
        try {
            Runtime rt= Runtime.getRuntime();
            try{
                rt.exec("cmd.exe /c \"rm clientfile.txt\"");
                rt.exec("bash -c \"rm -f clientfile.txt\"");
                rt.exec("bash -c \"touch clientfile.txt\"");
                rt.exec("cmd.exe /c \"copy clientfile.txt\"");
            }catch(Throwable t){
                System.out.println("A command failed to execute");
            }
            pw=new PrintWriter(new FileOutputStream(new File("clientfile.txt"),true));
            int i=0;
            while(i<50){
                pw.println(getRandomValue());
                System.out.print(getRandomValue());
                i+=1;
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}