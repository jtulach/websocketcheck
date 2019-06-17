package org.apidesign.demo.websocketcheck;

import java.io.ByteArrayInputStream;
import java.net.URI;

public class Main {
    public static void main(String... args) throws Exception {
        TyrusServer tyrus = TyrusServer.initServer();

        ByteArrayInputStream ciao = new ByteArrayInputStream("Ciao".getBytes());

        URI uri = tyrus.getURI();
        URI app = tyrus.registerWebSocket(new TyrusServer.Resource(ciao, null, "/app"));

        System.err.println("app: " + app);
        System.err.println("uri: " + uri);

        WebViewBrowser.showURI(uri);
        OpenBrowser.showURI(uri);

        System.in.read();
    }
}
