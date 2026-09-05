export default function EmptyState({ icon = "bi-inbox", title, copy }) {
  return (
    <div className="empty-state">
      <div className="empty-icon">
        <i className={`bi ${icon}`} aria-hidden="true"></i>
      </div>
      <div className="empty-title">{title}</div>
      {copy && <div className="empty-copy">{copy}</div>}
    </div>
  );
}
