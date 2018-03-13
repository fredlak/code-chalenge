package challenge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class HttpHandler extends AbstractHandler {
	
	private static final String REST_COMMAND_ADD_MESSAGE = "/add-message";
	private static final String REST_COMMAND_ADD_FOLLOW_USER = "/add-follow-user";
	private static final String REST_COMMAND_SHOW_MESSAGES = "/show-messages";
	private static final String REST_COMMAND_SHOW_FOLLOW_USERS_MESSAGES = "/show-follow-users-messages";
	private static final String GET_PARAM_USER_NAME = "user";
	private static final String GET_PARAM_MESSAGE = "message";
	private static final String GET_PARAM_FOLLOW_USER = "follow-user";
	private static final String GET_PARAM_REVERSE_ORDER = "reverse-order";

	private final Map<String, User> users;
	private final Map<String, RestHandler> restHandlers;

	public HttpHandler() {
		this.users = new ConcurrentHashMap<String, User>();
		this.restHandlers = new ConcurrentHashMap<String, RestHandler>();
		addUserMessageHandler();
		addFollowUserHandler();
		addShowMessagesHandler();
		addShowFollowMessagesHandler();
	}
	
	public static String generateJsonFromObject(String header, Object object) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		JsonElement jsonElement = gson.toJsonTree(object);
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(header, jsonElement);
		return jsonObject.toString();
	}

	private static List<Message> getReversedList(List<Message> source) {
		List<Message> result = new ArrayList<Message>();
		result.addAll(source);
		Collections.sort(result, new ReverseDateComparator());
		return result;
	}

	private User getOrAddUser(String userName) {
		User result = null;
		if (users.containsKey(userName)) {
			result = users.get(userName);
		} else {
			result = new User(userName);
			users.put(userName, result);
		}
		return result;
	}

	private void addUserMessageHandler() {
		this.restHandlers.put(REST_COMMAND_ADD_MESSAGE, new RestHandler() {
			public boolean handle(Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
				boolean result = false;
				final String userName = request.getParameter(GET_PARAM_USER_NAME);
				final String userMessage = request.getParameter(GET_PARAM_MESSAGE);
				if (userName != null && userMessage != null) {
					getOrAddUser(userName).addMessage(new Message(new Date(System.currentTimeMillis()), userMessage));
					response.getWriter().println(generateJsonFromObject("Request status", new Status(true, "OK")));
					result = true;
				}
				return result;
			}
		});
	}

	private void addFollowUserHandler() {
		this.restHandlers.put(REST_COMMAND_ADD_FOLLOW_USER, new RestHandler() {
			public boolean handle(Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
				boolean result = false;
				final String userName = request.getParameter(GET_PARAM_USER_NAME);
				final String followUserName = request.getParameter(GET_PARAM_FOLLOW_USER);
				if (userName != null && followUserName != null) {
					User user = getOrAddUser(userName);
					user.addFollowUser(getOrAddUser(followUserName));
					response.getWriter().println(generateJsonFromObject("Request status", new Status(true, "OK")));
					result = true;
				}
				return result;
			}
		});
	}
	
	private void addShowMessagesHandler() {
		this.restHandlers.put(REST_COMMAND_SHOW_MESSAGES, new RestHandler() {
			public boolean handle(Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
				boolean result = false;
				final String userName = request.getParameter(GET_PARAM_USER_NAME);
				final String reverseOrder = request.getParameter(GET_PARAM_REVERSE_ORDER);
				if (userName != null) {
					if (users.containsKey(userName)) {
						User user = users.get(userName);
						if (reverseOrder != null) {
							response.getWriter().println(generateJsonFromObject(userName, getReversedList(user.getMessages())));
						} else {
							response.getWriter().println(generateJsonFromObject(userName, user.getMessages()));	
						}
						
						result = true;
					}
				}
				return result;
			}
		});
	}

	private void addShowFollowMessagesHandler() {
		this.restHandlers.put(REST_COMMAND_SHOW_FOLLOW_USERS_MESSAGES, new RestHandler() {
			public boolean handle(Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
				boolean result = false;
				final String userName = request.getParameter(GET_PARAM_USER_NAME);
				final String reverseOrder = request.getParameter(GET_PARAM_REVERSE_ORDER);
				if (userName != null) {
					if (users.containsKey(userName)) {
						User user = users.get(userName);
						Map<String, List<Message>> outputMap = new HashMap<String, List<Message>>();
						result = true;
						for (User followUser : user.getFollowUsers()) {
							if (reverseOrder != null) {
								outputMap.put(followUser.getUserName(), getReversedList(followUser.getMessages()));
							} else {
								outputMap.put(followUser.getUserName(), followUser.getMessages());
							}
						}
						response.getWriter().println(generateJsonFromObject(userName, outputMap));

					}
				}
				return result;
			}
		});
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		baseRequest.setHandled(true);
		response.setContentType("text/html;charset=utf-8");

		if (restHandlers.containsKey(target)) {
			if (restHandlers.get(target).handle(baseRequest, request, response)) {
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
				response.getWriter().println(generateJsonFromObject("Request status", new Status(false, "REQUEST FAILED")));
			}
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(generateJsonFromObject("Request status", new Status(false, "UNKNOWN REQUEST")));
		}

	}

}
