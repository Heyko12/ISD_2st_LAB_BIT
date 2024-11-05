/* Простой продьюсер, генерит случайного пользователя со своим именем и паролем, 
 * полученный объект шлет в консьюмер
 */
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        String serverAddress = "172.18.0.2";
        int serverPort = 8097;
        String filePath = "file.txt";

        while(true) {
            try (Socket socket = new Socket(serverAddress, serverPort)) {
                Date date = new Date();
                String userName = generateString();
                String password = generateString();

                MiniClass obj = new MiniClass(date, userName, password);

                FileOutputStream file = new FileOutputStream(filePath);
                ObjectOutputStream out = new ObjectOutputStream(file);

                out.writeObject(obj);

                out.close();
                file.close();

                sendFile(socket, filePath);
                deleteFile(filePath);

                synchronized (Thread.currentThread()) {
                    try {
                        Thread.currentThread().wait(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void sendFile(Socket socket, String filePath) throws Exception {
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }
        }
    }

    private static void deleteFile(String filePath) {
        File fileToDelete = new File(filePath);

        if (fileToDelete.exists()) {
            fileToDelete.delete();
        }
    }

    private static String generateString() {
        String characters = "qwerty123456";
        Random rng = new Random();
        int length = rng.nextInt(10) + 3;
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }
}