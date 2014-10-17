package fi.helsinki.cs.tmc.client.core.testutil;

public class SimpleObject {
    
    private String value;
    
    public SimpleObject() { }
    
    public SimpleObject(final String value) {
        this.value = value;
    }
    
    public String getValue() {
        
        return value;
    }
    
    public void setValue(final String value) {
        
        this.value = value;
    }
}
