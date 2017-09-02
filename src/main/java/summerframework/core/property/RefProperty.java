package summerframework.core.property;

public class RefProperty extends Property {
    private Object ref;

    public Object getRef() {
        return ref;
    }

    public void setRef(Object ref) {
        this.ref = ref;
    }

    @Override
    public Object getValue() {
        return ref;
    }
}
