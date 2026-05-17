# java-kanban

## Бэкэнд трекера задач

![view](base-view.png)

### Типы задач
- Подзадача (Subtask)
- Задача (Task)
- Эпик (Epic)

### Статусы задач
- NEW
- IN_PROGRESS
- DONE

### Основные возможности
- Добавление задач
- Удаление задач
- Обновление задач
- Просмотр истории изменения задач
- Просмотр в порядке приоритетности

### Хранение данных
Данные задач хранятся в таблице CSV  
![data](data-csv.png)
Файл и каталог с таблицей создаются автоматически  
в /src/autosave/data.csv  
Есть возможность работы только в оперативной памяти, без csv   
История изменений всегда хранятся только в оперативной памяти  

### Управление
Основное - через HTTP API  
Эндпоинты:
- /tasks
- /subtasks
- /epics
- /history
- /prioritized

Дополнительное - через CLI  
Команды:
- help
- update
- print
- history
- find
- add-task
- add-epic
- add-sub
- set-status
- set-time
- set-dur
- remove
- exit

### Примененные технологии
- Java 21
- HttpServer
- google.Gson
- java.nio.file
- junit.jupiter
