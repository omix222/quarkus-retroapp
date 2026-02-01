import { useState, useEffect } from 'react';
import { getRetrospectives } from '../api';
import RetrospectiveForm from './RetrospectiveForm';

export default function RetrospectiveList({ onSelectRetro }) {
  const [retrospectives, setRetrospectives] = useState([]);

  async function loadRetrospectives() {
    try {
      const data = await getRetrospectives();
      setRetrospectives(data);
    } catch (error) {
      console.error('Error loading retrospectives:', error);
    }
  }

  useEffect(() => {
    loadRetrospectives();
  }, []);

  return (
    <>
      <RetrospectiveForm onCreated={loadRetrospectives} />
      <div className="section">
        <h2 className="section-title">ふりかえり一覧</h2>
        <div className="retrospective-list">
          {retrospectives.map((retro) => (
            <div
              key={retro.id}
              className="retrospective-card"
              onClick={() => onSelectRetro(retro.id)}
            >
              <h3>{retro.title}</h3>
              <p>📅 {retro.date}</p>
              <p>{retro.description || ''}</p>
            </div>
          ))}
        </div>
      </div>
    </>
  );
}
