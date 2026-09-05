import { useMemo } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useDataStore } from "../context/DataStoreContext";
import { ACTIVITY_TYPES, findById } from "../data/mockData";
import StatusBadge from "../components/common/StatusBadge";
import EmptyState from "../components/common/EmptyState";

function formatDateShort(iso) {
  if (!iso) return "";
  return new Date(iso).toLocaleDateString("en-GB", { day: "numeric", month: "short" });
}

function relativeDue(dateStr) {
  const today = new Date().toISOString().slice(0, 10);
  if (dateStr < today) return "Overdue";
  if (dateStr === today) return "Due today";
  return `Due ${formatDateShort(dateStr)}`;
}

export default function DashboardPage() {
  const { currentSe } = useAuth();
  const { getActivitiesForSe, getOpenFollowUpsForSe, getClientById, sales } = useDataStore();
  const navigate = useNavigate();

  const myActivities = getActivitiesForSe(currentSe.id);
  const openFollowUps = getOpenFollowUpsForSe(currentSe.id);

  const todayCount = useMemo(() => {
    const todayStr = new Date().toISOString().slice(0, 10);
    return myActivities.filter((log) => log.activity_date.slice(0, 10) === todayStr).length;
  }, [myActivities]);

  const monthSalesCount = useMemo(() => {
    const monthKey = new Date().toISOString().slice(0, 7);
    return sales.filter((s) => s.se_id === currentSe.id && s.issue_date.slice(0, 7) === monthKey).length;
  }, [sales, currentSe.id]);

  const recentActivities = useMemo(
    () => [...myActivities].sort((a, b) => new Date(b.activity_date) - new Date(a.activity_date)).slice(0, 6),
    [myActivities]
  );

  function startFollowUp(client) {
    navigate("/log-activity", { state: { prefillClient: client } });
  }

  return (
    <div>
      <h4 className="mb-1">Hey, {currentSe.full_name.split(" ")[0]}</h4>
      <p className="text-secondary mb-3">Here's where things stand today.</p>

      <div className="stat-strip mb-3">
        <div className="stat-tile">
          <div className="stat-number">{todayCount}</div>
          <div className="stat-label">Today</div>
        </div>
        <div className="stat-tile">
          <div className="stat-number">{openFollowUps.length}</div>
          <div className="stat-label">Follow-ups</div>
        </div>
        <div className="stat-tile">
          <div className="stat-number">{monthSalesCount}</div>
          <div className="stat-label">Sold (mo.)</div>
        </div>
      </div>

      <button type="button" className="cta-log-activity mb-4" onClick={() => navigate("/log-activity")}>
        <span className="cta-icon">
          <i className="bi bi-plus-lg" />
        </span>
        <span>
          <span className="cta-title d-block">Log new activity</span>
          <span className="cta-sub">Field visit, call, WhatsApp, email, or meet-up</span>
        </span>
        <i className="bi bi-chevron-right ms-auto" />
      </button>

      <div className="section-eyebrow">Follow-ups due</div>
      {openFollowUps.length === 0 ? (
        <div className="list-card mb-4">
          <EmptyState icon="bi-check2-circle" title="Nothing due" copy="You're all caught up on follow-ups." />
        </div>
      ) : (
        <div className="list-card mb-4">
          {openFollowUps.map((log) => {
            const client = getClientById(log.client_id);
            if (!client) return null;
            return (
              <button type="button" key={log.id} className="list-row" onClick={() => startFollowUp(client)}>
                <span className="list-row-icon">
                  <i className="bi bi-person-fill" />
                </span>
                <span className="list-row-body">
                  <span className="list-row-title">{client.full_name}</span>
                  <span className="list-row-sub">{client.contact_number}</span>
                </span>
                <span className="list-row-meta text-tabular">{relativeDue(log.next_follow_up_date)}</span>
              </button>
            );
          })}
        </div>
      )}

      <div className="section-eyebrow">Recent activity</div>
      {recentActivities.length === 0 ? (
        <div className="list-card">
          <EmptyState
            icon="bi-clock-history"
            title="No activity yet"
            copy="Log your first visit, call, or message to see it here."
          />
        </div>
      ) : (
        <div className="list-card">
          {recentActivities.map((log) => {
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
                    {activityType?.activity_name} · {formatDateShort(log.activity_date)}
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
