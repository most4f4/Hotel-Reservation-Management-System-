package ca.proj.Utility;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

public class TableColumnModifier {
    public static <T, P> void centerAlignColumn(TableColumn<T, P> column) {
        column.setCellFactory(col -> new TableCell<T, P>() {
            @Override
            protected void updateItem(P item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }
}
