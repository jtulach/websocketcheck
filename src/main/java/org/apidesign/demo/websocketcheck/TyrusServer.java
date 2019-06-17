
package org.apidesign.demo.websocketcheck;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.grizzly.PortRange;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketAddOn;
import org.glassfish.grizzly.websockets.WebSocketApplication;
import org.glassfish.grizzly.websockets.WebSocketEngine;


final class TyrusServer extends HttpHandler {
    private static ServerConfiguration conf;
    private static HttpServer server;

    private TyrusServer() {
    }

    static TyrusServer initServer() throws Exception {
        server = HttpServer.createSimpleServer(null, new PortRange(8080, 65535));
        final WebSocketAddOn addon = new WebSocketAddOn();
        for (NetworkListener listener : server.getListeners()) {
            listener.registerAddOn(addon);
        }

        conf = server.getServerConfiguration();
        final TyrusServer dh = new TyrusServer();

        conf.addHttpHandler(dh, "/");

        server.start();
        return dh;
    }


    public URI getURI() {
        return pageURL("http", server, "/test.html");
    }

    @Override
    public void service(Request request, Response response) throws Exception {
        if ("/test.html".equals(request.getRequestURI())) {
            response.setContentType("text/html");
            final InputStream is = TyrusServer.class.getResourceAsStream("test.html");
            copyStream(is, response.getOutputStream(), null);
            return;
        }
    }

    URI registerWebSocket(Resource r) {
        WebSocketEngine.getEngine().register("", r.httpPath, new WS(r));
        return pageURL("ws", server, r.httpPath);
    }

    private static URI pageURL(String proto, HttpServer server, final String page) {
        NetworkListener listener = server.getListeners().iterator().next();
        int port = listener.getPort();
        try {
            return new URI(proto + "://localhost:" + port + page);
        } catch (URISyntaxException ex) {
            throw new IllegalStateException(ex);
        }
    }

    static final class Resource {

        final InputStream httpContent;
        final String httpType;
        final String httpPath;
        final String[] parameters;

        Resource(InputStream httpContent, String httpType, String httpPath,
            String... parameters) {
            httpContent.mark(Integer.MAX_VALUE);
            this.httpContent = httpContent;
            this.httpType = httpType;
            this.httpPath = httpPath;
            this.parameters = parameters;
        }
    }

    static void copyStream(InputStream is, OutputStream os, String baseURL, String... params) throws IOException {
        for (;;) {
            int ch = is.read();
            if (ch == -1) {
                break;
            }
            if (ch == '$' && params.length > 0) {
                int cnt = is.read() - '0';
                if (baseURL != null && cnt == 'U' - '0') {
                    os.write(baseURL.getBytes("UTF-8"));
                } else {
                    if (cnt >= 0 && cnt < params.length) {
                        os.write(params[cnt].getBytes("UTF-8"));
                    } else {
                        os.write('$');
                        os.write(cnt + '0');
                    }
                }
            } else {
                os.write(ch);
            }
        }
    }

    private static class WS extends WebSocketApplication {
        private final Resource r;

        private WS(Resource r) {
            this.r = r;
        }

        @Override
        public void onMessage(WebSocket socket, String text) {
            try {
                r.httpContent.reset();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                copyStream(r.httpContent, out, null, text);
                String s = new String(out.toByteArray(), "UTF-8");
                socket.send(s);
            } catch (IOException ex) {
                LOG.log(Level.WARNING, null, ex);
            }
        }
        private static final Logger LOG = Logger.getLogger(WS.class.getName());

    }
}
