import { useLookups } from "../../context/DataStoreContext";

export default function StatusBadge({ statusId }) {
  const { leadStatuses } = useLookups();
  const status = leadStatuses.find((s) => s.id === statusId);
  if (!status) return null;
  const cls = `status-${status.status_name.toLowerCase().replace(/\s+/g, "-")}`;
  return <span className={`badge-status ${cls}`}>{status.status_name}</span>;
}
