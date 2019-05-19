import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import static java.lang.Character.*;

public class Client {
    static String key1 = "itsnot";
    static String key2 = "984315";

    public static String encrypt(String str) {
        int l = str.length();
        String encr = "";
        String key11 = "", key22 = "";
        char ch;
        int x, y, i;
        x = l / key1.length();
        y = l % key1.length();
        for (i = 0; i < x; i++) {
            key11 = key11 + key1;
            key22 = key22 + key2;
        }
        for (i = 0; i < y; i++) {
            key11 = key11 + key1.charAt(i);
            key22 = key22 + key2.charAt(i);
        }
        for (i = 0; i < l; i++) {
            ch = str.charAt(i);
            if (isDigit(ch))
                encr = encr + (char) ((ch - '0' + key22.charAt(i) - '0') % 10 + '0');
            else if (isUpperCase(ch))
                encr = encr + (char) ((ch - 'A' + (key11.charAt(i)) - 'a') % 26 + 'A');
            else if (isLowerCase(ch))
                encr = encr + (char) ((ch - 'a' + (key11.charAt(i)) - 'a') % 26 + 'a');
            else
                encr = encr + ch;
        }
        return encr;
    }

    public static String decrypt(String str) {
        int l = str.length();
        String decr = "";
        String key11 = "", key22 = "";
        char ch;
        int x, y, i;
        x = l / key1.length();
        y = l % key1.length();
        for (i = 0; i < x; i++) {
            key11 = key11 + key1;
            key22 = key22 + key2;
        }
        for (i = 0; i < y; i++) {
            key11 = key11 + key1.charAt(i);
            key22 = key22 + key2.charAt(i);
        }
        for (i = 0; i < l; i++) {
            ch = str.charAt(i);
            if (isDigit(ch))
                decr = decr + (char) ((ch - '0' - key22.charAt(i) + '0' + 10) % 10 + '0');
            else if (isUpperCase(ch))
                decr = decr + (char) ((ch - 'A' - (key11.charAt(i)) + 'a' + 26) % 26 + 'A');
            else if (isLowerCase(ch))
                decr = decr + (char) ((ch - 'a' - (key11.charAt(i)) + 'a' + 26) % 26 + 'a');
            else
                decr = decr + ch;
        }
        return decr;
    }

    public static void main(String[] args) throws IOException {
        InetAddress ip = InetAddress.getByName("localhost");
        int port = 16099;
        Socket socket = new Socket(ip, port);
        DataInputStream sin = new DataInputStream(socket.getInputStream());
        DataOutputStream sout = new DataOutputStream(socket.getOutputStream());
        System.out.println("Enter message in this format:");
        System.out.println("Destination client no + : + Your message");
        System.out.println("You may enter end to end process");
        myThread2 t1 = new myThread2(sout, sin, socket);
        myThread1 t2 = new myThread1(sin);
        while (true) {

        }

    }
}
class myThread1 implements Runnable {
    final DataInputStream dis;
    Thread t;

    myThread1(DataInputStream sin2) {
        dis=sin2;
        t = new Thread(this);
        t.start();
    }

    public void run(){
        String s;
        while(true) {
            try {
                s=Client.decrypt(dis.readUTF());
                String decom[]=s.split(":");
                if(decom[0].equals("0"))
                    System.out.println(decom[1]);
                else
                {
                    System.out.print("Client "+decom[0]+" : ");
                    for(int i=1;i<decom.length-1;i++)
                        System.out.print(decom[i]+":");
                    System.out.println(decom[decom.length-1]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
class myThread2 implements Runnable {
    Scanner scn = new Scanner(System.in);
    final DataOutputStream dis;
    final DataInputStream dos;
    Thread t;
    Socket soc;

    myThread2(DataOutputStream sout, DataInputStream sin, Socket soc) {
        dis = sout;
        dos = sin;
        this.soc = soc;
        t = new Thread(this);
        t.start();
    }

    public void run() {
        String str;
        while (true) {
            try {
                str = scn.nextLine();
                dis.writeUTF(Client.encrypt(str));
                if (str.equals("end")) {
                    soc.close();
                    dis.close();
                    dos.close();
                    t.interrupt();
                    System.exit(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
