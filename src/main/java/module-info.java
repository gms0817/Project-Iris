module com.iris {
    requires javafx.controls;
    requires javafx.fxml;
            
        requires org.controlsfx.controls;
                            
    opens com.iris to javafx.fxml;
    exports com.iris;
}