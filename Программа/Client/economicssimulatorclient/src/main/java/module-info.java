module org.example.economicssimulatorclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;
    requires javafx.graphics;      // если не было
    opens org.example.economicssimulatorclient.controller to javafx.fxml;
    opens org.example.economicssimulatorclient to javafx.fxml;
    exports org.example.economicssimulatorclient;
}