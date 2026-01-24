const API_BASE = '/api';
let currentRetrospectiveId = null;

// 初期化
document.addEventListener('DOMContentLoaded', () => {
    loadRetrospectives();
    setupEventListeners();
});

// イベントリスナーの設定
function setupEventListeners() {
    document.getElementById('create-retrospective-form').addEventListener('submit', createRetrospective);
    document.getElementById('add-card-form').addEventListener('submit', addCard);
    document.getElementById('add-action-form').addEventListener('submit', addActionItem);
}

// レトロスペクティブ一覧を読み込み
async function loadRetrospectives() {
    try {
        const response = await fetch(`${API_BASE}/retrospectives`);
        const retrospectives = await response.json();
        displayRetrospectives(retrospectives);
    } catch (error) {
        console.error('Error loading retrospectives:', error);
    }
}

// レトロスペクティブ一覧を表示
function displayRetrospectives(retrospectives) {
    const list = document.getElementById('retrospective-list');
    list.innerHTML = '';

    retrospectives.forEach(retro => {
        const card = document.createElement('div');
        card.className = 'retrospective-card';
        card.onclick = () => showDetailView(retro.id);

        card.innerHTML = `
            <h3>${retro.title}</h3>
            <p>📅 ${retro.date}</p>
            <p>${retro.description || ''}</p>
        `;

        list.appendChild(card);
    });
}

// レトロスペクティブを作成
async function createRetrospective(e) {
    e.preventDefault();

    const data = {
        title: document.getElementById('retro-title').value,
        date: document.getElementById('retro-date').value,
        description: document.getElementById('retro-description').value
    };

    try {
        const response = await fetch(`${API_BASE}/retrospectives`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            e.target.reset();
            loadRetrospectives();
            alert('ふりかえりを作成しました！');
        }
    } catch (error) {
        console.error('Error creating retrospective:', error);
        alert('エラーが発生しました');
    }
}

// 詳細画面を表示
async function showDetailView(id) {
    currentRetrospectiveId = id;

    try {
        const response = await fetch(`${API_BASE}/retrospectives/${id}`);
        const retro = await response.json();

        document.getElementById('detail-title').textContent = retro.title;
        document.getElementById('detail-description').textContent = retro.description || '';

        document.getElementById('list-view').classList.add('hidden');
        document.getElementById('detail-view').classList.remove('hidden');

        loadCards(id);
        loadActionItems(id);
    } catch (error) {
        console.error('Error loading retrospective:', error);
    }
}

// 一覧画面に戻る
function showListView() {
    document.getElementById('detail-view').classList.add('hidden');
    document.getElementById('list-view').classList.remove('hidden');
    currentRetrospectiveId = null;
    loadRetrospectives();
}

// カードを読み込み
async function loadCards(retroId) {
    try {
        const response = await fetch(`${API_BASE}/cards/retrospectives/${retroId}`);
        const cards = await response.json();

        // タイプ別にカードを分類
        const keepCards = cards.filter(c => c.type === 'KEEP');
        const problemCards = cards.filter(c => c.type === 'PROBLEM');
        const tryCards = cards.filter(c => c.type === 'TRY');

        displayCards('keep-cards', keepCards);
        displayCards('problem-cards', problemCards);
        displayCards('try-cards', tryCards);
    } catch (error) {
        console.error('Error loading cards:', error);
    }
}

// カードを表示
function displayCards(containerId, cards) {
    const container = document.getElementById(containerId);
    container.innerHTML = '';

    cards.forEach(card => {
        const cardElement = document.createElement('div');
        cardElement.className = 'card-item';

        cardElement.innerHTML = `
            <div class="card-content">${card.content}</div>
            <div class="card-votes">
                <button class="vote-button" onclick="voteCard(${card.id})">👍 投票</button>
                <span>投票数: ${card.votes}</span>
                <button class="vote-button btn-danger" onclick="deleteCard(${card.id})">削除</button>
            </div>
        `;

        container.appendChild(cardElement);
    });
}

// カードを追加
async function addCard(e) {
    e.preventDefault();

    const data = {
        type: document.getElementById('card-type').value,
        content: document.getElementById('card-content').value
    };

    try {
        const response = await fetch(`${API_BASE}/cards/retrospectives/${currentRetrospectiveId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            e.target.reset();
            loadCards(currentRetrospectiveId);
        }
    } catch (error) {
        console.error('Error adding card:', error);
        alert('エラーが発生しました');
    }
}

// カードに投票
async function voteCard(cardId) {
    try {
        const response = await fetch(`${API_BASE}/cards/${cardId}/vote`, {
            method: 'POST'
        });

        if (response.ok) {
            loadCards(currentRetrospectiveId);
        }
    } catch (error) {
        console.error('Error voting card:', error);
    }
}

// カードを削除
async function deleteCard(cardId) {
    console.log('deleteCard called with cardId:', cardId);
    if (!confirm('このカードを削除しますか？')) return;

    try {
        const url = `${API_BASE}/cards/${cardId}`;
        console.log('DELETE request to:', url);
        const response = await fetch(url, {
            method: 'DELETE'
        });

        console.log('DELETE response status:', response.status);
        if (response.ok) {
            loadCards(currentRetrospectiveId);
        } else {
            alert('カードの削除に失敗しました（ステータス: ' + response.status + '）');
        }
    } catch (error) {
        console.error('Error deleting card:', error);
        alert('エラーが発生しました: ' + error.message);
    }
}

// アクションアイテムを読み込み
async function loadActionItems(retroId) {
    try {
        const response = await fetch(`${API_BASE}/action-items/retrospectives/${retroId}`);
        const actionItems = await response.json();
        displayActionItems(actionItems);
    } catch (error) {
        console.error('Error loading action items:', error);
    }
}

// アクションアイテムを表示
function displayActionItems(actionItems) {
    const container = document.getElementById('action-items-list');
    container.innerHTML = '';

    actionItems.forEach(item => {
        const itemElement = document.createElement('div');
        itemElement.className = `action-item ${item.status}`;

        itemElement.innerHTML = `
            <div>
                <strong>${item.description}</strong>
                ${item.assignee ? `<br>担当: ${item.assignee}` : ''}
            </div>
            <div>
                <select onchange="updateActionItemStatus(${item.id}, this.value)">
                    <option value="TODO" ${item.status === 'TODO' ? 'selected' : ''}>未着手</option>
                    <option value="IN_PROGRESS" ${item.status === 'IN_PROGRESS' ? 'selected' : ''}>進行中</option>
                    <option value="DONE" ${item.status === 'DONE' ? 'selected' : ''}>完了</option>
                </select>
                <button class="btn-danger" onclick="deleteActionItem(${item.id})">削除</button>
            </div>
        `;

        container.appendChild(itemElement);
    });
}

// アクションアイテムを追加
async function addActionItem(e) {
    e.preventDefault();

    const data = {
        description: document.getElementById('action-description').value,
        assignee: document.getElementById('action-assignee').value,
        status: 'TODO'
    };

    try {
        const response = await fetch(`${API_BASE}/action-items/retrospectives/${currentRetrospectiveId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            e.target.reset();
            loadActionItems(currentRetrospectiveId);
        }
    } catch (error) {
        console.error('Error adding action item:', error);
        alert('エラーが発生しました');
    }
}

// アクションアイテムのステータスを更新
async function updateActionItemStatus(itemId, status) {
    try {
        const response = await fetch(`${API_BASE}/action-items/${itemId}/status?status=${status}`, {
            method: 'PATCH'
        });

        if (response.ok) {
            loadActionItems(currentRetrospectiveId);
        }
    } catch (error) {
        console.error('Error updating action item status:', error);
    }
}

// アクションアイテムを削除
async function deleteActionItem(itemId) {
    if (!confirm('このアクションアイテムを削除しますか？')) return;

    try {
        const response = await fetch(`${API_BASE}/action-items/${itemId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            loadActionItems(currentRetrospectiveId);
        }
    } catch (error) {
        console.error('Error deleting action item:', error);
    }
}
