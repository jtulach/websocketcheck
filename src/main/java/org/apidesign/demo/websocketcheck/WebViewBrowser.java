package org.apidesign.demo.websocketcheck;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class WebViewBrowser extends Application {
    private static final Logger LOG = Logger.getLogger(WebViewBrowser.class.getName());

    static void showURI(URI uri) {
        Executors.newSingleThreadExecutor().execute(() -> {
            launch(WebViewBrowser.class, new String[]{uri.toASCIIString()});
        });
    }

    private BorderPane root;
    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        start(primaryStage, this.getParameters().getRaw().get(0));
    }

    final void start(Stage primaryStage, String url) {
        this.stage = primaryStage;
        this.root = new BorderPane();

        Object[] arr = findInitialSize();
        Scene scene = new Scene(root, (Double)arr[2], (Double)arr[3]);
        primaryStage.setScene(scene);
        primaryStage.setX((Double)arr[0]);
        primaryStage.setY((Double)arr[1]);

        final WebView view = new WebView();
        view.setContextMenuEnabled(false);
        view.getEngine().load(url);
        root.setCenter(view);

        primaryStage.show();
    }

    private static Object[] findInitialSize() {
        Rectangle2D screen = Screen.getPrimary().getBounds();
        double x = screen.getWidth() * 0.05;
        double y = screen.getHeight() * 0.05;
        double width = screen.getWidth() * 0.9;
        double height = screen.getHeight() * 0.9;

        Object[] arr = {
            x, y, width, height, null
        };
        return arr;
    }
}
