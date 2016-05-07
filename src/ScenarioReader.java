import java.io.*;
import javax.xml.bind.annotation.*;

public class ScenarioReader {
    public static void main(String[] args) {
        RootElementClass adr = new RootElementClass();
        FileInputStream adrFile = null;
        String scenarioFile = args[0];
        try {
            adrFile = new FileInputStream(scenarioFile);
            JAXBContext ctx = JAXBContext.newInstance(RootElementClass.class);
            Unmarshaller um = ctx.createUnmarshaller();
            adr = (RootElementClass) um.unmarshal(adrFile);
        }
        catch(IOException exc) {
        }
        catch(JAXBException exc) {
        }
        finally {
        }
    }
}