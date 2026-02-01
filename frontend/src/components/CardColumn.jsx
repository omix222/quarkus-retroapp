import { voteCard, deleteCard } from '../api';

const COLUMN_CONFIG = {
  KEEP: { className: 'keep', label: '✅ Keep - 良かったこと' },
  PROBLEM: { className: 'problem', label: '❗ Problem - 問題・課題' },
  TRY: { className: 'try', label: '💡 Try - 試したいこと' },
};

export default function CardColumn({ type, cards, onCardsChanged }) {
  const config = COLUMN_CONFIG[type];

  async function handleVote(cardId) {
    try {
      await voteCard(cardId);
      onCardsChanged();
    } catch (error) {
      console.error('Error voting card:', error);
    }
  }

  async function handleDelete(cardId) {
    if (!confirm('このカードを削除しますか？')) return;
    try {
      await deleteCard(cardId);
      onCardsChanged();
    } catch (error) {
      console.error('Error deleting card:', error);
      alert('カードの削除に失敗しました');
    }
  }

  return (
    <div className={`card-column ${config.className}`}>
      <h3>{config.label}</h3>
      {cards.map((card) => (
        <div key={card.id} className="card-item">
          <div className="card-content">{card.content}</div>
          <div className="card-votes">
            <button className="vote-button" onClick={() => handleVote(card.id)}>
              👍 投票
            </button>
            <span>投票数: {card.votes}</span>
            <button
              className="vote-button btn-danger"
              onClick={() => handleDelete(card.id)}
            >
              削除
            </button>
          </div>
        </div>
      ))}
    </div>
  );
}
