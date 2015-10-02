public class mathTest2 {
	
	public static double sgn(double x) {
		if (x > 0) {
			return 1;
		} else if (x < 0) {
			return -1;
		} else {
			return 0;
		}
	}
	
	public static double fitToMotorRange(double value) {
		if (value>1) {
			value=1;
		} else if (value<-1) {
			value=-1;
		}
		return value;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double gyroAngle = 0.5*Math.PI;//clockwise, 0 is direction want to be from perspective of
		double y;
		double x;
		double m;
		double lFp;
		double rFp;
		double lRp;
		double rRp;
		double angle;
		double righty = 1;
		double rightx = 1;
		double turnSpeed=0;
		y = righty;
		x = rightx;
		m = Math.sqrt(x * x + y * y);
		if(m==0) {
			m=0.000000000001;
		}
		angle=Math.asin(y/m);
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

		angle = angle-gyroAngle;
		lFp=m*Math.sin(angle+Math.PI/4)+turnSpeed;
		rFp=m*Math.cos(angle+Math.PI/4)-turnSpeed;
		lRp=m*Math.cos(angle+Math.PI/4)+turnSpeed;
		rRp=m*Math.sin(angle+Math.PI/4)-turnSpeed;
		
		lFp=fitToMotorRange(lFp);
		rFp=fitToMotorRange(rFp);
		lRp=fitToMotorRange(lRp);
		rRp=fitToMotorRange(rRp);
		
		System.out.println("Left Front: "+lFp);
		System.out.println("Right Front: "+rFp);
		System.out.println("Left Rear: "+lRp);
		System.out.println("Right Rear: "+rRp);
	}
}