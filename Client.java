import java.io.*;
import java.net.*;
import java.security.*;
import javax.crypto.*;

public class Client
{
    public static final int kBufferSize = 8192;

    public static void main(String[] args) throws Exception 
    {

        try {           
            // Generate new key
            KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
	    Key pubKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            String message = "Hello World";

            // Compute signature
            Signature instance = Signature.getInstance("SHA1withRSA");
            instance.initSign(privateKey);
            instance.update((message).getBytes());
            byte[] signature = instance.sign();

            // Compute digest
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            byte[] digest = sha1.digest((message).getBytes());

            // Encrypt digest
            Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] encryptedMsg = cipher.doFinal(digest);

            //Store the key in a file
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("KeyFile.xx"));
            out.writeObject(privateKey);
            out.close();

            System.out.println("Client - Message: " + message);
            System.out.println("Client - Encrypted: " + Server.asHex(encryptedMsg));

            String host = "localhost";
            int port = 7890;
            Socket s = new Socket(host, port);

            //Open stream to cipher server
            DataOutputStream os = new DataOutputStream(s.getOutputStream());
            os.writeInt(encryptedMsg.length);
            os.write(encryptedMsg);
            os.writeInt(digest.length);
            os.write(digest);
            os.writeInt(signature.length);
            os.write(signature);

            os.flush();
            os.close();

            //Close socket
            s.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
