// ============================================================
// API клиент
// ============================================================
const API = {
    // Пустая строка = тот же origin (localhost:8080)
    baseURL: '',

    // ------------------------------------------------------------
    // Универсальный запрос
    // ------------------------------------------------------------
    async request(endpoint, options = {}) {
        const url = this.baseURL + endpoint;

        try {
            const response = await fetch(url, {
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers,
                },
                ...options,
            });

            // 204 No Content
            if (response.status === 204) {
                return null;
            }

            if (!response.ok) {
                const errorText = await response.text().catch(() => '');
                const message = errorText.trim() || `HTTP ${response.status} ${response.statusText}`;
                throw new Error(message);
            }

            // Пустое тело
            const contentLength = response.headers.get('content-length');
            if (contentLength === '0') {
                return null;
            }

            const contentType = response.headers.get('content-type') || '';

            // JSON — парсим как JSON
            if (contentType.includes('application/json')) {
                const text = await response.text();
                if (!text) return null;
                try {
                    return JSON.parse(text);
                } catch (e) {
                    console.warn('Invalid JSON from server:', text.substring(0, 100));
                    return text;
                }
            }

            // Не JSON — пробуем распарсить, если похоже
            const text = await response.text();
            try {
                return text ? JSON.parse(text) : null;
            } catch {
                return text;
            }
        } catch (error) {
            console.error(`API Error [${options.method || 'GET'} ${url}]:`, error);
            throw error;
        }
    },

    // ============================================================
    // GET
    // ============================================================
    getTasks() {
        return this.request('/tasks');
    },
    getTask(id) {
        return this.request(`/tasks/${id}`);
    },
    getSubtasks() {
        return this.request('/subtasks');
    },
    getSubtask(id) {
        return this.request(`/subtasks/${id}`);
    },
    getSubtasksByEpic(epicId) {
        return this.request(`/epics/${epicId}/subtasks`);
    },
    getEpics() {
        return this.request('/epics');
    },
    getEpic(id) {
        return this.request(`/epics/${id}`);
    },
    getHistory() {
        return this.request('/history');
    },
    getPrioritized() {
        return this.request('/prioritized');
    },

    // ============================================================
    // POST — создание
    // ============================================================
    createTask(data) {
        return this.request('/tasks', {method: 'POST', body: JSON.stringify(data)});
    },
    createSubtask(data) {
        return this.request('/subtasks', {method: 'POST', body: JSON.stringify(data)});
    },
    createEpic(data) {
        return this.request('/epics', {method: 'POST', body: JSON.stringify(data)});
    },

    // ============================================================
    // POST — обновление
    // ============================================================
    updateTask(id, data) {
        return this.request(`/tasks/${id}`, {method: 'POST', body: JSON.stringify(data)});
    },
    updateSubtask(id, data) {
        return this.request(`/subtasks/${id}`, {method: 'POST', body: JSON.stringify(data)});
    },
    updateEpic(id, data) {
        return this.request(`/epics/${id}`, {method: 'POST', body: JSON.stringify(data)});
    },

    // ============================================================
    // DELETE
    // ============================================================
    deleteTask(id, data) {
        return this.request(`/tasks/${id}`, {
            method: 'DELETE',
            body: data ? JSON.stringify(data) : undefined
        });
    },
    deleteSubtask(id, data) {
        return this.request(`/subtasks/${id}`, {
            method: 'DELETE',
            body: data ? JSON.stringify(data) : undefined
        });
    },
    deleteEpic(id, data) {
        return this.request(`/epics/${id}`, {
            method: 'DELETE',
            body: data ? JSON.stringify(data) : undefined
        });
    },

    // ============================================================
    // Утилиты по типу
    // ============================================================
    getByType(type, id) {
        switch (type) {
            case 'epic':
                return this.getEpic(id);
            case 'subtask':
                return this.getSubtask(id);
            case 'task':
            default:
                return this.getTask(id);
        }
    },
    createByType(type, data) {
        switch (type) {
            case 'epic':
                return this.createEpic(data);
            case 'subtask':
                return this.createSubtask(data);
            case 'task':
            default:
                return this.createTask(data);
        }
    },
    updateByType(type, id, data) {
        switch (type) {
            case 'epic':
                return this.updateEpic(id, data);
            case 'subtask':
                return this.updateSubtask(id, data);
            case 'task':
            default:
                return this.updateTask(id, data);
        }
    },
    deleteByType(type, id, data) {
        switch (type) {
            case 'epic':
                return this.deleteEpic(id, data);
            case 'subtask':
                return this.deleteSubtask(id, data);
            case 'task':
            default:
                return this.deleteTask(id, data);
        }
    },
};

window.API = API;