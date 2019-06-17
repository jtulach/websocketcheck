# Demo showing `WebSocket` access from JavaFX WebView

JavaFX WebView included in JDK8 has support for WebSocket connections
which works fine up until update 202. However, in the recent
updates 211 and 212, it got broken. This projects demonstrates that.

After checkout execute:
```bash
websocketcheck$ JAVA_HOME=/jdk1.8.0_202/ mvn package exec:exec
```
A websocket enabled HTTP server is started and prints the URL 
to use to connect to it. Then a JavaFX WebView window opens, connecting
to the URL. Then a default system browser is started connecting to the
same URL as well.

On JDK `1.8.0_202` everything works fine and both browsers show following content:
```html
WebSocket Check

connecting to ws://localhost:45489/app
connection established
init message sent
reply received: Ciao
```

However, when executing the test on JDK `1.8.0_212` or 211 the JavaFX window
never connects to the server:
```bash
websocketcheck$ JAVA_HOME=/jdk1.8.0_212/ mvn package exec:exec
```
