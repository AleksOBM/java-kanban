package ru.practicum.kanban.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.kanban.data.Endpoint;
import ru.practicum.kanban.data.Task;
import ru.practicum.kanban.manager.TaskManager;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {

	public HistoryHandler(TaskManager manager) {
		super(manager);
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String method = exchange.getRequestMethod();
		String path = exchange.getRequestURI().getPath();

		Endpoint endpoint = getEndpoint(method, path);

		switch (endpoint) {

			case GET_HISTORY -> {
				List<Task> historyList = manager.getHistory();

				// ✅ Пустая история — это нормально, отдаём 200 с пустым массивом
				if (historyList == null) {
					historyList = Collections.emptyList();
				}

				String jsonHistoryList;
				try {
					jsonHistoryList = gson.toJson(historyList);
				} catch (Exception exception) {
					sendServerError(exchange, endpoint);
					System.out.println(exception.getMessage());
					return;
				}

				sendText(exchange, endpoint, jsonHistoryList);
			}

			case UNKNOWN -> sendFormatException(exchange, endpoint, path);
		}
	}
}