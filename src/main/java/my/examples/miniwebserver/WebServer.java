package my.examples.miniwebserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;

public class WebServer {
    private int port;

    public WebServer(int port){
        this.port = port;
    }
    public void run(){
        // 접속을 대기하고 있다.
        ServerSocket serverSocket = null;
        try{
            serverSocket = new ServerSocket(port);
            while(true) {
                System.out.println("접속을 대기합니다.");
                Socket socket = serverSocket.accept(); // 클라이언트가 접속할때까지 대기
                HttpHandler httpHandler = new HttpHandler(socket);
                httpHandler.start(); // 쓰레드를 실행한다.
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
        }finally {
            try{ serverSocket.close(); }catch(Exception ignore){ }
        }
    }
}

class HttpHandler extends Thread{
    private Socket socket;
    public HttpHandler(Socket socket){
        this.socket = socket;
    }

    public void run(){ // 별도로 동작할 코드는 run메소드에서 작성한다.
        final String baseDir = "C:\\Users\\andrew\\Desktop\\simpleHTMLpage-master\\simpleHTMLpage-master";

        BufferedReader in = null;
        PrintStream out = null;
        FileInputStream fis = null;
        InputStream is = null;
        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream());

            String requestLine = in.readLine();
            System.out.println("요청정보: " + requestLine);
            String[] s = requestLine.split(" ");
            String httpMethod = s[0];
            System.out.println(httpMethod);
            String httpPath = s[1];
            System.out.println(httpPath);
            String filePath = "";
            if(httpPath.equals("/")) {
                httpPath = "/index.html";
                filePath = baseDir + httpPath;
            }else{
                filePath = baseDir + httpPath;
                System.out.println(filePath);
            }

            File file = new File(filePath);
            is = new BufferedInputStream(new FileInputStream(file));
            String mimeType = URLConnection.guessContentTypeFromStream(is);
            System.out.println("mimeType: " + mimeType);
            if(mimeType == null)
                mimeType = "text/css";

            String line = null;
            while((line = in.readLine()) != null){ // 빈줄까지 읽어들인다.
                if("".equals(line)){
                    break;
                }
                System.out.println("헤더정보 : " + line );
            }

            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: " + mimeType);
            out.println("Content-Length: " + file.length());
            out.println();

            byte[] buffer = new byte[1024];
            int readCount = 0;
            fis = new FileInputStream(file);
            while((readCount = fis.read(buffer)) != -1){
                out.write(buffer,0, readCount);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally {
            try{ fis.close(); } catch(Exception ignore){}
            try{ in.close(); }catch(Exception ignore){}
            try{ out.close(); }catch(Exception ignore){}
            try{ socket.close(); }catch(Exception ignore){}
        }
    }
}

//package my.examples.miniwebserver;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintStream;
//import java.net.ServerSocket;
//import java.net.Socket;
//
//public class WebServer {
//    private int port;
//
//    public WebServer(int port) {
//        this.port = port;
//    }
//
//    public void run() {
//        //접속을 대기하다가
//        ServerSocket serverSocket = null;
//        try {
//            serverSocket = new ServerSocket(port);
//            while (true) {
//                System.out.println("접속 대기");
//                Socket socket = serverSocket.accept(); //클라이언트가 접속 할때까지 대기한다. Blocking method.
//                HttpHandler httpHandler = new HttpHandler(socket);
//                httpHandler.start(); //쓰레드 실행
//            }
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        } finally {
//            try {
//                serverSocket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
//
//class HttpHandler extends Thread {
//    //누군가 접속을 하면 서버는 빈줄이 나올때까지 한줄씩 읽어들인다.
//    //별도의 일을 처리해주는 것을 Handler라고 한다.
//    //서버는 path에 해당하는 리소스를 찾는다. (File class 활용)
//    //리소스가 있을 경우, 상태코드를 보낸다.
//    //헤더정보를 보낸다. (body의 길이, body의 내용{이미지인지, css인지..} mime type이 포함되어있다)
//    //빈줄을 보낸다.
//    //body를 보낸다.
//    //Thread 사용해서 구현
//
//    private Socket socket;
//
//    public HttpHandler(Socket socket) {
//        this.socket = socket;
//    }
//
//    public void run() {
//        //별도로 동작할 코드는 run메소드에서 작성한다.
//        BufferedReader in = null;
////        body는 바이너리 데이터일수도 있다.
//        PrintStream out = null;
//        try {
//            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            out = new PrintStream(socket.getOutputStream());
//
//            String requestLine = in.readLine();//요청라인 "Get /" 형식
//            String line = null;
//            while ((line = in.readLine()) != null) {//빈줄까지 읽어들인다.
//                if ("".equals(line)) { //null 일 경우를 대비해서 방어적인 코드로 작성. 만약  line.equals("")로 작성한다면 null이 있을경우 NullPointerException이 발생한다.
//                    break;
//                }
//                System.out.println("헤더정보: " + line);
//            }
//            String mainType = "";
//            String subType = requestLine.substring(requestLine.lastIndexOf(".") + 1);
//            String typeInfo = "";
//            if (subType.equals("html") || subType.equals("plain") || subType.equals("css") || subType.equals("javascript") || subType.equals("markdown")) {
//                mainType = "text";
//                typeInfo = " charset=UTF-8";
//            } else if (subType.equals("gif") || subType.equals("png") || subType.equals("jpeg") || subType.equals("bmp") || subType.equals("webp")) {
//                mainType = "image";
//                typeInfo = "";
//            }
//
//            out.println("HTTP/1.1 200 OK");
//            out.println("Content-type: " + mainType + "/" + subType + ";" + typeInfo); //requestLine에 맞춰서 변경되어야 한다.
//            out.println("Contents-length: " );
//            //out.println("content-type: text/html; charset=UTF-8");
//            out.println();
//            out.println("<html><h1>HELLO</h1></html>");//requestLine에 맞춰서 변경되어야 한다.
//
//        } catch (Exception ex) {
//
//        } finally {
//            try {
//                in.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            out.close();
//            try {
//                socket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//}
