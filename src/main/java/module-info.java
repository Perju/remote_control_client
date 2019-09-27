module dam.proyecto.remote_control_client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
	requires socket.io.client;
	requires engine.io.client;

    opens dam.proyecto.remote_control_client to javafx.fxml,javafx.graphics;
    exports dam.proyecto.remote_control_client;
}