import { useState } from 'react';
import { createCard } from '../api';

export default function CardForm({ retroId, onCardAdded }) {
  const [type, setType] = useState('KEEP');
  const [content, setContent] = useState('');

  async function handleSubmit(e) {
    e.preventDefault();
    try {
      await createCard(retroId, { type, content });
      setContent('');
      onCardAdded();
    } catch (error) {
      console.error('Error adding card:', error);
      alert('エラーが発生しました');
    }
  }

  return (
    <div className="section">
      <h3 className="section-title">カードを追加</h3>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="card-type">タイプ</label>
          <select
            id="card-type"
            value={type}
            onChange={(e) => setType(e.target.value)}
            required
          >
            <option value="KEEP">Keep - 良かったこと</option>
            <option value="PROBLEM">Problem - 問題・課題</option>
            <option value="TRY">Try - 試したいこと</option>
          </select>
        </div>
        <div className="form-group">
          <label htmlFor="card-content">内容</label>
          <textarea
            id="card-content"
            rows="2"
            value={content}
            onChange={(e) => setContent(e.target.value)}
            required
          />
        </div>
        <button type="submit">カードを追加</button>
      </form>
    </div>
  );
}
