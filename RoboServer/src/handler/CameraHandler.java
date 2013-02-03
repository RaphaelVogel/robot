package handler;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tinkerforge.BrickServo;
import com.tinkerforge.BrickletDualRelay;

import core.Action;
import core.Connection;
import core.Handler;

public class CameraHandler extends Handler {
	
	private short servo2 = (short)2; // camera left-right
	private short servo3 = (short)3; // camera up-down
	private short servo2And3 = (short)((1 << 2) | (1 << 3) | (1 << 7));
	
	private BrickServo servoBrick = Connection.getInstance().getServoBrick();
	private final static short CAMERA_MAX_POSITION = 20000; //RS-2 servo has 200 degree rotation range
	private final static short DIRECTION_INCREMENT = 2500; // Camera moves 2500 units by each call
	
	private short currentLeftRightCameraPosition = CAMERA_MAX_POSITION/2;
	private short currentUpDownCameraPosition = CAMERA_MAX_POSITION/2;
	
	private BrickletDualRelay dualRelais = Connection.getInstance().getDualRelaisBricklet();
	
	@Override
	public void serve(Action action, HttpServletRequest request, HttpServletResponse response) {
		// /Camera/<direction>
		List<String> parameters = action.getParameters();
		if(parameters.size() != 1){
			sendResponse(HttpServletResponse.SC_BAD_REQUEST, "Wrong URL format, use /Camera/<direction>", response);
			return;
		}
		String direction = parameters.get(0);
		Object resultString = null;
		try {
			Method method = this.getClass().getDeclaredMethod(direction);
			resultString = method.invoke(this);
		} catch (Exception e) {
			sendResponse(HttpServletResponse.SC_BAD_REQUEST, "Could not call method Camera."+direction, response);
			return;
		}
		sendResponse(HttpServletResponse.SC_OK, (String)resultString, response);
		return;
	}

	@SuppressWarnings("unused")
	private String on(){
		dualRelais.setState(true, false);
		return "Switch Camera On";
	}
	
	@SuppressWarnings("unused")
	private String off(){
		dualRelais.setState(false, false);
		return "Switch Camera Off";
	}
	
	@SuppressWarnings("unused")
	private String initCamera(){
		servoBrick.setDegree(servo2, (short)0, CAMERA_MAX_POSITION);
		servoBrick.setDegree(servo3, (short)0, CAMERA_MAX_POSITION);
		center();
		return "Camera initialized";
	}
	
	@SuppressWarnings("unused")
	private String left(){
		if(currentLeftRightCameraPosition+DIRECTION_INCREMENT > CAMERA_MAX_POSITION){
			return "Camera cannot move further left";
		}
		currentLeftRightCameraPosition += DIRECTION_INCREMENT;
		servoBrick.setPosition(servo2, currentLeftRightCameraPosition);
        servoBrick.enable(servo2);
		return "Move camera left";
	}

	private String center(){
		currentLeftRightCameraPosition = CAMERA_MAX_POSITION/2;
		currentUpDownCameraPosition = CAMERA_MAX_POSITION/2;
		servoBrick.setPosition(servo2And3, (short)(CAMERA_MAX_POSITION/2));
        servoBrick.enable(servo2And3);
		return "Center camera";
	}
	
	@SuppressWarnings("unused")
	private String right(){
		if(currentLeftRightCameraPosition-DIRECTION_INCREMENT < 0){
			return "Camera cannot move further right";
		}
		currentLeftRightCameraPosition -= DIRECTION_INCREMENT;
		servoBrick.setPosition(servo2, currentLeftRightCameraPosition);
        servoBrick.enable(servo2);
		return "Move camera right";
	}

	@SuppressWarnings("unused")
	private String down(){
		if(currentUpDownCameraPosition-DIRECTION_INCREMENT < 0){
			return "Camera cannot move further up";
		}
		currentUpDownCameraPosition -= DIRECTION_INCREMENT;
		servoBrick.setPosition(servo3, currentUpDownCameraPosition);
        servoBrick.enable(servo3);
		return "Move camera up";
	}
	
	@SuppressWarnings("unused")
	private String up(){
		if(currentUpDownCameraPosition+DIRECTION_INCREMENT > CAMERA_MAX_POSITION){
			return "Camera cannot move further down";
		}
		currentUpDownCameraPosition += DIRECTION_INCREMENT;
		servoBrick.setPosition(servo3, currentUpDownCameraPosition);
        servoBrick.enable(servo3);
		return "Move camera down";
	}
}
