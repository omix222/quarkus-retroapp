import { useState } from 'react';
import RetrospectiveList from './components/RetrospectiveList';
import RetrospectiveDetail from './components/RetrospectiveDetail';

export default function App() {
  const [currentView, setCurrentView] = useState('list');
  const [selectedRetroId, setSelectedRetroId] = useState(null);

  function handleSelectRetro(id) {
    setSelectedRetroId(id);
    setCurrentView('detail');
  }

  function handleBackToList() {
    setSelectedRetroId(null);
    setCurrentView('list');
  }

  return (
    <div className="container">
      <h1>🔄 レトロスペクティブアプリ</h1>
      {currentView === 'list' ? (
        <RetrospectiveList onSelectRetro={handleSelectRetro} />
      ) : (
        <RetrospectiveDetail retroId={selectedRetroId} onBack={handleBackToList} />
      )}
    </div>
  );
}
