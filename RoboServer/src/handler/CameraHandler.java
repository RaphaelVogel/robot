package handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tinkerforge.BrickServo;

import core.Action;
import core.Connection;
import core.Handler;

public class CameraHandler extends Handler {

	
	private short servo2 = (short)2;
	private short servo3 = (short)3;
	
	private BrickServo servoBrick = Connection.getInstancxe().getServoBrick();

	@Override
	public void serve(Action action, HttpServletRequest request, HttpServletResponse response) {
		
	}
	

}
