package com.socket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 封装了常用的Socket的方法
 * @author Jason
 * @version 1.0
 */
public class SocketUtils {
	/**
	 * 获取一个客户端的Socket
	 * @param ip 服务端的IP地址
	 * @param port 服务端的端口
	 * @return 客户端的Socket
	 */
	public static SocketClient asClient(String ip, int port) throws UnknownHostException, IOException{
		return new SocketClient(ip, port);
	}
	
	/**
	 * 获取一个服务端的Socket
	 * @param port 用作服务端的端口
	 * @return 客户端的Socket
	 */
	public static SocketServer asServer(int port) throws IOException{
		return new SocketServer(port);
	}
	
	/**
	 * 客户端Socket
	 * @author Jason
	 * @version 1.0
	 */
	public static class SocketClient{
		public final Socket socket;
		
		public SocketClient(Socket socket) {
			this.socket = socket;
		}
		
		public SocketClient(String ip, int port) throws UnknownHostException, IOException {
			this.socket = new Socket(ip, port);
		}
		
		public Writer newBufferedWriter() throws IOException{
			return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		}
		
		public Reader newBufferedReader() throws IOException{
			return new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		
		public BufferedOutputStream newBufferedOutputStream() throws IOException{
			return new BufferedOutputStream((socket.getOutputStream()));
		}
		
		public BufferedInputStream newBufferedInputStream() throws IOException{
			return new BufferedInputStream(socket.getInputStream());
		}
	}
	
	/**
	 * 服务端Socket
	 * @author Jason
	 * @version 1.0
	 */
	public static class SocketServer{
		public final ServerSocket server;
		
		public SocketServer(ServerSocket server) {
			this.server = server;
		}
		
		public SocketServer(int port) throws IOException {
			this.server = new ServerSocket(port);
		}
		
		public SocketClient accept(){
			try {
				return new SocketClient(server.accept());
			} catch (IOException e) {
				return null;
			}
		}
	}
}
