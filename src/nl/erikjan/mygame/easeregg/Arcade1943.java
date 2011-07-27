package nl.erikjan.mygame.easeregg;

import cottage.Cottage;
import java.applet.Applet;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 *
 * @author edewit
 */
public class Arcade1943 extends Cottage {

    @Override
    protected void processKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == 18) {
            event.setKeyCode(17);
        }
        super.processKeyEvent(event);
    }

    static public void main(String argv[]) {
        final Applet applet = new Arcade1943();
        Frame frame = new Frame("easter egg");
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                applet.stop();
                applet.destroy();
                System.exit(0);
            }
        });
        frame.add("Center", applet);
        applet.setStub(new EasterEggAppletStub(argv, applet));
        frame.show();
        applet.init();
        applet.resize(448, 512);
        frame.setSize(448, 532);

//        WIDTH="224" HEIGHT="256"
//        width="448" height="512"
        applet.start();
//        frame.pack();
    }
}
