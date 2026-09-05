import { LEAD_STATUSES } from "../../data/mockData";

const STATUS_CLASS_BY_ID = {
  1: "status-inquired", // Inquired
  2: "status-pending", // Pending
  3: "status-sold", // Sold
  4: "status-rejected", // Rejected
};

export default function StatusBadge({ statusId }) {
  const status = LEAD_STATUSES.find((s) => s.id === statusId);
  if (!status) return null;
  const cls = STATUS_CLASS_BY_ID[statusId] || "status-pending";
  return <span className={`badge-status ${cls}`}>{status.status_name}</span>;
}
