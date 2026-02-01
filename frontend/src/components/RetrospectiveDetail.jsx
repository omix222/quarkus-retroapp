import { useState, useEffect } from 'react';
import { getRetrospective, getCardsByRetrospective } from '../api';
import CardForm from './CardForm';
import CardColumn from './CardColumn';
import ActionItemList from './ActionItemList';

export default function RetrospectiveDetail({ retroId, onBack }) {
  const [retro, setRetro] = useState(null);
  const [cards, setCards] = useState([]);

  async function loadRetro() {
    try {
      const data = await getRetrospective(retroId);
      setRetro(data);
    } catch (error) {
      console.error('Error loading retrospective:', error);
    }
  }

  async function loadCards() {
    try {
      const data = await getCardsByRetrospective(retroId);
      setCards(data);
    } catch (error) {
      console.error('Error loading cards:', error);
    }
  }

  useEffect(() => {
    loadRetro();
    loadCards();
  }, [retroId]);

  if (!retro) {
    return null;
  }

  const keepCards = cards.filter((c) => c.type === 'KEEP');
  const problemCards = cards.filter((c) => c.type === 'PROBLEM');
  const tryCards = cards.filter((c) => c.type === 'TRY');

  return (
    <>
      <button className="back-button btn-secondary" onClick={onBack}>
        ← 一覧に戻る
      </button>
      <h2>{retro.title}</h2>
      <p>{retro.description || ''}</p>

      <CardForm retroId={retroId} onCardAdded={loadCards} />

      <div className="cards-container">
        <CardColumn type="KEEP" cards={keepCards} onCardsChanged={loadCards} />
        <CardColumn type="PROBLEM" cards={problemCards} onCardsChanged={loadCards} />
        <CardColumn type="TRY" cards={tryCards} onCardsChanged={loadCards} />
      </div>

      <ActionItemList retroId={retroId} />
    </>
  );
}
