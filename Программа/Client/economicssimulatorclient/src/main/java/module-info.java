module org.example.economicssimulatorclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires java.net.http;
    requires javafx.graphics;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.databind;
    requires static lombok;
    requires java.prefs;
    requires org.json;      // если не было
    opens org.example.economicssimulatorclient.controller to javafx.fxml;
    opens org.example.economicssimulatorclient to javafx.fxml;
    opens org.example.economicssimulatorclient.config to javafx.fxml;
    opens org.example.economicssimulatorclient.dto to com.fasterxml.jackson.databind;
    exports org.example.economicssimulatorclient;
}