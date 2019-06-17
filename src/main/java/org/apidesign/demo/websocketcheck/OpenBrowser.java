package org.apidesign.demo.websocketcheck;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

final class OpenBrowser {
    private OpenBrowser() {
    }

    public static void showURI(URI uri) throws IOException {
        try {
            Desktop.getDesktop().browse(uri);
        } catch (Exception ex) {
            Runtime.getRuntime().exec(new String[] { "xdg-open", uri.toASCIIString() });
        }
    }
}
