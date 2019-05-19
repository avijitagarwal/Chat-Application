import java.io.*;
import java.util.*;
import java.net.*;

import static java.lang.Character.*;

public class Server {
    static HashMap<Integer,Clients> map=new HashMap<>();
    static int k=1;
    public static void main(String[] args)throws IOException {
        ServerSocket s=new ServerSocket(16099);
        DataInputStream dis;
        DataOutputStream dos;
        Socket socket;
        while(true)
        {
            socket=s.accept();   // New Client Request accepted
            System.out.println("New Client Request Accepted");
            dis=new DataInputStream(socket.getInputStream());
            dos=new DataOutputStream(socket.getOutputStream());
            Clients cl=new Clients(k,socket,dis,dos);
            map.put(k,cl);
            Thread t=new Thread(cl);
            k++;                // increement k as count of client
            t.start();          // start thread for the new client
        }
    }
}
class Clients implements Runnable
{
    boolean status;
    int client_no;
    DataInputStream dis;
    DataOutputStream dos;
    static String key1="itsnot";
    static String key2="984315";
    Socket s;
    public Clients(int client_no, Socket s,DataInputStream dis,DataOutputStream dos)
    {
        this.client_no=client_no;
        this.s=s;
        this.dis=dis;
        this.dos=dos;
        status=true;
    }
    public static String encrypt(String str)   // Method for decryption
    {
        int l=str.length();
        String encr="";
        String key11="",key22="";
        char ch;
        int x,y,i;
        x=l/key1.length();
        y=l%key1.length();
        for(i=0;i<x;i++) {
            key11 = key11 + key1;
            key22 = key22 + key2;
        }
        for(i=0;i<y;i++) {
            key11 = key11 + key1.charAt(i);
            key22 = key22 + key2.charAt(i);
        }
        for(i=0;i<l;i++)
        {
            ch=str.charAt(i);
            if(isDigit(ch))
                encr=encr+(char)((ch-'0'+key22.charAt(i)-'0')%10+'0');
            else if(isUpperCase(ch))
                encr=encr+(char)((ch-'A'+(key11.charAt(i))-'a')%26+'A');
            else if(isLowerCase(ch))
                encr=encr+(char)((ch-'a'+(key11.charAt(i))-'a')%26+'a');
            else
                encr=encr+ch;
        }
        return encr;
    }
    public static String decrypt(String str)    //Method for encryption
    {
        int l=str.length();
        String decr="";
        String key11="",key22="";
        char ch;
        int x,y,i;
        x=l/key1.length();
        y=l%key1.length();
        for(i=0;i<x;i++) {
            key11 = key11 + key1;
            key22 = key22 + key2;
        }
        for(i=0;i<y;i++) {
            key11 = key11 + key1.charAt(i);
            key22 = key22 + key2.charAt(i);
        }
        for(i=0;i<l;i++)
        {
            ch=str.charAt(i);
            if(isDigit(ch))
                decr=decr+(char)((ch-'0'-key22.charAt(i)+'0'+10)%10+'0');
            else if(isUpperCase(ch))
                decr=decr+(char)((ch-'A'-(key11.charAt(i))+'a'+26)%26+'A');
            else if(isLowerCase(ch))
                decr=decr+(char)((ch-'a'-(key11.charAt(i))+'a'+26)%26+'a');
            else
                decr=decr+ch;
        }
        return decr;
    }
    public void run()
    {
        String msg;
        int dest,f;
        String errmsg="Error!!! Destination Client could not be found.",str;
        while(true)
        {
            try {
                msg = decrypt(dis.readUTF());     // message received from client "client_no"
                System.out.println(msg);
                if(msg.equals("end"))    // end this client connection to Server
                {
                    status=false;
                    s.close();
                    break;
                }
                String decom[]=msg.split(":");
                dest=Integer.parseInt(decom[0]);    // Get Dest Client's client_no
                f=0;
                if(Server.map.containsKey(dest)) {
                    Clients cl=Server.map.get(dest);
                    if(cl.status==true)
                    {
                        f=1;
                        str=Integer.toString(this.client_no);
                        for(int i=1;i<decom.length;i++)
                            str=str+":"+decom[i];
                        cl.dos.writeUTF(encrypt(str));
                    }
                }
                if(f==0)                                // Destination Client does not exist
                {
                    str="0:"+errmsg;
                    dos.writeUTF(encrypt(str));
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        try
        {
            // closing resources
            this.dis.close();
            this.dos.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
