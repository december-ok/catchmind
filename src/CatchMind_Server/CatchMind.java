package CatchMind_Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class CatchMind {
	ServerSocket serverSocket = null;
	ArrayList<PrintWriter> listWriters[] = new ArrayList[5];
	
	public CatchMind() {
		
		for (int i = 0; i < 5; i++) {
			listWriters[i] = new ArrayList<PrintWriter>();			
		}
		new SuperChat(listWriters).start();
		new ServerInfoSender(listWriters).start();
		access();
	}
	
	public void access() {
		try {
			serverSocket = new ServerSocket();

			String hostAddress = InetAddress.getLocalHost().getHostAddress();
			serverSocket.bind(new InetSocketAddress(hostAddress, Main.Port));
			consoleLog("연결 기다림 - " + hostAddress + ":" + Main.Port);
			
			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("someone tried connect");
				BufferedReader buffereedReader = 
			            new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
				
				//read for what to do
				String request = buffereedReader.readLine();
				if(request.equals("login")) {
					login(socket, buffereedReader);
					System.out.println("someone tried login");
				}
				if(request.equals("signUp")) {
					signUp(socket, buffereedReader);
					System.out.println("Someone tired SignUp");
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void login(Socket socket, BufferedReader buffereedReader) {
		System.out.println("로그인 드러옴");
		new ServerManager(socket, buffereedReader,listWriters).start();
	}
	
	public void signUp(Socket socket, BufferedReader buffereedReader) {
		
	}
	
    private static void consoleLog(String log) {
        System.out.println("[server " + Thread.currentThread().getId() + "] " + log);
    }
}
