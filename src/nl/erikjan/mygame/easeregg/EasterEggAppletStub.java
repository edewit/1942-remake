package nl.erikjan.mygame.easeregg;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class EasterEggAppletStub implements AppletStub {

    private HashMap<String, String> properties;
    private Applet applet;

    /**
     * Creates a new EasterEggAppletStub instance and initializes
     * the init parameters from the command line.
     * Arguments are passed in as name=value pairs.
     * Reading the command line arguments can be made more
     * sophisciated depending on your needs, but the basic
     * idea will likely remain the same.
     * Also, this particular implementation doesn't deal
     * very well with invalid name=value pairs.
     *
     * @param argv[] Command line arguments passed to Main
     * @param an Applet instance.
     */
    public EasterEggAppletStub(String argv[], Applet a) {
        applet = a;
        properties = new HashMap<String, String>();
        for (int i = 0; i < argv.length; i++) {
            try {
                StringTokenizer parser = new StringTokenizer(argv[i], "=");
                String name = parser.nextToken().toString();
                String value = parser.nextToken("\"").toString();
                value = value.substring(1);
                properties.put(name, value);
            } catch (NoSuchElementException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Calls the applet's resize
     * @param width
     * @param height
     * @return void
     */
    public void appletResize(int width, int height) {
        applet.resize(width, height);
    }

    /**
     * Returns the applet's context, which is
     * null in this case. This is an area where more
     * creative programming
     * work can be done to try and provide a context
     * @return AppletContext Always null
     */
    public AppletContext getAppletContext() {
        return null;
    }

    /**
     * Returns the CodeBase. If a host parameter
     * isn't provided
     * in the command line arguments, the URL is based
     * on InetAddress.getLocalHost().
     * The protocol is "file:"
     * @return URL
     */
    public java.net.URL getCodeBase() {
        String host;
        if ((host = getParameter("host")) == null) {
            try {
                host = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        java.net.URL u = null;
        try {
            u = new URL("file://" + host);
        } catch (Exception e) {
        }
        
        return u;
    }

    /**
     * Returns getCodeBase
     * @return URL
     */
    public java.net.URL getDocumentBase() {
        return getCodeBase();
    }

    /**
     * Returns the corresponding command line value
     * @return String
     */
    public String getParameter(String p) {
        return properties.get(p);
    }

    /**
     * Applet is always true
     * @return boolean True
     */
    public boolean isActive() {
        return true;
    }
}
