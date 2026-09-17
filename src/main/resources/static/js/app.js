// ============================================================
// Главное приложение
// ============================================================
class App {
    constructor() {
        this.currentPage = 'kanban';
        this.currentEpicId = null;
        this.currentModalContext = null;

        this.modal = document.getElementById('modal');
        this.content = document.getElementById('content');
        this.modalBody = document.getElementById('modal-body');

        this.init();
    }

    init() {
        document.querySelectorAll('.nav-link').forEach(link => {
            link.addEventListener('click', () => {
                this.navigate(link.dataset.page);
            });
        });

        document.querySelector('.modal-close')
            ?.addEventListener('click', () => this.closeModal());

        this.modal?.addEventListener('click', (e) => {
            if (e.target === this.modal) this.closeModal();
        });

        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && this.modal?.style.display === 'flex') {
                this.closeModal();
            }
        });

        window.addEventListener('hashchange', () => this.handleHashChange());

        window.App = this;
        this.handleHashChange();
    }

    handleHashChange() {
        const hash = window.location.hash;

        const epicMatch = hash.match(/^#\/epics\/(\d+)$/);
        if (epicMatch) {
            const epicId = Number(epicMatch[1]);
            this.renderEpicDetail(epicId);
            return;
        }

        const pageMatch = hash.match(/^#\/(\w+)$/);
        if (pageMatch) {
            const page = pageMatch[1];
            if (Pages[page]) {
                this.navigate(page, false);
                return;
            }
        }

        this.navigate('kanban', false);
    }

    async navigate(page, updateHash = true) {
        this.currentPage = page;
        this.currentEpicId = null;

        document.querySelectorAll('.nav-link').forEach(link => {
            link.classList.toggle('active', link.dataset.page === page);
        });

        if (updateHash) {
            const newHash = `#/${page}`;
            if (window.location.hash !== newHash) {
                history.pushState(null, '', newHash);
            }
        }

        this.content.innerHTML = '<div class="loading">Загрузка...</div>';

        try {
            if (Pages[page]) {
                const html = await Pages[page]();
                this.content.innerHTML = html;
            } else {
                this.content.innerHTML = '<div class="error">Страница не найдена</div>';
            }
        } catch (error) {
            console.error(error);
            this.content.innerHTML = `<div class="error">❌ Ошибка: ${error.message}</div>`;
        }
    }

    navigateToEpic(epicId) {
        const newHash = `#/epics/${epicId}`;
        if (window.location.hash === newHash) {
            this.renderEpicDetail(epicId);
        } else {
            window.location.hash = newHash;
        }
    }

    async renderEpicDetail(epicId) {
        this.currentPage = 'epic-detail';
        this.currentEpicId = epicId;

        document.querySelectorAll('.nav-link').forEach(link => {
            link.classList.toggle('active', link.dataset.page === 'epics');
        });

        this.content.innerHTML = '<div class="loading">Загрузка эпика...</div>';

        try {
            const html = await Pages.epicDetail(epicId);
            this.content.innerHTML = html;
        } catch (error) {
            console.error(error);
            this.content.innerHTML = `
                <div class="error">❌ Ошибка загрузки эпика: ${error.message}</div>
                <button class="btn" onclick="window.App.navigate('epics')">← Назад к эпикам</button>
            `;
        }
    }

    openModal(html) {
        this.modalBody.innerHTML = html;
        this.modal.style.display = 'flex';
    }

    closeModal() {
        this.modal.style.display = 'none';
        this.modalBody.innerHTML = '';
        this.currentModalContext = null;
    }

    async viewItem(type, id) {
        try {
            const item = await API.getByType(type, id);
            this.openModal(Components.taskDetails(item));
        } catch (error) {
            alert(`Ошибка: ${error.message}`);
        }
    }

    showCreateForm() {
        const html = Components.taskForm({}, {type: 'task', isEdit: false});
        this.currentModalContext = {type: 'task', id: null};
        this.openModal(html);
    }

    showSubtaskForm() {
        const html = Components.taskForm(
            {},
            {type: 'subtask', showEpicId: true, isEdit: false}
        );
        this.currentModalContext = {type: 'subtask', id: null};
        this.openModal(html);
    }

    showEpicForm() {
        const html = Components.taskForm({}, {type: 'epic', isEdit: false});
        this.currentModalContext = {type: 'epic', id: null};
        this.openModal(html);
    }

    createEpicInStatus(status) {
        const html = Components.taskForm(
            {status},
            {type: 'epic', isEdit: false}
        );
        this.currentModalContext = {type: 'epic', id: null};
        this.openModal(html);
    }

    showSubtaskFormForEpic(epicId) {
        const html = Components.taskForm(
            {epicId},
            {type: 'subtask', showEpicId: true, isEdit: false}
        );
        this.currentModalContext = {type: 'subtask', id: null};
        this.openModal(html);
    }

    async editItem(type, id) {
        try {
            const item = await API.getByType(type, id);
            const html = Components.taskForm(item, {
                type,
                showEpicId: type === 'subtask',
                isEdit: true,
            });
            this.currentModalContext = {type, id};
            this.openModal(html);
        } catch (error) {
            alert(`Ошибка: ${error.message}`);
        }
    }

    async handleFormSubmit(event) {
        event.preventDefault();

        const form = event.target;
        const formData = new FormData(form);
        const data = {};

        for (const [key, value] of formData.entries()) {
            if (key === '__type') continue;

            if (value === '') {
                data[key] = null;
            } else if (key === 'id' || key === 'epicId' || key === 'duration') {
                const num = Number(value);
                data[key] = Number.isNaN(num) ? null : num;
            } else {
                data[key] = value;
            }
        }

        const type = formData.get('__type') || 'task';
        const isEdit = !!data.id;

        try {
            if (isEdit) {
                await API.updateByType(type, data.id, data);
                this.showToast('✅ Обновлено');
            } else {
                await API.createByType(type, data);
                this.showToast('✅ Создано');
            }

            this.closeModal();
            this.refreshCurrentPage();
        } catch (error) {
            alert(`❌ Ошибка: ${error.message}`);
        }
    }

    async deleteItem(type, id) {
        if (!confirm(`Удалить ${type} #${id}?`)) return;

        try {
            const item = await API.getByType(type, id);
            await API.deleteByType(type, id, item);
            this.showToast('✅ Удалено');
            this.refreshCurrentPage();
        } catch (error) {
            alert(`❌ Ошибка: ${error.message}`);
        }
    }

    showEpicMenu(event, epicId) {
        event.stopPropagation();

        const action = prompt(
            `Эпик #${epicId}\n` +
            `1 — Редактировать\n` +
            `2 — Удалить\n` +
            `Введите номер:`
        );

        if (action === '1') {
            this.editItem('epic', epicId);
        } else if (action === '2') {
            this.deleteItem('epic', epicId);
        }
    }

    refreshCurrentPage() {
        if (this.currentPage === 'epic-detail' && this.currentEpicId) {
            this.renderEpicDetail(this.currentEpicId);
        } else {
            this.navigate(this.currentPage, false);
        }
    }

    async filterSubtasks() {
        const epicId = document.getElementById('epic-filter')?.value;
        if (!epicId) {
            this.navigate('subtasks');
            return;
        }

        try {
            const subtasks = await API.getSubtasksByEpic(Number(epicId));
            const grid = document.querySelector('.task-grid');
            if (grid) {
                grid.outerHTML = Components.taskList(subtasks, {
                    type: 'subtask',
                    showEpicId: true,
                    onView: true,
                    onEdit: true,
                    onDelete: true,
                });
            }
        } catch (error) {
            alert(`Ошибка: ${error.message}`);
        }
    }

    clearFilter() {
        const input = document.getElementById('epic-filter');
        if (input) input.value = '';
        this.navigate('subtasks');
    }

    showToast(message) {
        const toast = document.createElement('div');
        toast.textContent = message;
        toast.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: #48bb78;
            color: white;
            padding: 12px 20px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.3);
            z-index: 2000;
            font-weight: 500;
            animation: slideIn 0.3s;
        `;
        document.body.appendChild(toast);
        setTimeout(() => toast.remove(), 2000);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new App();
});