// ============================================================
// UI-компоненты
// ============================================================
const Components = {

    // ============================================================
    // Карточка (для страниц /tasks, /subtasks, /epics)
    // ============================================================
    taskCard(task, options = {}) {
        const {
            onView = false,
            onEdit = false,
            onDelete = false,
            showEpicId = false,
            type = 'task',
            clickable = false,
            onCardClick = null,
        } = options;

        const statusClass = this.getStatusClass(task.status);
        const statusLabel = this.getStatusLabel(task.status);

        const iconMap = {task: '📋', subtask: '📌', epic: '🎯'};
        const icon = iconMap[type] || '📋';

        const cardStyle = clickable ? 'style="cursor:pointer"' : '';
        const cardClick = clickable && onCardClick
            ? `onclick="${onCardClick}(${task.id})"`
            : '';

        const epicTag = showEpicId && task.epicId
            ? `<span class="task-tag tag-epic"
                     onclick="event.stopPropagation(); window.App.navigateToEpic(${task.epicId})"
                     style="cursor:pointer"
                     title="Перейти к эпику">
                   🔗 Эпик #${task.epicId}
               </span>`
            : '';

        const viewButton = onView
            ? (type === 'epic'
                ? `<button class="btn btn-primary"
                           onclick="event.stopPropagation(); window.App.navigateToEpic(${task.id})">
                       📂 Открыть
                   </button>`
                : `<button class="btn btn-primary"
                           onclick="event.stopPropagation(); window.App.viewItem('${type}', ${task.id})">
                       👁 Детали
                   </button>`)
            : '';

        return `
            <div class="task-card" data-id="${task.id}" ${cardStyle} ${cardClick}>
                <div class="task-title">${icon} ${this.escapeHtml(task.title || `#${task.id}`)}</div>
                ${task.description ? `<div class="task-description">${this.escapeHtml(task.description)}</div>` : ''}
                <div class="task-meta">
                    ${task.status ? `<span class="task-tag ${statusClass}">${statusLabel}</span>` : ''}
                    ${task.duration ? `<span class="task-tag tag-priority">${task.duration}ч</span>` : ''}
                    ${task.startTime ? `<span class="task-tag">🕐 ${this.formatDate(task.startTime)}</span>` : ''}
                    ${epicTag}
                </div>
                ${(onView || onEdit || onDelete) ? `
                    <div class="task-actions">
                        ${viewButton}
                        ${onEdit ? `<button class="btn btn-primary"
                                            onclick="event.stopPropagation(); window.App.editItem('${type}', ${task.id})">
                                       ✏ Изменить
                                   </button>` : ''}
                        ${onDelete ? `<button class="btn btn-danger"
                                             onclick="event.stopPropagation(); window.App.deleteItem('${type}', ${task.id})">
                                        🗑 Удалить
                                     </button>` : ''}
                    </div>
                ` : ''}
            </div>
        `;
    },

    // ============================================================
    // Список карточек
    // ============================================================
    taskList(tasks, options = {}) {
        if (!tasks || tasks.length === 0) {
            return '<div class="empty">📭 Нет данных</div>';
        }
        return `<div class="task-grid">${tasks.map(t => this.taskCard(t, options)).join('')}</div>`;
    },

    // ============================================================
    // Форма создания/редактирования
    // ============================================================
    taskForm(data = {}, options = {}) {
        const {showEpicId = false, isEdit = false, type = 'task'} = options;

        const titleMap = {
            task: isEdit ? 'Редактировать задачу' : 'Создать задачу',
            subtask: isEdit ? 'Редактировать сабтаску' : 'Создать сабтаску',
            epic: isEdit ? 'Редактировать эпик' : 'Создать эпик',
        };
        const formTitle = titleMap[type] || titleMap.task;

        return `
            <form id="task-form" onsubmit="window.App.handleFormSubmit(event)">
                <h2 style="margin:0 0 20px 0;color:#fff">${formTitle}</h2>

                <input type="hidden" name="id" value="${data.id || ''}">
                <input type="hidden" name="__type" value="${type}">

                <div class="form-group">
                    <label class="form-label">Название *</label>
                    <input type="text" class="form-control" name="title"
                           value="${this.escapeHtml(data.title || '')}" required>
                </div>

                <div class="form-group">
                    <label class="form-label">Описание</label>
                    <textarea class="form-control" name="description" rows="3">${this.escapeHtml(data.description || '')}</textarea>
                </div>

                <div class="form-group">
                    <label class="form-label">Статус</label>
                    <select class="form-control" name="status">
                        <option value="">Не указан</option>
                        <option value="NEW"         ${data.status === 'NEW' ? 'selected' : ''}>Новая</option>
                        <option value="IN_PROGRESS" ${data.status === 'IN_PROGRESS' ? 'selected' : ''}>В работе</option>
                        <option value="DONE"        ${data.status === 'DONE' ? 'selected' : ''}>Выполнена</option>
                    </select>
                </div>

                ${showEpicId ? `
                    <div class="form-group">
                        <label class="form-label">ID Эпика</label>
                        <input type="number" class="form-control" name="epicId"
                               value="${data.epicId != null ? data.epicId : ''}">
                    </div>
                ` : ''}

                <div class="form-group">
                    <label class="form-label">Длительность (часы)</label>
                    <input type="number" class="form-control" name="duration"
                           value="${data.duration != null ? data.duration : ''}">
                </div>

                <div class="form-actions">
                    <button type="button" class="btn" onclick="window.App.closeModal()">Отмена</button>
                    <button type="submit" class="btn btn-success">
                        ${isEdit ? 'Сохранить' : 'Создать'}
                    </button>
                </div>
            </form>
        `;
    },

    // ============================================================
    // Детали (модалка)
    // ============================================================
    taskDetails(task) {
        if (!task) return '<div class="error">Задача не найдена</div>';

        const statusClass = this.getStatusClass(task.status);
        const statusLabel = this.getStatusLabel(task.status);

        return `
            <h2 style="margin:0 0 12px 0;color:#fff">Детали #${task.id}</h2>
            <h3 style="margin:0 0 12px 0;color:#cbd5e0">
                ${this.escapeHtml(task.title || 'Без названия')}
            </h3>
            ${task.description
            ? `<p style="color:#a0aec0;line-height:1.6;margin-bottom:16px">${this.escapeHtml(task.description)}</p>`
            : ''}
            <div class="task-meta" style="margin-bottom:20px">
                ${task.status ? `<span class="task-tag ${statusClass}">${statusLabel}</span>` : ''}
                ${task.duration ? `<span class="task-tag tag-priority">${task.duration}ч</span>` : ''}
                ${task.epicId
            ? `<span class="task-tag tag-epic"
                             onclick="window.App.closeModal(); window.App.navigateToEpic(${task.epicId})"
                             style="cursor:pointer">
                           🔗 Эпик #${task.epicId}
                       </span>`
            : ''}
                ${task.startTime ? `<span class="task-tag">🕐 ${this.formatDate(task.startTime)}</span>` : ''}
            </div>
            <div class="form-actions">
                <button class="btn" onclick="window.App.closeModal()">Закрыть</button>
            </div>
        `;
    },

    // ============================================================
    // Шапка страницы эпика
    // ============================================================
    epicHeader(epic, subtasksCount = 0) {
        const statusClass = this.getStatusClass(epic.status);
        const statusLabel = this.getStatusLabel(epic.status);

        return `
            <div class="epic-detail-header">
                <h2>🎯 ${this.escapeHtml(epic.title || `Эпик #${epic.id}`)}</h2>
                ${epic.description
            ? `<p class="epic-description">${this.escapeHtml(epic.description)}</p>`
            : ''}
                <div class="task-meta">
                    ${epic.status ? `<span class="task-tag ${statusClass}">${statusLabel}</span>` : ''}
                    ${epic.duration ? `<span class="task-tag tag-priority">${epic.duration}ч</span>` : ''}
                    <span class="task-tag tag-epic">ID: ${epic.id}</span>
                    <span class="task-tag">📌 Сабтасок: ${subtasksCount}</span>
                </div>
            </div>
        `;
    },

    // ============================================================
    // Вспомогательные
    // ============================================================
    getStatusClass(status) {
        const map = {
            'OPEN': 'tag-status-open',
            'NEW': 'tag-status-open',
            'IN_PROGRESS': 'tag-status-in_progress',
            'DONE': 'tag-status-done',
        };
        return map[status] || '';
    },

    getStatusLabel(status) {
        const map = {
            'OPEN': 'Открыта',
            'NEW': 'Новая',
            'IN_PROGRESS': 'В работе',
            'DONE': 'Выполнена',
        };
        return map[status] || status || 'Не указан';
    },

    formatDate(isoString) {
        if (!isoString) return '';
        try {
            const date = new Date(isoString);
            if (isNaN(date.getTime())) return isoString;
            return date.toLocaleString('ru-RU', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit',
            });
        } catch {
            return isoString;
        }
    },

    escapeHtml(text) {
        if (text == null) return '';
        return String(text)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    },
};

window.Components = Components;