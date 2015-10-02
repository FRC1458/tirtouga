
public class mathTest {
	
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
		double y;
		double x;
		double m;
		double xa;
		double ya;
		double angle;
		double adjustAngle = Math.PI / 4;
		double righty = -1;
		double rightx = -1;
		
			y = righty;
			x = rightx;
			m = Math.sqrt(x * x + y * y);
			if(m==0) {
				m=0.000000000001;
			}
			angle = Math.asin(y / m);
			System.out.println("asin angle: "+angle*180/Math.PI);
			if (sgn(y)>=0&&sgn(x)>=0) {
				//Quadrant I
				angle = angle;
			} else if (sgn(x)<=0&&sgn(y)>=0) {
				//Quadrant II
				angle = Math.PI-angle;
			} else if(sgn(x)<=0&&sgn(y)<=0) {
				//Quadrant III
				angle+=Math.PI;
			} else if(sgn(x)>=0&&sgn(y)<=0) {
				//Quadrant IV
				angle=2*Math.PI-angle;
			}
			System.out.println("Preadjusted angle in degrees: "+angle*180/Math.PI);
			angle-=adjustAngle;
			xa=m*Math.cos(angle);
			ya=m*Math.sin(angle);
			System.out.println("m: "+m);
			System.out.println("angle: "+angle);
			System.out.println();
			System.out.println();
			System.out.println("Adjust x: "+xa);
			System.out.println("Adjust y: "+ya);
	}

}
