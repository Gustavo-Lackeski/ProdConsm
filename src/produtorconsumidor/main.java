package produtorconsumidor;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;



public class main {

    public static void main(String[] args) {
        Reader r1, r2;
        Writer w;
        Buffer b = new Buffer();
        r1 = new Reader(b);
        r2 = new Reader(b);
        w = new Writer("1.txt", b);      
        r1.start();
        r2.start();
        w.start();
    }
    
}
class Reader extends Thread{
    Buffer b;
    public void run(){
        char x;
        x = b.get();
        while (x != '\32'){
          System.out.println(x);
          x = b.get();
        }
    }
    Reader(Buffer b){
        this.b = b;
    }
}
class Writer extends Thread {
    Buffer b;
    FileInputStream fs;
    public void run(){
        int x;
        char c;
        try{
            while((x = fs.read()) != -1){
                c = (char)x;
                b.put(c);
            }
            b.put('\32');
        } catch (Exception e){
            System.err.println("Cannot Read");
            System.exit(1);
        }        
    }
    Writer(String fname, Buffer b){
        this.b = b;
        try{
            fs = new FileInputStream(fname);            
        } catch(Exception e){
            fs = null;
            System.err.println("Cannot open"+fname);
            System.exit(1);
        }        
    }
}
class Buffer{
    final int MAXSIZE = 512;
    char keep[];
    int count, front, rear;
    public synchronized char get() {
        while (count == 0)
            try {
                wait();
            } catch (InterruptedException ex) {
                System.err.println("wait do get");
                //Logger.getLogger(Buffer.class.getName()).log(Level.SEVERE, null, ex);
            }
        char x = keep[rear];
        rear = (rear+1) % MAXSIZE;
        count--;
        notify();
        return x;
    }
    public synchronized void put(char x) throws InterruptedException {
        while (count == MAXSIZE)
            wait();
        keep[front] = x;
        front = (front+1)%MAXSIZE;
        count++;
        notify();
    }
    Buffer(){
        keep = new char[MAXSIZE];
        count = 0;
        front = rear = 0;
    }
}