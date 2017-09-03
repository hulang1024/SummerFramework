package beans.cat;

public class Cat {
    private static int id = 0;
    private String nickName;
    private Integer age;
    
    private Head head;
    private Body body;

    static {
        System.out.println("load " + Cat.class.getName());
    }
    
    public Cat() {
        System.out.println("new " + getClass().getName());
        id++;
    }

    public static int getId() {
        return id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

}
