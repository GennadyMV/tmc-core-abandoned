package fi.helsinki.cs.tmc.client.core.io.zip.decider;


public class ZipEverythingDecider implements ZippingDecider {
    
    @Override
    public boolean shouldZip(String zipPath) {
        return true;
    }

}
