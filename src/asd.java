import java.util.ArrayList;
import java.util.Random;

public class asd {

	public static void main(String[] args) {
		Random rand = new Random();
		int num = 0;
		while(num<4) {
			num = rand.nextInt(5);
			System.out.println(num);
		}
	}

}
