package summerframework.beans;

/**
 * 属性
 * @author hulang
 */
public class Property {
    private String name;
    private Object value;
    private ValueType valueType;
    
    /**
     * 属性值的类型
     */
    public static enum ValueType {
        SIMPLE, // 原始类型
        REF,    // 引用类型
        BEAN,
        LIST,
        SET,
        MAP;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
    
    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }
}
