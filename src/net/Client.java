package net;

import java.net.*;
import java.io.*;

public class Client implements Closeable {
	
	/**
	 * 主催者側として接続を作る
	 * @param acceptPort 待ち受けポート番号
	 * @return
	 * @throws IOException
	 */
	public static Client connectAsHost(int acceptPort) throws IOException {
		ServerSocket serverSocket = new ServerSocket(acceptPort);
		Socket socket = serverSocket.accept();
		serverSocket.close();
		return new Client(socket, true);
	}
	
	/**
	 * 客側として接続を作る
	 * @param hostIpAddress 主催者のIPアドレス
	 * @param hostPort 主催者のポート番号
	 * @return
	 * @throws IOException
	 */
	public static Client connectAsGuest(String hostIpAddress, int hostPort) throws IOException {
		InetSocketAddress hostEndPoint = new InetSocketAddress(hostIpAddress, hostPort);
		Socket socket = new Socket();
		socket.connect(hostEndPoint, 10000);
		return new Client(socket, false);
	}
	
	/**
	 * 接続のもととなるソケット
	 */
	private Socket socket;
	
	/**
	 * ソケットから行単位で文字列を読み出すストリームリーダー
	 */
	private BufferedReader reader;
	
	/**
	 * ソケットに業単位で文字列を書き込むストリームライタ
	 */
	private PrintWriter writer;
	
	/**
	 * 主催者側かどうか
	 */
	private boolean isHost;
	
	/**
	 * 接続済のソケットを基にクライアントを作る
	 * @param socket 基となるソケット
	 * @param isHost 主催者側であればtrue 客側であればfalse
	 * @throws IOException
	 */
	private Client(Socket socket, boolean isHost) throws IOException {
		this.socket = socket;
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.writer = new PrintWriter(socket.getOutputStream());
		this.isHost = isHost;
	}
	
	public void sendMessage(String message) {
		this.writer.println(message);
		this.writer.flush();
	}
	
	public String receiveMessage() throws IOException {
		return this.reader.readLine();
	}
	
	public InetAddress getAddress() {
		return this.socket.getInetAddress();
	}
	
	public boolean isHost() {
		return this.isHost;
	}
	
	public boolean isGuest() {
		return !this.isHost();
	}
	
	@Override
	public void close() throws IOException {
		if (this.reader != null) {
			this.reader.close();
		}
		if (this.reader != null) {
			this.writer.close();
		}
	}
}
