/**
 * Created by yzw on 16/8/10.
 */
public class A {

    public static synchronized  void a(String[] args) {
        b();
    }

    public static  synchronized  void b(){
        System.out.println("xxx");
    }

    public static void main(String[] args) {
        a(null);
    }
}
