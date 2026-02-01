import { useState } from 'react';
import { createActionItem } from '../api';

export default function ActionItemForm({ retroId, onItemAdded }) {
  const [description, setDescription] = useState('');
  const [assignee, setAssignee] = useState('');

  async function handleSubmit(e) {
    e.preventDefault();
    try {
      await createActionItem(retroId, { description, assignee, status: 'TODO' });
      setDescription('');
      setAssignee('');
      onItemAdded();
    } catch (error) {
      console.error('Error adding action item:', error);
      alert('エラーが発生しました');
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
      <button type="submit">アクションアイテムを追加</button>
    </form>
  );
}
