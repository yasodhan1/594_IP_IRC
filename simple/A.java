import java.lang.System;
import java.lang.Runtime;

public class A {
    public static void main(String args[]) {
	Runtime.getRuntime().addShutdownHook(new Thread() {
		public void run() {
		    System.out.println("Exited!");
		}
	    });
	for(;;);
    }
}
