package beans.cat;

public class Head {
    private Eye eye;
    
    public Head() {
        System.out.println("new " + getClass().getName());
    }
    
    public Eye getEye() {
        return eye;
    }

    public void setEye(Eye eye) {
        this.eye = eye;
    }

}
