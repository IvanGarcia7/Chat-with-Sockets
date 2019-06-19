package es.uma.informatica.rsd.chat.ifaces;

import java.net.InetSocketAddress;

import es.uma.informatica.rsd.chat.impl.DialogoPuerto.PuertoAlias;

/**
 * Interfaz de la vista.
 *
 */

public interface Vista
{	
	public static final String PROPIO ="propio";
	public static final String AJENO ="ajeno";
	public static final String TITLE = "Chat UDP";
	
	
	/**
	 * Este método se utiliza para crear ventanas de conversación.
	 * @param nombre Nombre de la ventana de conversación. Debe ser único.
	 * @return Si todo fue correcto devuelve true. En caso de que ya existiera una 
	 * ventana con ese nombre devuelve false.
	 */
	public boolean crearPanel (String nombre);
	
	/**
	 * Este método se utiliza para cerrar ventanas de conversación. 
	 * @param nombre Nombre de la ventana de conversación a cerrar.
	 * @return Devuelve true si todo fue bien y false si la ventana no existe.
	 */
	public boolean cerrarPanel (String nombre);
	
	/**
	 * Este método lo invca el controlador para escribir un mensaje en la ventana de
	 * conversación indicada como primer argumento.
	 * @param nombre Nombre de la ventana de conversación.
	 * @param mensaje Mensaje a escribir en la ventana.
	 * @param estilo Estilo del mensaje. Pueder ser "propio" o "ajeno" para indicar 
	 * de quién procede el texto.
	 */
	public void mostrarMensaje (String nombre, String mensaje, String estilo);
	
	/**
	 * Este método se llama al inicio para conectar la vista al controlador
	 * @param al Controlador.
	 */
	public void setControlador (Controlador al);
	
	/**
	 * Este método se usará al comienzo de la sesión para preguntar al usuario el puerto
	 * en el que escuchará el programa.
	 * @return Devuelve el número de puerto introducido por el usuario.
	 */
	public PuertoAlias getPuertoEscuchaAlias();
	
	/**
	 * Este método se usará cada vez que el usuario solicite crear una nueva ventana de 
	 * conversación para preguntar por la dirección IP y el puerto al que se deben enviarse
	 * los mensajes.
	 * @return Devuelve la dirección de socket a la que hay que enviar los mensajes.
	 */
	public InetSocketAddress getIPPuerto();
	
	/**
	 * Este método se utiliza para obtener el mensaje que el usuario escribió en una 
	 * determinada ventana de conversación.
	 * @param nombre Nombre de la ventana de conversación.
	 * @return
	 */
	public String getMensaje(String nombre);
	
	/**
	 * Muestra un mensaje de advertencia en una ventana modal.
	 * @param titulo Título de la ventana.
	 * @param mensaje Mensaje.
	 */
	public void warning(String titulo, String mensaje);
}
