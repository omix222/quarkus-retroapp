import { useState } from 'react';
import { createActionItem } from '../api';

const JIRA_TICKET_PATTERN = /^$|^[A-Z][A-Z0-9]*-[0-9]+$/;

export default function ActionItemForm({ retroId, onItemAdded }) {
  const [description, setDescription] = useState('');
  const [assignee, setAssignee] = useState('');
  const [jiraTicket, setJiraTicket] = useState('');
  const [jiraError, setJiraError] = useState('');

  function validateJiraTicket(value) {
    if (value && !JIRA_TICKET_PATTERN.test(value)) {
      setJiraError('形式: PROJECT-123（大文字英字-数字）');
      return false;
    }
    setJiraError('');
    return true;
  }

  function handleJiraChange(e) {
    const value = e.target.value.toUpperCase();
    setJiraTicket(value);
    validateJiraTicket(value);
  }

  async function handleSubmit(e) {
    e.preventDefault();
    if (!validateJiraTicket(jiraTicket)) {
      return;
    }
    try {
      await createActionItem(retroId, {
        description,
        assignee,
        jiraTicket: jiraTicket || null,
        status: 'TODO'
      });
      setDescription('');
      setAssignee('');
      setJiraTicket('');
      setJiraError('');
      onItemAdded();
    } catch (error) {
      console.error('Error adding action item:', error);
      if (error.message) {
        alert(error.message);
      } else {
        alert('エラーが発生しました');
      }
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <div className="form-group">
        <label htmlFor="action-description">内容</label>
        <input
          type="text"
          id="action-description"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          required
        />
      </div>
      <div className="form-group">
        <label htmlFor="action-assignee">担当者</label>
        <input
          type="text"
          id="action-assignee"
          value={assignee}
          onChange={(e) => setAssignee(e.target.value)}
        />
      </div>
      <div className="form-group">
        <label htmlFor="action-jira">Jiraチケット</label>
        <input
          type="text"
          id="action-jira"
          value={jiraTicket}
          onChange={handleJiraChange}
          placeholder="例: PROJECT-123"
        />
        {jiraError && <span className="error-message">{jiraError}</span>}
      </div>
      <button type="submit">アクションアイテムを追加</button>
    </form>
  );
}
