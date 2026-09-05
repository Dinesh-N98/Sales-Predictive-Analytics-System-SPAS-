import { useMemo, useState } from "react";
import { Form } from "react-bootstrap";
import { useAuth } from "../context/AuthContext";
import { useDataStore } from "../context/DataStoreContext";
import { ACTIVITY_TYPES, LEAD_STATUSES, findById } from "../data/mockData";
import StatusBadge from "../components/common/StatusBadge";
import EmptyState from "../components/common/EmptyState";

function formatDateTime(iso) {
  return new Date(iso).toLocaleString("en-GB", {
    day: "numeric",
    month: "short",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export default function HistoryPage() {
  const { currentSe } = useAuth();
  const { getActivitiesForSe, getClientById } = useDataStore();
  const [activityFilter, setActivityFilter] = useState("");
  const [statusFilter, setStatusFilter] = useState("");

  const allActivities = getActivitiesForSe(currentSe.id);

  const filtered = useMemo(() => {
    return [...allActivities]
      .filter((log) => !activityFilter || log.activity_type_id === Number(activityFilter))
      .filter((log) => !statusFilter || log.status_id === Number(statusFilter))
      .sort((a, b) => new Date(b.activity_date) - new Date(a.activity_date));
  }, [allActivities, activityFilter, statusFilter]);

  return (
    <div>
      <h4 className="mb-1">Activity history</h4>
      <p className="text-secondary mb-3">Everything you've logged, most recent first.</p>

      <div className="d-flex gap-2 mb-3">
        <Form.Select size="sm" value={activityFilter} onChange={(e) => setActivityFilter(e.target.value)}>
          <option value="">All activity types</option>
          {ACTIVITY_TYPES.map((t) => (
            <option key={t.id} value={t.id}>
              {t.activity_name}
            </option>
          ))}
        </Form.Select>
        <Form.Select size="sm" value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
          <option value="">All statuses</option>
          {LEAD_STATUSES.map((s) => (
            <option key={s.id} value={s.id}>
              {s.status_name}
            </option>
          ))}
        </Form.Select>
      </div>

      {filtered.length === 0 ? (
        <div className="list-card">
          <EmptyState icon="bi-journal-text" title="Nothing here" copy="No activities match these filters yet." />
        </div>
      ) : (
        <div className="list-card">
          {filtered.map((log) => {
            const client = getClientById(log.client_id);
            const activityType = findById(ACTIVITY_TYPES, log.activity_type_id);
            return (
              <div className="list-row" key={log.id} style={{ cursor: "default" }}>
                <span className="list-row-icon">
                  <i className={`bi ${activityType?.icon || "bi-circle"}`} />
                </span>
                <span className="list-row-body">
                  <span className="list-row-title">{client?.full_name || "Unknown customer"}</span>
                  <span className="list-row-sub">
                    {activityType?.activity_name} · {formatDateTime(log.activity_date)}
                    {log.remarks ? ` · ${log.remarks}` : ""}
                  </span>
                </span>
                <span className="list-row-meta">
                  <StatusBadge statusId={log.status_id} />
                </span>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
