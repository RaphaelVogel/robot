package handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tinkerforge.BrickServo;
import com.tinkerforge.BrickletDistanceIR;

import core.Action;
import core.Handler;

public class CollisionHandler extends Handler {

	private short servo4 = (short)4;
	
	@Override
	public void serve(Action action, HttpServletRequest request,
			HttpServletResponse response) {
		// There is currently no need to control IR Sensor over UI
	}

	public String startCollisionDetection(){
		BrickServo servoBrick = StackHandler.getServoBrick();
		
		return "start collision detection";
	}
}
