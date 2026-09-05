import { useEffect, useMemo, useState } from "react";
import { Form } from "react-bootstrap";
import { useAuth } from "../context/AuthContext";
import { useDataStore, useLookups } from "../context/DataStoreContext";
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
  const { getActivityLogPage, getClientById } = useDataStore();
  const { activityTypes, leadStatuses } = useLookups();
  const [activityFilter, setActivityFilter] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [page, setPage] = useState(0);
  const [activities, setActivities] = useState([]);
  const [allActivities, setAllActivities] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isFilteringLoading, setIsFilteringLoading] = useState(false);
  const [loadError, setLoadError] = useState(null);
  const filtersActive = Boolean(activityFilter || statusFilter);

  useEffect(() => {
    let cancelled = false;
    setLoadError(null);

    if (filtersActive) {
      if (allActivities !== null) {
        setIsLoading(false);
        return () => {
          cancelled = true;
        };
      }

      async function loadAllActivities() {
        try {
          setIsFilteringLoading(true);
          setIsLoading(true);
          const rows = [];
          const pageSize = 100;
          const maxPages = 20;
          // The backend has no filter or total-count endpoint, so cap the
          // client-side search at the same bounded window as the dashboard.
          for (let activityPage = 0; activityPage < maxPages; activityPage += 1) {
            const batch = await getActivityLogPage(activityPage, pageSize);
            rows.push(...batch);
            if (batch.length < pageSize) break;
          }
          if (!cancelled) setAllActivities(rows);
        } catch (error) {
          if (!cancelled) setLoadError(error.message);
        } finally {
          if (!cancelled) {
            setIsFilteringLoading(false);
            setIsLoading(false);
          }
        }
      }

      loadAllActivities();
    } else {
      setIsLoading(true);
      getActivityLogPage(page, 20)
        .then((rows) => {
          if (!cancelled) setActivities(rows);
        })
        .catch((error) => {
          if (!cancelled) setLoadError(error.message);
        })
        .finally(() => {
          if (!cancelled) setIsLoading(false);
        });
    }

    return () => {
      cancelled = true;
    };
  }, [allActivities, filtersActive, getActivityLogPage, page, currentSe.id]);

  const filtered = useMemo(() => {
    const source = filtersActive ? allActivities || [] : activities;
    return source
      .filter((log) => !activityFilter || log.activity_type_id === Number(activityFilter))
      .filter((log) => !statusFilter || log.status_id === Number(statusFilter))
      .sort((a, b) => new Date(b.activity_date) - new Date(a.activity_date));
  }, [activities, allActivities, activityFilter, filtersActive, statusFilter]);

  const displayedActivities = filtersActive ? filtered.slice(page * 20, (page + 1) * 20) : filtered;

  return (
    <div>
      <h4 className="mb-1">Activity history</h4>
      <p className="text-secondary mb-3">Everything you've logged, most recent first.</p>

      <div className="d-flex gap-2 mb-3">
        <Form.Select
          size="sm"
          value={activityFilter}
          onChange={(e) => {
            setActivityFilter(e.target.value);
            setPage(0);
          }}
        >
          <option value="">All activity types</option>
          {activityTypes.map((t) => (
            <option key={t.id} value={t.id}>
              {t.activity_name}
            </option>
          ))}
        </Form.Select>
        <Form.Select
          size="sm"
          value={statusFilter}
          onChange={(e) => {
            setStatusFilter(e.target.value);
            setPage(0);
          }}
        >
          <option value="">All statuses</option>
          {leadStatuses.map((s) => (
            <option key={s.id} value={s.id}>
              {s.status_name}
            </option>
          ))}
        </Form.Select>
      </div>

      {loadError && <div className="alert alert-danger">{loadError}</div>}

      {isLoading ? (
        <div className="list-card text-center py-4">
          {isFilteringLoading ? "Searching all activity history..." : "Loading activity history..."}
        </div>
      ) : displayedActivities.length === 0 ? (
        <div className="list-card">
          <EmptyState icon="bi-journal-text" title="Nothing here" copy="No activities match these filters yet." />
        </div>
      ) : (
        <div className="list-card">
          {displayedActivities.map((log) => {
            const client = getClientById(log.client_id);
            const activityType = activityTypes.find((type) => type.id === log.activity_type_id);
            return (
              <div className="list-row" key={log.id} style={{ cursor: "default" }}>
                <span className="list-row-icon">
                  <i className={`bi ${activityType?.icon || "bi-circle"}`} />
                </span>
                <span className="list-row-body">
                  <span className="list-row-title">{client?.full_name || log.client_name || "Unknown customer"}</span>
                  <span className="list-row-sub">
                    {activityType?.activity_name || log.activity_type_name} · {formatDateTime(log.activity_date)}
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

      <div className="d-flex justify-content-between align-items-center mt-3">
        <button
          type="button"
          className="btn btn-sm btn-outline-portal"
          disabled={page === 0 || isLoading}
          onClick={() => setPage((currentPage) => currentPage - 1)}
        >
          Previous
        </button>
        <span className="small text-secondary">Page {page + 1}</span>
        <button
          type="button"
          className="btn btn-sm btn-outline-portal"
          disabled={isLoading || (filtersActive ? (page + 1) * 20 >= filtered.length : activities.length < 20)}
          onClick={() => setPage((currentPage) => currentPage + 1)}
        >
          Next
        </button>
      </div>
    </div>
  );
}
