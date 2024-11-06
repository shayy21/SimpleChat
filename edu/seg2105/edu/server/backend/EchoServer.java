package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import edu.seg2105.client.common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //instance variables 
  ChatIF serverUI; 
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
    try 
    {
      listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	serverUI.display("Message received: <" + msg + "> from <" +client.getInfo("loginID")+">");
	
	String msgStr = (String)msg; 
	
	if (msgStr.startsWith("#login")) {
		
		int start = msgStr.indexOf('<') +1;
		int end = msgStr.lastIndexOf('>');
		String loginID = msgStr.substring(start, end);
		
		if (client.getInfo("loginID") == null) {
			client.setInfo("loginID",loginID);
			serverUI.display("<"+ loginID +"> has logged on.");
			
			
		}else {
			try {
				client.sendToClient("Error: You are already logged in. Closing connection.");
				client.close();
			} catch (IOException e) {
				serverUI.display("Error: cannot send message to client.");
			}
		}
			
	}else {
		String loginID = (String)client.getInfo("loginID");
	    this.sendToAllClients(">"+ loginID +":"+ msg );    
	}
  }
  
  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
 * @throws IOException 
   */
  public void handleMessageFromServerUI(String message) throws IOException
  {
	  
    if (message.startsWith("#")) {
    	handleCommand(message);
    }else {
    	serverUI.display("<SERVER MSG> " +message);
    	sendToAllClients("<SERVER MSG> " +message);
    }
      
  }
  
  private void handleCommand(String command) {
	  
	  if (command.equals("#quit")) {
		  System.exit(0); 
	  }
	  else if(command.equals("#stop")) {
		  stopListening();
	  }
	  else if(command.equals("#close")) {
		  try {
				close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	  }
	  else if(command.startsWith("#setport")) {
		  int start = command.indexOf('<') +1;
		  int end = command.lastIndexOf('>');
		  String port = command.substring(start, end);
		  
		  if(!isListening()) {
			  setPort(Integer.parseInt(port));
			  serverUI.display("new port is set");
		  }else {
			  serverUI.display("Cannot execute command #setport if the server is not closed!");
		  }  
	  }
	  else if(command.equals("#start")) {
		  if(!isListening()) {
			  try {
				listen();
			} catch (IOException e) {
				serverUI.display("Error! Server could not start listening for new clients.");
			}
		  }
	  }
	  else if(command.equals("#getport")) {
		  serverUI.display(String.valueOf(getPort()));
	  }
	  
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  
  //Class methods ***************************************************

  
  /**
	 * Implementing the Hook method called each time a new client connection is
	 * accepted. The default implementation does nothing.
	 * @param client the connection connected to the client.
	 */
	protected void clientConnected(ConnectionToClient client) {
		System.out.println("A new client has connected to the server.");	
	}

	/**
	 * Implementing the Hook method called each time a client disconnects.
	 * The default implementation does nothing. The method
	 * may be overridden by subclasses but should remains synchronized.
	 *
	 * @param client the connection with the client.
	 * @throws  
	 */
	synchronized protected void clientDisconnected(
		ConnectionToClient client) {
		System.out.println("The client: <"+client.getInfo("loginID")+ "> has disconnected!");
		
	}
}
//End of EchoServer class
