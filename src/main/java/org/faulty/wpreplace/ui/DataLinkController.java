package org.faulty.wpreplace.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import lombok.Data;
import org.faulty.wpreplace.models.UnitDataLink;

public class DataLinkController {
    @FXML
    public Label typeLabel;
    public Text link16Details;


    public void initialize(UnitDataLink data) {
        typeLabel.setText("Type: " + data.getType());

        link16Details.setText("Link16:" + data.getLink16());

    }

    @Data
    public static final class Item {
        private final String type;
        private final int quantity;

    }
}
