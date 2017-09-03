package beans.cat;

public class Leg {
    private String color;
    
    public Leg() {
        System.out.println("new " + getClass().getName());
    }
    
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
