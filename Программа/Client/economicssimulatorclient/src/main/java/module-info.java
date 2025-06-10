module org.example.economicssimulatorclient {
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires java.net.http;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.databind;
    requires static lombok;
    requires java.prefs;
    requires org.json;
    requires exp4j;
    requires org.mockito;
    requires org.fxyz3d.core;
    requires javafx.swing;
    opens org.example.economicssimulatorclient.controller to javafx.fxml;
    opens org.example.economicssimulatorclient to javafx.fxml, org.testfx.junit5;
    opens org.example.economicssimulatorclient.config to javafx.fxml;
    opens org.example.economicssimulatorclient.dto to com.fasterxml.jackson.databind;
    exports org.example.economicssimulatorclient;
}