package summerframework.core.property;

public abstract class Property {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public abstract Object getValue();
}
