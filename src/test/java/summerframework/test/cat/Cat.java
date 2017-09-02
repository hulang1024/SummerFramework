package summerframework.test.cat;

public class Cat {
    private static int id = 0;
    private String nickName;
    private Integer age;
    private Body body;

    public Cat() {
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

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

}
