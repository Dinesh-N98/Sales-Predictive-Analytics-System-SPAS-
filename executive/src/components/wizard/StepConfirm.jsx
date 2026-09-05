import { findById } from "../../data/mockData";
import { useLookups } from "../../context/DataStoreContext";
import StatusBadge from "../common/StatusBadge";

function Row({ label, value }) {
  if (!value && value !== 0) return null;
  return (
    <div className="d-flex justify-content-between py-2 border-bottom">
      <span className="text-secondary small">{label}</span>
      <span className="fw-semibold small text-end ms-3">{value}</span>
    </div>
  );
}

export default function StepConfirm({ draft }) {
  const { activityTypes, clientTypes, leadStatuses, policies, policyCategories, rejectionReasons } = useLookups();
  const activityType = findById(activityTypes, draft.activityTypeId);
  const policy = findById(policies, draft.policyId);
  const category = policy ? findById(policyCategories, policy.policy_category_id) : null;
  const clientType = draft.client ? findById(clientTypes, draft.client.client_type_id) : null;
  const rejectionReason = draft.rejection_reason_id ? findById(rejectionReasons, draft.rejection_reason_id) : null;
  const status = findById(leadStatuses, draft.status_id);

  return (
    <div className="wizard-fade">
      <h5 className="step-heading">Review & save</h5>
      <p className="step-subheading">Double-check the details, then log it.</p>

      <div className="list-card mb-3 px-3">
        <Row label="Activity" value={activityType?.activity_name} />
        <Row label="Customer" value={draft.client?.full_name} />
        <Row label="Customer type" value={clientType?.type_name} />
        <Row label="Policy" value={policy?.policy_name} />
        <Row label="Category" value={category?.category_name} />
        <Row
          label="Status"
          value={status ? <StatusBadge statusId={draft.status_id} /> : null}
        />
        {draft.status_id === 3 && <Row label="Premium amount" value={`Rs. ${Number(draft.premium_amount).toLocaleString()}`} />}
        {draft.status_id === 4 && <Row label="Rejection reason" value={rejectionReason?.reason_name} />}
        {draft.next_follow_up_date && <Row label="Next follow-up" value={draft.next_follow_up_date} />}
        {draft.duration_minutes && <Row label="Duration" value={`${draft.duration_minutes} min`} />}
      </div>

      {draft.remarks && (
        <div className="mb-3">
          <div className="section-eyebrow">Remarks</div>
          <p className="small text-dark mb-0">{draft.remarks}</p>
        </div>
      )}
    </div>
  );
}
