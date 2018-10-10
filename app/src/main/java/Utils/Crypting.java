package Utils;

import android.util.Base64;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by USER on 10-10-2018.
 */

public class Crypting {
    static String pwd="MyAccounts",AES="AES";
    public static String enCryptText(String Data) throws Exception{
        String EncryptVal="";

            SecretKeySpec key=generateKey();
            Cipher c=Cipher.getInstance(AES);
            c.init(Cipher.ENCRYPT_MODE,key);
            byte[] encVal=c.doFinal(Data.getBytes());
            EncryptVal= Base64.encodeToString(encVal,Base64.DEFAULT);


        return  EncryptVal;
    }
    public static String deCryptText(String Data) throws Exception{
        String DecryptVal="";

            SecretKeySpec key=generateKey();
            Cipher c=Cipher.getInstance(AES);
            c.init(Cipher.DECRYPT_MODE,key);
            byte[] decedData=Base64.decode(Data,Base64.DEFAULT);
            byte[] decVal=c.doFinal(decedData);
            DecryptVal=new String(decVal);
        return  DecryptVal;
    }

    private static SecretKeySpec generateKey ()throws Exception
    {
        final MessageDigest digest=MessageDigest.getInstance("SHA-256");
        byte[] bytes=pwd.getBytes("UTF-8");
        digest.update(bytes,0,bytes.length);
        byte[] key=digest.digest();
        SecretKeySpec secretKeySpec=new SecretKeySpec(key,AES);
        return  secretKeySpec;
    }
}
