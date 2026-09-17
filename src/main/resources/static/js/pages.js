// ============================================================
// Страницы приложения
// ============================================================
const Pages = {

    // ============================================================
    // КАНБАН-ДОСКА (Подход A: эпики + задачи в одних колонках)
    // ============================================================
    async kanban() {
        try {
            const [epics, subtasks, tasks] = await Promise.all([
                API.getEpics(),
                API.getSubtasks(),
                API.getTasks(),
            ]);

            // Индекс сабтасок
            const subtaskById = new Map();
            (subtasks || []).forEach(st => subtaskById.set(st.id, st));

            // Нормализация статуса
            const normalizeStatus = (s) => {
                if (!s) return 'OPEN';
                const upper = String(s).toUpperCase().replace(/[\s-]/g, '_');
                if (['OPEN', 'NEW', 'TODO', 'TO_DO', 'BACKLOG'].includes(upper)) return 'OPEN';
                if (['IN_PROGRESS', 'INPROGRESS', 'DOING'].includes(upper)) return 'IN_PROGRESS';
                if (['DONE', 'CLOSED', 'COMPLETED', 'FINISHED'].includes(upper)) return 'DONE';
                console.warn(`Unknown status "${s}", treating as OPEN`);
                return 'OPEN';
            };

            const columns = [
                {status: 'OPEN', label: 'To do'},
                {status: 'IN_PROGRESS', label: 'In Progress'},
                {status: 'DONE', label: 'Done'},
            ];

            const columnsData = {
                OPEN: {epics: [], tasks: []},
                IN_PROGRESS: {epics: [], tasks: []},
                DONE: {epics: [], tasks: []},
            };

            // 1. Эпики — раскладываем по статусу, прикрепляем сабтаски
            (epics || []).forEach(epic => {
                const ids = epic.subtaskIds || [];
                epic.subtasks = ids
                    .map(id => subtaskById.get(id))
                    .filter(Boolean);

                const status = normalizeStatus(epic.status);
                columnsData[status].epics.push(epic);

                console.log(`Epic "${epic.title}" → ${status}, subtasks: ${epic.subtasks.length}`);
            });

            // 2. Обычные задачи
            (tasks || []).forEach(task => {
                const status = normalizeStatus(task.status);
                columnsData[status].tasks.push(task);
            });

            // 3. Сабтаски без эпика (epicId == null) → «виртуальный» эпик
            const orphanSubtasks = (subtasks || []).filter(st => st.epicId == null);
            if (orphanSubtasks.length > 0) {
                const orphanByStatus = {OPEN: [], IN_PROGRESS: [], DONE: []};
                orphanSubtasks.forEach(st => {
                    const status = normalizeStatus(st.status);
                    orphanByStatus[status].push(st);
                });
                Object.entries(orphanByStatus).forEach(([status, list]) => {
                    if (list.length === 0) return;
                    columnsData[status].epics.push({
                        id: null,
                        title: 'Без эпика',
                        description: null,
                        status,
                        duration: null,
                        isOrphan: true,
                        subtasks: list,
                    });
                });
            }

            console.log('Columns data:', columnsData);

            // 4. Рендер
            const columnsHtml = columns.map(col => {
                const data = columnsData[col.status];
                return this._renderKanbanColumn(col, data);
            }).join('');

            return `<div class="kanban-board">${columnsHtml}</div>`;
        } catch (error) {
            console.error('Kanban error:', error);
            return `<div class="error">❌ Ошибка загрузки доски: ${error.message}</div>`;
        }
    },

    // ------------------------------------------------------------
    // Колонка: эпики сверху, задачи снизу
    // ------------------------------------------------------------
    _renderKanbanColumn(column, data) {
        const {epics = [], tasks = []} = data || {};
        const totalCount = epics.length + tasks.length;

        const epicsHtml = epics.map(epic => this._renderKanbanEpic(epic)).join('');
        const tasksHtml = tasks.map(task => this._renderKanbanTask(task)).join('');

        const isEmpty = totalCount === 0;
        const bodyHtml = isEmpty
            ? '<div class="kanban-empty">none</div>'
            : `${epicsHtml}${tasksHtml}`;

        return `
            <div class="kanban-column" data-status="${column.status}">
                <div class="kanban-column-header">
                    <h3>${column.label}</h3>
                    <span class="kanban-count">${totalCount}</span>
                </div>
                <div class="kanban-column-body">
                    ${bodyHtml}
                </div>
                <button class="kanban-add"
                        onclick="window.App.createEpicInStatus('${column.status}')"
                        title="Создать эпик">
                    +
                </button>
            </div>
        `;
    },

    // ------------------------------------------------------------
    // Эпик внутри колонки
    // ------------------------------------------------------------
    _renderKanbanEpic(epic) {
        const subtasks = epic.subtasks || [];
        const isOrphan = epic.isOrphan === true;

        // Счётчик выполненных сабтасок
        const doneCount = subtasks.filter(st => {
            const s = String(st.status || '').toUpperCase();
            return s === 'DONE' || s === 'COMPLETED';
        }).length;
        const totalCount = subtasks.length;

        // Прогресс-бар
        const progressPercent = totalCount > 0
            ? Math.round((doneCount / totalCount) * 100)
            : 0;

        const subtasksHtml = subtasks.length > 0
            ? subtasks.map(st => this._renderKanbanSubtask(st)).join('')
            : '<div class="kanban-subtask-empty">Нет задач</div>';

        const onClickHeader = isOrphan
            ? ''
            : `onclick="event.stopPropagation(); window.App.navigateToEpic(${epic.id})"`;

        const menuButton = isOrphan
            ? ''
            : `<button class="kanban-menu-btn"
                   onclick="event.stopPropagation(); window.App.showEpicMenu(event, ${epic.id})"
                   title="Действия">
               ⋯
           </button>`;

        return `
        <div class="kanban-epic ${isOrphan ? 'kanban-epic-orphan' : ''}"
             data-epic-id="${epic.id ?? ''}">
            <div class="kanban-epic-header"
                 ${onClickHeader}
                 style="${isOrphan ? '' : 'cursor:pointer'}">
                <div class="kanban-epic-head-left">
                    <span class="kanban-epic-icon">🎯</span>
                    <span class="kanban-epic-title">
                        ${Components.escapeHtml(epic.title || 'Без названия')}
                    </span>
                </div>
                <div class="kanban-epic-head-right">
                    <span class="kanban-epic-counter"
                          title="Выполнено ${doneCount} из ${totalCount}">
                        ${doneCount}/${totalCount}
                    </span>
                    ${menuButton}
                </div>
            </div>
            ${totalCount > 0 ? `
                <div class="kanban-epic-progress">
                    <div class="kanban-epic-progress-bar"
                         style="width: ${progressPercent}%"></div>
                </div>
            ` : ''}
            <div class="kanban-epic-body">
                ${subtasksHtml}
            </div>
        </div>
    `;
    },

    // ------------------------------------------------------------
    // Обычная задача внутри колонки
    // ------------------------------------------------------------
    _renderKanbanTask(task) {
        return `
            <div class="kanban-task" data-task-id="${task.id}"
                 onclick="window.App.viewItem('task', ${task.id})"
                 style="cursor:pointer">
                <div class="kanban-task-title">
                    📋 ${Components.escapeHtml(task.title || `#${task.id}`)}
                </div>
                <div class="kanban-task-meta">
                    ${task.duration
            ? `<span class="kanban-subtask-duration">${task.duration}ч</span>`
            : ''}
                    ${task.startTime
            ? `<span class="kanban-subtask-duration">🕐 ${Components.formatDate(task.startTime)}</span>`
            : ''}
                </div>
            </div>
        `;
    },

    // ------------------------------------------------------------
    // Сабтаска внутри эпика
    // ------------------------------------------------------------
    _renderKanbanSubtask(st) {
        const isDone = String(st.status || '').toUpperCase() === 'DONE';
        const isInProgress = String(st.status || '').toUpperCase() === 'IN_PROGRESS';

        const statusIcon = isDone ? '✅' : (isInProgress ? '⚙️' : '○');

        return `
        <div class="kanban-subtask ${isDone ? 'kanban-subtask-done' : ''}"
             data-subtask-id="${st.id}"
             onclick="window.App.viewItem('subtask', ${st.id})"
             style="cursor:pointer">
            <span class="kanban-subtask-status">${statusIcon}</span>
            <span class="kanban-subtask-title">
                ${Components.escapeHtml(st.title || `#${st.id}`)}
            </span>
            ${st.duration
            ? `<span class="kanban-subtask-duration">${st.duration}ч</span>`
            : ''}
        </div>
    `;
    },

    // ============================================================
    // ЗАДАЧИ
    // ============================================================
    async tasks() {
        try {
            const tasks = await API.getTasks();
            return `
                <div class="page-header">
                    <h2>📋 Все задачи</h2>
                    <button class="btn btn-success" onclick="window.App.showCreateForm()">
                        ➕ Создать задачу
                    </button>
                </div>
                ${Components.taskList(tasks, {
                type: 'task',
                onView: true,
                onEdit: true,
                onDelete: true,
            })}
            `;
        } catch (error) {
            return `<div class="error">❌ Ошибка загрузки: ${error.message}</div>`;
        }
    },

    // ============================================================
    // САБТАСКИ
    // ============================================================
    async subtasks() {
        try {
            const subtasks = await API.getSubtasks();
            return `
                <div class="page-header">
                    <h2>📌 Сабтаски</h2>
                    <button class="btn btn-success" onclick="window.App.showSubtaskForm()">
                        ➕ Создать сабтаску
                    </button>
                </div>
                <div class="filters">
                    <input type="number" id="epic-filter"
                           placeholder="Фильтр по Epic ID"
                           style="width:180px">
                    <button class="btn btn-primary" onclick="window.App.filterSubtasks()">
                        Применить
                    </button>
                    <button class="btn" onclick="window.App.clearFilter()">
                        Сбросить
                    </button>
                </div>
                ${Components.taskList(subtasks, {
                type: 'subtask',
                showEpicId: true,
                onView: true,
                onEdit: true,
                onDelete: true,
            })}
            `;
        } catch (error) {
            return `<div class="error">❌ Ошибка загрузки: ${error.message}</div>`;
        }
    },

    // ============================================================
    // ЭПИКИ (список)
    // ============================================================
    async epics() {
        try {
            const epics = await API.getEpics();
            if (!epics || epics.length === 0) {
                return `
                    <div class="page-header">
                        <h2>🎯 Эпики</h2>
                        <button class="btn btn-success" onclick="window.App.showEpicForm()">
                            ➕ Создать эпик
                        </button>
                    </div>
                    <div class="empty">📭 Эпиков пока нет</div>
                `;
            }
            return `
                <div class="page-header">
                    <h2>🎯 Эпики</h2>
                    <button class="btn btn-success" onclick="window.App.showEpicForm()">
                        ➕ Создать эпик
                    </button>
                </div>
                ${Components.taskList(epics, {
                type: 'epic',
                clickable: true,
                onCardClick: 'window.App.navigateToEpic',
                onView: true,
                onEdit: true,
                onDelete: true,
            })}
            `;
        } catch (error) {
            return `<div class="error">❌ Ошибка загрузки: ${error.message}</div>`;
        }
    },

    // ============================================================
    // ЭПИК — страница с его сабтасками
    // ============================================================
    async epicDetail(epicId) {
        try {
            const [epic, subtasks] = await Promise.all([
                API.getEpic(epicId),
                API.getSubtasksByEpic(epicId),
            ]);

            const subtasksHtml = (subtasks && subtasks.length > 0)
                ? Components.taskList(subtasks, {
                    type: 'subtask',
                    onView: true,
                    onEdit: true,
                    onDelete: true,
                })
                : '<div class="empty">📭 У этого эпика пока нет сабтасок</div>';

            return `
                <div class="page-header">
                    <button class="btn" onclick="window.App.navigate('epics')">
                        ← Назад к эпикам
                    </button>
                </div>

                ${Components.epicHeader(epic, subtasks ? subtasks.length : 0)}

                <div class="page-header" style="margin-top:30px">
                    <h3>📌 Сабтаски эпика</h3>
                    <button class="btn btn-success"
                            onclick="window.App.showSubtaskFormForEpic(${epic.id})">
                        ➕ Создать сабтаску
                    </button>
                </div>

                ${subtasksHtml}
            `;
        } catch (error) {
            console.error(error);
            return `
                <div class="error">❌ Не удалось загрузить эпик #${epicId}: ${error.message}</div>
                <button class="btn" onclick="window.App.navigate('epics')">← Назад к эпикам</button>
            `;
        }
    },

    // ============================================================
    // ИСТОРИЯ
    // ============================================================
    async history() {
        try {
            const history = await API.getHistory();

            if (!history || history.length === 0) {
                return `
                    <div class="page-header">
                        <h2>📜 История</h2>
                    </div>
                    <div class="empty">📭 История пуста</div>
                `;
            }

            return `
                <div class="page-header">
                    <h2>📜 История просмотров</h2>
                </div>
                <p style="color:#718096;margin-bottom:20px">
                    Задачи, которые вы недавно открывали
                </p>
                ${Components.taskList(history, {
                type: 'task',
                onView: true,
                showEpicId: false,
            })}
            `;
        } catch (error) {
            return `<div class="error">❌ Ошибка загрузки: ${error.message}</div>`;
        }
    },

    // ============================================================
    // ПРИОРИТЕТЫ
    // ============================================================
    async prioritized() {
        try {
            const tasks = await API.getPrioritized();
            return `
                <div class="page-header">
                    <h2>⚡ Задачи по приоритету</h2>
                </div>
                ${Components.taskList(tasks, {
                type: 'task',
                onView: true,
            })}
            `;
        } catch (error) {
            return `<div class="error">❌ Ошибка загрузки: ${error.message}</div>`;
        }
    },
};

window.Pages = Pages;