package CatchMind_Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ServerManager extends Thread{
	private String nickname;
    private Socket socket = null;
    private BufferedReader buffereedReader = null;
    private PrintWriter printWriter = null;
    private int WhereIam;
    private ArrayList<PrintWriter> listWriters[] = null;
    static int isAnswer=0;
    static int Answer=0;
    
    public ServerManager(Socket socket, BufferedReader BR, ArrayList<PrintWriter> LW[]) {
        this.socket = socket;
        this.buffereedReader = BR;
        this.WhereIam=0;
        this.listWriters = LW;
    }
    
    public void run() {
    	try {
    		printWriter = 
	            new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
    		String request = buffereedReader.readLine();
    		System.out.println("닉네임은"+request);
    		this.nickname = request;
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    	
        rubyChat();
    }
    
    private void rubyChat(){
    	try {
            while(true) {
                String request = buffereedReader.readLine();
                if( request == null) {
                	System.out.println("누군가 나갑니다");
                    doQuit(printWriter,this.WhereIam);
                    break;
                }
                
                String[] tokens = request.split(":");
                //join:이름:방번호
                if("join".equals(tokens[0])) {
                    doJoin(printWriter,Integer.parseInt(tokens[1]));
                    if(Integer.parseInt(tokens[1])==0)
                    	System.out.println("main chatroom entry");
                    else
                    	System.out.println("room "+tokens[1]+" entry");
                }
                else if("message".equals(tokens[0])) {
                    doMessage(tokens[1],Integer.parseInt(tokens[2]));
                }
                else if("quit".equals(tokens[0])) {
                    doQuit(printWriter,Integer.parseInt(tokens[1]));
                }
                else if("draw".equals(tokens[0])) {
                	if("erase".equals(tokens[1])) 
                		doErase(Integer.parseInt(tokens[2]));
                	else
						doDraw(request, Integer.parseInt(tokens[5]));
				}
			}
		}
        catch(IOException e) {
            consoleLog(this.nickname + "님이 게임에서 나갔습니다.");
        }
    }

    private void doQuit(PrintWriter writer, int num) {
        removeWriter(writer, num);

        String data = "ㆍ[" + this.nickname + "]님이 퇴장했습니다.";
        synchronized (listWriters) {
        	System.out.println(listWriters[0]);
        }
        broadcast(data,num);
    }

    private void removeWriter(PrintWriter writer, int num) {
        synchronized (listWriters) {
            listWriters[num].remove(writer);
        }
    }

    private void doMessage(String data, int num) {
        broadcast("["+this.nickname + "]: " + data,num);
    }

    private void doJoin(PrintWriter writer, int num) {
        String data ="ㆍ["+ nickname + "]님이 입장하였습니다.";
        broadcast(data,num);
        WhereIam=num;
        
        addWriter(writer,num);
        synchronized (listWriters) {
        	System.out.println(listWriters[0]);
        }
	    System.out.println(this.nickname+"이가 입장");
    }
    
    private void doDraw(String points, int roomnum) {
    	synchronized (listWriters) {
    		for(PrintWriter writer : listWriters[roomnum]) {
                writer.println(points);
                writer.flush();
            }
    	}
    }
    
    private void doErase(int roomnum) {
    	synchronized (listWriters) {
    		for(PrintWriter writer : listWriters[roomnum]) {
                writer.println("draw:erase");
                writer.flush();
            }
    	}
    }
    
    private void addWriter(PrintWriter writer, int num) {
        synchronized (listWriters) {
            listWriters[num].add(writer);
    	}
    }

    private void broadcast(String data, int num) {
        synchronized (listWriters) {
            for(PrintWriter writer : listWriters[num]) {
                writer.println(data);
                writer.flush();
            }
        }
    }

    private void consoleLog(String log) {
        System.out.println(log);
    }
}
