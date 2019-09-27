package dam.proyecto.remote_control_client;

import java.io.IOException;
import java.net.UnknownHostException;

import dam.proyecto.networking.SocketIOUtil;
import io.socket.client.Socket;
import io.socket.emitter.Emitter.Listener;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;

public class MainController {
	// elementos de la interfaz
	@FXML private TextArea areaNotificaciones;
	@FXML private Label labelServidor;

	// conexiones y streams
	private String serverHOST = "192.168.1.75";
	private String serverPORT = "8080";
	private String serverFullURL;

	// para conectar con nodejs
	private SocketIOUtil socketUtil;

	// Relleno de los datos de la interfaz
	@FXML
	protected void initialize() {
		actualziarURLServidor();
		//intermitentes.getSelectedToggle();
	}

	/**
	 * Funciones para los botones del menu archivo ConectarCon..., ElegirServidor,
	 * Desconectar, Salir.
	 */
	// para conectar al servidor elegido
	@FXML
	private void archivoConectarCon() throws IOException, UnknownHostException {
		conectarAlServidor();
	}

	// para cambiar la ip del servidor al que nos conectamos
	@FXML
	private void archivoElegirServidor() {
		TextInputDialog dialogServerIP = new TextInputDialog();
		dialogServerIP.titleProperty().set("Solicitud de datos");
		dialogServerIP.setHeaderText("Escribe la direccion del servidor");
		dialogServerIP.setContentText(serverHOST);
		serverHOST = dialogServerIP.showAndWait().get();
		dialogServerIP.setHeaderText("Escribe el puerto del servidor");
		dialogServerIP.setContentText(serverPORT);
		serverPORT = dialogServerIP.showAndWait().get();
		actualziarURLServidor();
		actualizarAreaNotificaciones(serverFullURL);
	}
	

	// para desconectar del servidor
	@FXML
	private void archivoDesconectar() {
		socketUtil.close();
	}

	// para cerrar la aplicacion del todo
	@FXML
	private void archivoSalir() {
		if(socketUtil!=null)socketUtil.close();
		Platform.exit();
	}

	/** fin de las funciones del menu archivo */

	private void conectarAlServidor() {
		actualizarAreaNotificaciones("Intentando conectar con " + serverHOST + ":" + serverPORT + "...");
		socketUtil = new SocketIOUtil(serverHOST, serverPORT);
		// manejador de mensajes recibidos que actualiza la interfaz
		socketUtil.getSocket().on(Socket.EVENT_MESSAGE,  new Listener() {
			@Override
			public void call(Object... args) {
				for(int i=0;i<args.length;i++) {
					actualizarAreaNotificaciones("Servidor: "+args[i]);
				}
			}
		});
		socketUtil.connect();
	}

	/**
	 * Nuevos metodos para enviar los eventos correctos al servidor
	 * */
	@FXML
	private void botonLuces(ActionEvent event) {
		if(((ToggleButton)event.getSource()).isSelected()) {
			socketUtil.emit("luces", "on");
		}else {
			socketUtil.emit("luces", "off");
		}
	}
	
	@FXML
	private void botonesIntermitentes(ActionEvent event) {
		if (event.getSource() instanceof RadioButton) {
			String message = ((RadioButton) event.getSource()).getText();
			socketUtil.emit("intermitentes",message);
		} else if (event.getSource() instanceof ToggleButton) {
			System.out.println(((ToggleButton)event.getSource()).getText());
			if(((ToggleButton)event.getSource()).isSelected()){
				socketUtil.emit("emergencias","on");
			}else {
				socketUtil.emit("emergencias","off");
			}
		}
	}
	/**
	 * Nuevo metodos para botones en vez de para togglebuttons
	 */
	@FXML
	private void botonesDireccionNew(MouseEvent event) {
		if(((Button)event.getSource()).isPressed()) {
			String message = ((Button)event.getSource()).getText();
			socketUtil.emit("movimiento", message);
		}else {
			socketUtil.emit("movimiento", "Parar");
		}
	}
	/**
	 * Metodo para el claxon
	 * */
	@FXML
	private void botonClaxon(MouseEvent event) {
		socketUtil.emit("claxon", ((Button)event.getSource()).isPressed());
	}
	/**
	 * Metodo de ejemplo para el tipo de control chachi
	 * */
	/*@FXML
	private void botonesDireccionPruebaBotonNormal(MouseEvent event) {
		String message = ((Button)event.getSource()).getText()+" "+((Button)event.getSource()).isPressed();
		
		String message1 = ((Button)event.getSource()).getText();
		String message2 = ((Button)event.getSource()).isPressed()+"";

		socketUtil.emit("pruebas", message1, message2);
	}*/

	/**
	 * Metodos que actualizan la interfaz
	 */
	private void actualizarAreaNotificaciones(final String msg) {
		areaNotificaciones.appendText(msg+"\n");
	}
	
	private void actualziarURLServidor() {
		serverFullURL = "http://"+serverHOST+":"+serverPORT;
		labelServidor.setText("Servidor: " + serverFullURL);
	}
}