import { useState, useEffect } from 'react';
import { getActionItemsByRetrospective, patchActionItemStatus, deleteActionItem } from '../api';
import ActionItemForm from './ActionItemForm';

export default function ActionItemList({ retroId }) {
  const [actionItems, setActionItems] = useState([]);

  async function loadActionItems() {
    try {
      const data = await getActionItemsByRetrospective(retroId);
      setActionItems(data);
    } catch (error) {
      console.error('Error loading action items:', error);
    }
  }

  useEffect(() => {
    loadActionItems();
  }, [retroId]);

  async function handleStatusChange(itemId, status) {
    try {
      await patchActionItemStatus(itemId, status);
      loadActionItems();
    } catch (error) {
      console.error('Error updating action item status:', error);
    }
  }

  async function handleDelete(itemId) {
    if (!confirm('このアクションアイテムを削除しますか？')) return;
    try {
      await deleteActionItem(itemId);
      loadActionItems();
    } catch (error) {
      console.error('Error deleting action item:', error);
    }
  }

  return (
    <div className="action-items">
      <h3 className="section-title">アクションアイテム</h3>
      <ActionItemForm retroId={retroId} onItemAdded={loadActionItems} />
      <div>
        {actionItems.map((item) => (
          <div key={item.id} className={`action-item ${item.status}`}>
            <div>
              <strong>{item.description}</strong>
              {item.assignee && (
                <>
                  <br />
                  担当: {item.assignee}
                </>
              )}
            </div>
            <div>
              <select
                value={item.status}
                onChange={(e) => handleStatusChange(item.id, e.target.value)}
              >
                <option value="TODO">未着手</option>
                <option value="IN_PROGRESS">進行中</option>
                <option value="DONE">完了</option>
              </select>
              <button className="btn-danger" onClick={() => handleDelete(item.id)}>
                削除
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
