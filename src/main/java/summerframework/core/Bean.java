package summerframework.core;

import java.util.List;

import summerframework.core.property.Property;

public class Bean {
    private String name;
    private String className;
    
    private List<Property> properties;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    
    public List<Property> getProperties() {
        return properties;
    }
    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

}
