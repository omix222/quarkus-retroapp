const API_BASE = '/api';

async function request(url, options = {}) {
  const response = await fetch(url, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}: ${response.statusText}`);
  }
  if (response.status === 204) {
    return null;
  }
  return response.json();
}

// Retrospectives

export function getRetrospectives() {
  return request(`${API_BASE}/retrospectives`);
}

export function getRetrospective(id) {
  return request(`${API_BASE}/retrospectives/${id}`);
}

export function getRecentRetrospectives(limit = 10) {
  return request(`${API_BASE}/retrospectives/recent?limit=${limit}`);
}

export function createRetrospective(data) {
  return request(`${API_BASE}/retrospectives`, {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

export function updateRetrospective(id, data) {
  return request(`${API_BASE}/retrospectives/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  });
}

export function deleteRetrospective(id) {
  return request(`${API_BASE}/retrospectives/${id}`, {
    method: 'DELETE',
  });
}

// Cards

export function getCardsByRetrospective(retroId) {
  return request(`${API_BASE}/cards/retrospectives/${retroId}`);
}

export function getCardsByType(retroId, type) {
  return request(`${API_BASE}/cards/retrospectives/${retroId}/type/${type}`);
}

export function getTopVotedCards(retroId, limit = 5) {
  return request(`${API_BASE}/cards/retrospectives/${retroId}/top-voted?limit=${limit}`);
}

export function createCard(retroId, data) {
  return request(`${API_BASE}/cards/retrospectives/${retroId}`, {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

export function updateCard(id, data) {
  return request(`${API_BASE}/cards/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  });
}

export function voteCard(id) {
  return request(`${API_BASE}/cards/${id}/vote`, {
    method: 'POST',
  });
}

export function unvoteCard(id) {
  return request(`${API_BASE}/cards/${id}/unvote`, {
    method: 'POST',
  });
}

export function deleteCard(id) {
  return request(`${API_BASE}/cards/${id}`, {
    method: 'DELETE',
  });
}

// Action Items

export function getActionItemsByRetrospective(retroId) {
  return request(`${API_BASE}/action-items/retrospectives/${retroId}`);
}

export function getActionItemsByStatus(retroId, status) {
  return request(`${API_BASE}/action-items/retrospectives/${retroId}/status/${status}`);
}

export function getActionItemsByAssignee(assignee) {
  return request(`${API_BASE}/action-items/assignee/${assignee}`);
}

export function createActionItem(retroId, data) {
  return request(`${API_BASE}/action-items/retrospectives/${retroId}`, {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

export function updateActionItem(id, data) {
  return request(`${API_BASE}/action-items/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  });
}

export function patchActionItemStatus(id, status) {
  return request(`${API_BASE}/action-items/${id}/status?status=${status}`, {
    method: 'PATCH',
  });
}

export function deleteActionItem(id) {
  return request(`${API_BASE}/action-items/${id}`, {
    method: 'DELETE',
  });
}
