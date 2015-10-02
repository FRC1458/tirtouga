import edu.wpi.first.wpilibj.Joystick;

public class joystickTest {
	static Joystick right = new Joystick(0);
	static Joystick left = new Joystick(1);
	
	public static double sgn(double x) {
		if (x > 0) {
			return 1;
		} else if (x < 0) {
			return -1;
		} else {
			return 0;
		}
	}

	public static void main(String[] args) {
		while(true) {
			System.out.println("Right X:"+right.getRawAxis(0));
			System.out.println("Right Y:"+right.getRawAxis(1));
			System.out.println("Left X:"+left.getRawAxis(0));
			System.out.println("Left Y:"+left.getRawAxis(1));
		}

	}

}
