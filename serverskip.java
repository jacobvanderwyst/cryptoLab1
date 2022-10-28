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
public class serverskip{
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
    FileInputStream fis;
    FileOutputStream fos;

    // key exchange client/server
    public KeyPair createServerKey(){
        try {
            //create server key pair 
            kpg=KeyPairGenerator.getInstance("DH");
            kpg.initialize(1024);
            kp=kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return kp;
    }
    public byte[] getSecretSessionKey(KeyPair serverkeypair, PublicKey pk){
        try {
            secretK=KeyAgreement.getInstance("DH");
            try {
                secretK.init(kp.getPrivate());
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
    }

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
    }
    
    public void createFile(){
        
    }

}