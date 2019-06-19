package es.uma.informatica.rsd.chat.impl;



import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.StringTokenizer;

import es.uma.informatica.rsd.chat.ifaces.Comunicacion;
import es.uma.informatica.rsd.chat.ifaces.Controlador;
import es.uma.informatica.rsd.chat.impl.DialogoPuerto.PuertoAlias;

// Clase a implementar 
public class ComunicacionImpl implements Comunicacion {

	
	/*
	 * Se definen las variables que se van a usar a lo largo de la clase.
	 */
	
	private MulticastSocket mSocket;
	private Controlador c;
	private String alias;
	private InetAddress ipOrigen;
	
	
	@Override
	public void crearSocket(PuertoAlias pa) {
		try {
			
			//Guardo el alias en la variable String que inicializé anteriormente
			alias = pa.alias;
			mSocket = new MulticastSocket(pa.puerto);
		}catch(UnknownHostException e) {
			System.out.println("La ip no es válida.");
			e.printStackTrace();
		}catch(IOException e) {
			System.out.println("Error al crear el socket.");
			e.printStackTrace();
		}
	}
	

	@Override
	public void setControlador(Controlador c) {
		//Guardar variable controlador 
		this.c = c;
	}
	
	

	@Override
	public void runReceptor() {
		while(true) {
		
		//Esta clase recibe mesajes, los trata y los muestra si son correctos.
		
		//Creo un datagrama vacio y un buffer vacio con longitud x.
		byte [] buffer = new byte[1500];
		int tamanio = buffer.length;
		
		//Creo el datagrama vacio
		DatagramPacket packet = new DatagramPacket(buffer,tamanio);
		
		try {
			
			//Lanzo el método .receive() para la recepción del datagrama
			mSocket.receive(packet);
		}catch(Exception e) {
			System.out.println("Error en la recepcion del datagrama");
			e.printStackTrace();
		}
		
		//Creo un buffer y obtengo los datos del datagrama, los proceso en formato utf-8 para poder enviar caracteres especiales
		byte [] buffer1 = packet.getData();
		String cadena = new String(buffer1,Charset.forName("UTF-8"));
		
		//Si el primer caracter del string es ! es que es unicast porque no se incluye la direccion ip
		StringTokenizer tokenizer = new StringTokenizer(cadena,"!");		
		
		if(cadena.charAt(0) != '!') {
			//Es multicast
			//A través de un tokenizador voy separando cada campo
			String ip = tokenizer.nextToken();
			String nombre = tokenizer.nextToken();
			String mensaje = tokenizer.nextToken();
			
			//Creo un nuevo SocketAddress, el primer elemento de la tupla corresponde a la ip y el segundo al puerto.
			SocketAddress sa =  new InetSocketAddress(ip,packet.getPort());
			
			//Si el alias es igual al nombre no lo envio para evitar mensaje duplicados en el envio del tipo multicast
			if(!alias.equals(nombre)) {
				
			//Tiene que salir como mensaje ip!nombre!mensaje
			StringBuilder salida = new StringBuilder();
			salida.append(ip).append("!").append(nombre);
			
				
			c.mostrarMensaje(sa, salida.toString(), mensaje);
			
			
			}
		}else {
		//Creo un tokenizador y voy separando los campos, creo el SocketAddress y ejecuto el metodo mostrarMensaje a través de la interfaz
		//del controlador
		String alias = tokenizer.nextToken();
		String mensaje = tokenizer.nextToken();
		SocketAddress sa =  new InetSocketAddress(packet.getAddress(),packet.getPort());
		StringBuilder salida = new StringBuilder();
		salida.append("!").append(alias).append("!");
		c.mostrarMensaje(sa, salida.toString(), mensaje);
		}
		}
	}

	@Override
	public void envia(InetSocketAddress sa, String mensaje) {
		
		//Inicializo la variable ipOrigen a la dirección transmitida en el InetSocketAddres
		
		ipOrigen = sa.getAddress();
		
		//Si la dirección ip es del tipo multicast
		
		if(ipOrigen.isMulticastAddress()) {
			
			
			//tengo que añadir la dirección ip al buffer
			
			String direccion = ipOrigen.getHostName();
			String correcto = direccion+"!"+alias+"!"+mensaje;
			byte buffer[]=correcto.getBytes();
			
			//Creo el numero datagrama y posteriormente lo envio con el método .send();
			
			DatagramPacket paquete = new DatagramPacket(buffer, buffer.length,ipOrigen,sa.getPort());
			
			try {
				mSocket.send(paquete);
			}catch(IOException e) {
				System.out.println("Error en el envio del paquete");
			}
			
			
		}else {
			
			//Si no es del tipo multicast no es necesario añadir la dirección ip
			//Sigo la estructura definida para el mensaje
			
			String correcto = "!"+alias+"!"+mensaje;
			byte buffer[]=correcto.getBytes();
			DatagramPacket paquete = new DatagramPacket(buffer, buffer.length,ipOrigen,sa.getPort());
			
			try {
				mSocket.send(paquete);
			}catch(IOException e) {
				System.out.println("Error en el envio del paquete");
			}
			
			
		}
		
		
	}

	
	@Override
	public void joinGroup(InetAddress multi) {
		//Unirme a un grupo multicast,cuya dirección se indica, para así poder simular las salas de chat
		//en las que más de dos personas pueden participar en una conversación.
		
		//Creo el nuevo InetSocketAddress donde le indico la dirección ip y el puerto del socket multicast
		InetSocketAddress sa = new InetSocketAddress(multi, mSocket.getLocalPort());
		try {	
		
			//Ejecuto el método .joinGroup() en el SocketMulticast, y se le pasa como argumentos el InetSocket, y la interfaz por donde debe transmitirse.
        mSocket.joinGroup(sa, NetworkInterface.getByInetAddress(InetAddress.getLocalHost()));
		//mSocket.joinGroup(sa, NetworkInterface.getByInetAddress(InetAddress.getByName("192.168.0.103")));
		}catch(IOException e) {
			System.out.println("Error al unirse al grupo multicast.");
			e.printStackTrace();
		}
	}

	
	@Override
	public void leaveGroup(InetAddress multi) {
		//Este método se utiliza para dejar el grupo multicast cuya dirección se indica.
		InetSocketAddress sa = new InetSocketAddress(multi, mSocket.getLocalPort());
		try {	
		////Ejecuto el método .leaveGroup() en el SocketMulticast, y se le pasa como argumentos el InetSocket, y la interfaz por donde deve transmitirse.
		//mSocket.leaveGroup(sa, NetworkInterface.getByInetAddress(InetAddress.getByName("192.168.0.103")));
		mSocket.leaveGroup(sa, NetworkInterface.getByInetAddress(InetAddress.getLocalHost()));	
		}catch(IOException e) {
			System.out.println("Error al salirse del grupo multicast.");
			e.printStackTrace();
		}
	}

}
