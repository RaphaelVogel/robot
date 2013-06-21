package core;

import handler.RoboStackHandler;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Dispatcher extends HttpServlet {
	private static Logger logger = Logger.getLogger(Dispatcher.class.getName());
	private static final long serialVersionUID = -1399448137949576706L;
	private static Map<String, Handler> handlers = new ConcurrentHashMap<String, Handler>();
	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Action action = Action.create(request.getPathInfo());
		try {
			Handler handler = handlers.get(action.getHandler());
			if(handler == null){
				@SuppressWarnings("unchecked")
				Class<Handler> clazz = (Class<Handler>) Class.forName("handler."+action.getHandler()+"Handler");
				Constructor<Handler> constr = clazz.getDeclaredConstructor();
				handler = constr.newInstance();
				handlers.put(action.getHandler(), handler);
			}	
			handler.serve(action, request, response);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setContentType("text/plain");
			response.getWriter().println("Could not initialize system");
			logger.log(Level.SEVERE, "Error during call of handler: "+action.getHandler()+"Handler", e);
			return;
		}
	}

	public static RoboStackHandler getRoboStackHandler(){
		return (RoboStackHandler)handlers.get("RoboStack");
	}
}
