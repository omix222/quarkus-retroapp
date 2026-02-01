import { useState } from 'react';
import { createRetrospective } from '../api';

export default function RetrospectiveForm({ onCreated }) {
  const [title, setTitle] = useState('');
  const [date, setDate] = useState('');
  const [description, setDescription] = useState('');

  async function handleSubmit(e) {
    e.preventDefault();
    try {
      await createRetrospective({ title, date, description });
      setTitle('');
      setDate('');
      setDescription('');
      onCreated();
    } catch (error) {
      console.error('Error creating retrospective:', error);
      alert('エラーが発生しました');
    }
  }

  return (
    <div className="section">
      <h2 className="section-title">新しいふりかえりを作成</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="retro-title">タイトル</label>
          <input
            type="text"
            id="retro-title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="retro-date">日付</label>
          <input
            type="date"
            id="retro-date"
            value={date}
            onChange={(e) => setDate(e.target.value)}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="retro-description">説明</label>
          <textarea
            id="retro-description"
            rows="3"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
        </div>
        <button type="submit">ふりかえりを作成</button>
      </form>
    </div>
  );
}
