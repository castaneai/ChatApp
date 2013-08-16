package console;

import java.io.IOException;
import java.util.Scanner;

import net.Client;

/**
 * コンソールで動くターン制チャットアプリケーション
 * @author castaneai
 *
 */
public class ConsoleApp {
	
	/**
	 * 通信を終了するメッセージ
	 */
	private static final String DISCONNECT_MESSAGE = "007";

	/**
	 * メインメソッド
	 */
	public static void main(String[] args) {
		Client client = null;
		try {
			switch (args.length){
			case 0:
				// 引数が0個の場合はその場でクライアントかサーバかを選ぶ
				client = selectClient();
				startChat(client);
				break;
				
			case 1:
				// 引数が1この場合は1個目の引数を待ち受けポート番号として主催者として接続する
				int acceptPort = Integer.parseInt(args[0]);
				client = Client.connectAsHost(acceptPort);
				startChat(client);
				break;
				
			case 2:
				// 引数が2個の場合は1個目の引数を接続先IPアドレス、2個めの引数を接続先ポート番号として客として接続する
				String hostIpAddress = args[0];
				int hostPort = Integer.parseInt(args[1]);
				client = Client.connectAsGuest(hostIpAddress, hostPort);
				startChat(client);
				break;
				
			default:
				System.out.println("引数の数が不正です。引数は0〜2個入力してください");
				break;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (client != null) {
				try {
					client.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 対話形式でクライアント・サーバを選択して生成されたClientを返す
	 * @return
	 */
	private static Client selectClient() throws IOException {
		System.out.println("サーバーにするにこっ？クライアントにするにゃー？");
		System.out.println("0: サーバーにこっ");
		System.out.println("1: クライアントにゃー");
		
		Scanner keyboardInputScanner = new Scanner(System.in);
		int selectedNumber = Integer.parseInt(keyboardInputScanner.nextLine());
		switch (selectedNumber) {
		case 0:
			System.out.println("サーバーにしたにこっ！");
			System.out.println("待ち受けポート番号を指定してよねっ");
			int acceptPort = Integer.parseInt(keyboardInputScanner.nextLine());
			return Client.connectAsHost(acceptPort);
			
		case 1:
			System.out.println("クライアントにしたにゃー！");
			System.out.println("接続したいIPアドレスを入れるにゃー");
			String hostIpAddress = keyboardInputScanner.nextLine();
			System.out.println("接続したポート番号を入れるにゃー");
			int hostPort = Integer.parseInt(keyboardInputScanner.nextLine());
			return Client.connectAsGuest(hostIpAddress, hostPort);
			
		default:
			return null;
		}
	}
	
	/**
	 * ターン制チャットを始める
	 * @param client 自分側のクライアント
	 */
	private static void startChat(Client client) throws IOException {
		System.out.println("接続完了　相手のアドレス：" + client.getAddress());
		// こちらがゲスト側（接続した側）ならば先に送信をする
		if (client.isGuest()) {
			if (sendSingleMessageFromInput(client) == false) {
				return;
			}
		}
		// お互いが順番に送受信を繰り返す（終了メッセージを送信または受信したらそこで終わり）
		while (true) {
			if (receiveSingleMessage(client) == false) {
				break;
			}
			if (sendSingleMessageFromInput(client) == false) {
				break;
			}
		}
	}
	
	/**
	 * チャット相手から単一のメッセージを受け取る
	 * @param client
	 * @return 相手が終了した場合はfalseを返す　それ以外（チャットが続く場合）はtrueを返す
	 */
	private static boolean receiveSingleMessage(Client client) throws IOException {
		String receivedMessage = client.receiveMessage();
		System.out.println("受信メッセージ:" + receivedMessage);
		if (receivedMessage.equals(DISCONNECT_MESSAGE)) {
			printExitMessage();
			return false;
		}
		return true;
	}
	
	/**
	 * チャット相手に単一のメッセージを送信する
	 * @param client
	 * @return 自分が終了メッセージを送った場合はfalse それ以外の場合はtrueを返す
	 */
	private static boolean sendSingleMessageFromInput(Client client) {
		Scanner keyboardInputScanner = new Scanner(System.in);
		System.out.print("送信メッセージ＞");
		String inputMessage = keyboardInputScanner.nextLine();
		client.sendMessage(inputMessage);
		if (inputMessage.equals(DISCONNECT_MESSAGE)) {
			printExitMessage();
			return false;
		}
		return true;
	}
	
	/**
	 * 通信が終了したことを表示する
	 */
	private static void printExitMessage() {
		System.out.println("終了します");
	}
}
