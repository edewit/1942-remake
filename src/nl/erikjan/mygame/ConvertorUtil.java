package nl.erikjan.mygame;

import com.jme.system.DisplaySystem;
import com.jme.system.dummy.DummySystemProvider;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.ObjToJme;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 *
 * @author edewit
 */
public class ConvertorUtil {

    public static void main(String[] args) {
        DisplaySystem.getDisplaySystem(DummySystemProvider.DUMMY_SYSTEM_IDENTIFIER);

        URL model = ConvertorUtil.class.getClassLoader().getResource("fokker.obj");
        FormatConverter converter = new ObjToJme();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        FileOutputStream fos = null;
        try {
            converter.convert(model.openStream(), baos);
            fos = new FileOutputStream("fokker.jme");
            fos.write(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
            }
        }
    }
}
