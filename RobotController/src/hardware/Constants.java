package hardware;

public interface Constants {

	// ESC servos
	public short servo0And1 = (short)((1 << 0) | (1 << 1) | (1 << 7));
	public short servo0 = (short)0;
	public short servo1 = (short)1;
	
	// IP Cam servos
	public short servo2 = (short)2; // camera left-right
	public short servo3 = (short)3; // camera up-down
	public short servo2And3 = (short)((1 << 2) | (1 << 3) | (1 << 7));
	
}
