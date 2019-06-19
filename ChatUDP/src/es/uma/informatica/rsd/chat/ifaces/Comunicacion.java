package es.uma.informatica.rsd.chat.ifaces;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import es.uma.informatica.rsd.chat.impl.DialogoPuerto.PuertoAlias;

/**
 * Interfaz del componente de comunicación. 
 * Este componente debe ser implementado por el alumno.
 * Los segmentos UDP que se intercambian las entidades tienen el siguiente formato:
 * - IP Multicast si es un envío multicast, sino vacío
 * - Una coma
 * - Alias
 * - Una coma
 * - Mensaje
 *
 */

public interface Comunicacion
{
	/**
	 * Crea un socket UDP asociado al puerto indicado que se usará en toda la sesión de chat.
	 * @param puerto
	 */
	public void crearSocket(PuertoAlias pa);
	
	/**
	 * Establece el controlador para que sea posible avisar de la llegada de nuevos mensajes
	 * @param c
	 */
	public void setControlador(Controlador c);
	
	/**
	 * Ejecuta un preocso que se encarga de leer los mensajes que llegan por la red y
	 * avisar al controlador de su llegada para que muestre la información en la GUI.
	 */
	public void runReceptor();
	
	/**
	 * Envía un mensaje a una dirección de socket indicada.
	 * @param sa Dirección de socket a la que enviar el mensaje
	 * @param mensaje Mensaje a enviar
	 */
	public void envia(InetSocketAddress sa, String mensaje);
	
	/**
	 * Debe unirse al grupo multicast indicado.
	 * @param multi Dirección multicast.
	 */
	public void joinGroup(InetAddress multi);
	
	/**
	 * Debe desvincularse del grupo multicast indicado.
	 * @param multi Dirección multicast.
	 */
	public void leaveGroup (InetAddress multi);
}
