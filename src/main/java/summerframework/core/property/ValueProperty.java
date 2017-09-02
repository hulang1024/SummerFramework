package summerframework.core.property;

public class ValueProperty extends Property {
    private Object value;
    private int valueType;
    
    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    public int getValueType() {
        return valueType;
    }
    public void setValueType(int valueType) {
        this.valueType = valueType;
    }

}
