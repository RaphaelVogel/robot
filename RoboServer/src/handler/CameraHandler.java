package handler;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tinkerforge.BrickServo;

import core.Action;
import core.Connection;
import core.Handler;

public class CameraHandler extends Handler {
	
	private short servo2 = (short)2; // camera left-right
	private short servo3 = (short)3; // camera up-down
	private short servo2And3 = (short)((1 << 2) | (1 << 3) | (1 << 7));
	
	private BrickServo servoBrick = Connection.getInstance().getServoBrick();
	private final static short CAMERA_MAX_POSITION = 20000;

	public CameraHandler(){
		servoBrick.setDegree(servo2, (short)0, CAMERA_MAX_POSITION); // RS servo has 200¡ rotation range
		servoBrick.setDegree(servo3, (short)0, CAMERA_MAX_POSITION); // RS servo has 200¡ rotation range
	}
	
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
	private String left(){
		servoBrick.setPosition(servo2, CAMERA_MAX_POSITION);
        servoBrick.enable(servo2);
		return "Move camera left";
	}

	@SuppressWarnings("unused")
	private String center(){
		servoBrick.setPosition(servo2And3, (short)(CAMERA_MAX_POSITION/2));
        servoBrick.enable(servo2And3);
		return "Center camera";
	}
	
	@SuppressWarnings("unused")
	private String right(){
		servoBrick.setPosition(servo2, (short)0);
        servoBrick.enable(servo2);
		return "Move camera right";
	}

	@SuppressWarnings("unused")
	private String up(){
		servoBrick.setPosition(servo3, CAMERA_MAX_POSITION);
        servoBrick.enable(servo3);
		return "Move camera up";
	}
	
	@SuppressWarnings("unused")
	private String down(){
		servoBrick.setPosition(servo3, (short)0);
        servoBrick.enable(servo3);
		return "Move camera down";
	}
}
