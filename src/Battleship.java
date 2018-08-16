import Main.Handlers.*;
import Main.Models.*;

import java.io.*;
import java.net.*;

import javax.swing.*;

import org.json.JSONObject;
import org.json.JSONTokener;

public class Battleship
{
	public static void main(String[] args)
	{
//		initializeGui();
		
		String serverName = "ec2-18-207-150-67.compute-1.amazonaws.com";
		int port = 8989;
		
		String username = JOptionPane.showInputDialog(new JFrame(), "Enter username: ");
//		System.out.println("Connecting to " + serverName + " on port " + port);
		
		try (Socket conn = new Socket(serverName, port);
				BufferedReader read = new BufferedReader(new InputStreamReader(conn.getInputStream())))
		{
			
//			System.out.println("Just connected to " + conn.getRemoteSocketAddress());
			
			ServerHandler sh = new ServerHandler(conn);
			BattleshipGui gui = new BattleshipGui(sh);
			
			sh.SendLoginMessage(username);
			
			String input = read.readLine();
			
			while (input != null)
			{
				System.out.println(input);
				Message message = MessageFactory.parse(input);
				
				System.out.println(message.type);
				
				if (message.type.equals("Chat")) {
					System.out.println("Chat message");
					ChatMessage chat = (ChatMessage) message;
					System.out.println(chat.username + ": " + chat.chatMessage);
					gui.addMessage(chat.chatMessage);
				} else if (message.type.equals("Hit")) {
					System.out.println("received hit");
					HitMessage hitMessage = (HitMessage) message;
					gui.hit(hitMessage.hit);
				} else if (message.type.equals("Ignore")) {
					System.out.println("don't care");
				} else if (message.type.equals("Move")) {
					System.out.println("reeived move");
					MoveMessage moveMessage = (MoveMessage) message;
					gui.isHit(moveMessage.xCoordinate, moveMessage.yCoordinate);
				} else if (message.type.equals("Start")) {
					System.out.println("received start");
					gui.start();
				} else if (message.type.equals("Win")) {
					System.out.println("received win");
					gui.showWin();
				}
				
				input = read.readLine();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
