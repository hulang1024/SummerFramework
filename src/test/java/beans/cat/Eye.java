package beans.cat;

public class Eye {
    private String color;

    public Eye() {
        System.out.println("new " + getClass().getName());
    }
    
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
