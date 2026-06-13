# java-kanban

![Static Badge](https://img.shields.io/badge/Java-21-green)
![Static Badge](https://img.shields.io/badge/HttpServer-blue)
![Static Badge](https://img.shields.io/badge/Java--NIO-green)
![Static Badge](https://img.shields.io/badge/Gson-c49654)
![Static Badge](https://img.shields.io/badge/CSV-567665)
![Static Badge](https://img.shields.io/badge/JUnit-5-orange)
![Static Badge](https://img.shields.io/badge/Maven-orange)

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

