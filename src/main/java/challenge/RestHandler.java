package challenge;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

public interface RestHandler {
	boolean handle(Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException;
}
