package com.classagendag2.features.task.presentation.json;

import com.classagendag2.features.task.domain.model._Task;
import com.classagendag2.features.task.domain.model.TaskPriority;
import com.classagendag2.features.task.domain.model.TaskStatus;

import java.util.List;

public final class _TaskJson {

    public record ParsedTask(
            String title,
            String description,
            String dueDate,
            TaskStatus status,
            TaskPriority priority
    ) {}

    public static ParsedTask fromJson(String json) {
        String title = null;
        String description = null;
        String dueDate = null;
        TaskStatus status = null;
        TaskPriority priority = null;

        json = json.replace("{", "").replace("}", "");
        String[] parts = json.split(",");

        for (String p : parts) {
            String[] kv = p.split(":");
            if (kv.length < 2) continue;

            String key = kv[0].replace("\"", "").trim();
            String value = kv[1].replace("\"", "").trim();

            switch (key) {
                case "title" -> title = value;
                case "description" -> description = value;
                case "dueDate" -> dueDate = value;
                case "status" -> status = TaskStatus.valueOf(value);
                case "priority" -> priority = TaskPriority.valueOf(value);
            }
        }

        return new ParsedTask(title, description, dueDate, status, priority);
    }

    public static String toJson(_Task t) {
        String descriptionJson =
                t.getDescription() == null ? "null" : "\"" + t.getDescription() + "\"";

        return """
        {
          "id": %d,
          "ownerUserId": %d,
          "title": "%s",
          "description": %s,
          "dueDate": "%s",
          "status": "%s",
          "priority": "%s",
          "createdAt": "%s",
          "access": {
            "scope": "OWN",
            "permission": "OWNER"
          }
        }
        """.formatted(
                t.getId(),
                t.getOwnerId(),
                t.getTitle(),
                descriptionJson,
                t.getDueDate(),
                t.getStatus().name(),
                t.getPriority().name(),
                t.getCreatedAt()
        );
    }

    public static String toJsonList(List<_Task> list) {
        StringBuilder sb = new StringBuilder("{\"items\":[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(toJson(list.get(i)));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]}");
        return sb.toString();
    }
}
