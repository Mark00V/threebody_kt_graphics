module com.example.threebody_kt_graphics {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.threebody_kt_graphics to javafx.fxml;
    exports com.example.threebody_kt_graphics;
}