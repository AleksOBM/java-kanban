# java-kanban

![Static Badge](https://img.shields.io/badge/Java-21-green)
![Static Badge](https://img.shields.io/badge/HttpServer-blue)
![Static Badge](https://img.shields.io/badge/NIO-green)
![Static Badge](https://img.shields.io/badge/Gson-c49654)
![Static Badge](https://img.shields.io/badge/CSV-567665)
![Static Badge](https://img.shields.io/badge/JUnit-5-orange)
![Static Badge](https://img.shields.io/badge/JavaScript-dfcd07)

## Трекер задач

**Учебный проект**

Идея

<img alt="view" src=".img/base-view.png" width="500"/>

Реализация

<img alt="frontend-kanban.png" src=".img/frontend-kanban.png" width="500"/>

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
(создается автоматически в корне проекта)

<img alt="data" src=".img/data-csv.png" width="600"/>

Есть возможность работы только в оперативной памяти, без csv   
История изменений всегда хранятся только в оперативной памяти  

### Frontend
- JavaScript

### HTTP server
Endpoints:
- /tasks
- /subtasks
- /epics
- /history
- /prioritized

### Console CLI
Commands:
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

### Как запустить
- Склонировать проект
- Добавить необходимые библиотеки

<img alt="libs.png" src=".img/libs.png" width="250"/>

- Запустить HttpTaskServer.java
- Открыть в браузере localhost:8080

Для использования консоли, запустить ConsoleCLI.java

