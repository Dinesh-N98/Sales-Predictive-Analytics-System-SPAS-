import { useEffect, useState } from "react";
import { Form, Row, Col } from "react-bootstrap";
import { useLookups } from "../../context/DataStoreContext";

function getStatusId(leadStatuses, name) {
  return leadStatuses.find((status) => status.status_name.toLowerCase() === name.toLowerCase())?.id ?? null;
}

function todayIso() {
  return new Date().toISOString().slice(0, 10);
}

export function isFollowUpStepValid(draft, leadStatuses) {
  const pendingStatusId = getStatusId(leadStatuses, "Pending");
  const soldStatusId = getStatusId(leadStatuses, "Sold");
  const rejectedStatusId = getStatusId(leadStatuses, "Rejected");
  if (draft.customerMode === "new") {
    // The backend has no Inquired status; new activities start as Pending.
    return true;
  }
  if (draft.status_id === pendingStatusId) {
    return Boolean(draft.next_follow_up_date);
  }
  if (draft.status_id === soldStatusId) {
    return Boolean(draft.premium_amount) && Number(draft.premium_amount) > 0;
  }
  if (draft.status_id === rejectedStatusId) {
    return Boolean(draft.rejection_reason_id);
  }
  return false; // existing customer must pick a status
}

export default function StepFollowUp({ draft, updateDraft }) {
  const { rejectionReasons, leadStatuses } = useLookups();
  const pendingStatusId = getStatusId(leadStatuses, "Pending");
  const soldStatusId = getStatusId(leadStatuses, "Sold");
  const rejectedStatusId = getStatusId(leadStatuses, "Rejected");
  const [touched, setTouched] = useState({ status: false, next_follow_up_date: false, premium_amount: false, rejection_reason_id: false });

  useEffect(() => {
    if (draft.customerMode === "new" && !draft.status_id) {
      updateDraft({ status_id: pendingStatusId });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [draft.customerMode, pendingStatusId]);

  function setField(field, value) {
    updateDraft({ [field]: value });
  }

  function chooseStatus(statusId) {
    setTouched((prev) => ({ ...prev, status: true }));
    updateDraft({
      status_id: statusId,
      next_follow_up_date: statusId === pendingStatusId ? draft.next_follow_up_date : null,
      premium_amount: statusId === soldStatusId ? draft.premium_amount : "",
      rejection_reason_id: statusId === rejectedStatusId ? draft.rejection_reason_id : "",
    });
  }

  const needsStatus = draft.customerMode === "existing" && !draft.status_id;
  const pendingError = draft.status_id === pendingStatusId && touched.next_follow_up_date && !draft.next_follow_up_date;
  const soldError = draft.status_id === soldStatusId && touched.premium_amount && (!draft.premium_amount || Number(draft.premium_amount) <= 0);
  const rejectedError = draft.status_id === rejectedStatusId && touched.rejection_reason_id && !draft.rejection_reason_id;

  return (
    <div className="wizard-fade">
      <h5 className="step-heading">Wrap it up</h5>
      <p className="step-subheading">
        {draft.customerMode === "new"
          ? "This first contact will be logged as Pending."
          : "How did this follow-up go?"}
      </p>

      {draft.customerMode === "existing" && (
        <div className="d-flex flex-column gap-2 mb-3">
          <button
            type="button"
            className={`option-card ${draft.status_id === pendingStatusId ? "is-selected" : ""}`}
            onClick={() => chooseStatus(pendingStatusId)}
          >
            <span className="option-icon"><i className="bi bi-hourglass-split" /></span>
            <span>
              <span className="option-title d-block">Pending</span>
              <span className="option-sub">Still deciding — needs a follow-up</span>
            </span>
          </button>
          <button
            type="button"
            className={`option-card ${draft.status_id === soldStatusId ? "is-selected" : ""}`}
            onClick={() => chooseStatus(soldStatusId)}
          >
            <span className="option-icon"><i className="bi bi-check-circle-fill" /></span>
            <span>
              <span className="option-title d-block">Sold</span>
              <span className="option-sub">They bought the policy</span>
            </span>
          </button>
          <button
            type="button"
            className={`option-card ${draft.status_id === rejectedStatusId ? "is-selected" : ""}`}
            onClick={() => chooseStatus(rejectedStatusId)}
          >
            <span className="option-icon"><i className="bi bi-x-circle-fill" /></span>
            <span>
              <span className="option-title d-block">Rejected</span>
              <span className="option-sub">Not moving forward this time</span>
            </span>
          </button>
        </div>
      )}

      {needsStatus && (
        <div className="text-danger small mb-3">Pick a status to continue.</div>
      )}

      <Form>
        {draft.status_id === rejectedStatusId && (
          <Form.Group className="mb-3" controlId="rejectionReason">
            <Form.Label>Reason *</Form.Label>
            <Form.Select
              value={draft.rejection_reason_id || ""}
              onChange={(e) => {
                setTouched((prev) => ({ ...prev, rejection_reason_id: true }));
                setField("rejection_reason_id", Number(e.target.value) || "");
              }}
              isInvalid={rejectedError}
            >
              <option value="">Select a reason</option>
              {rejectionReasons.map((r) => (
                <option key={r.id} value={r.id}>
                  {r.reason_name}
                </option>
              ))}
            </Form.Select>
            <Form.Control.Feedback type="invalid">
              Choose a rejection reason.
            </Form.Control.Feedback>
          </Form.Group>
        )}

        {draft.status_id === soldStatusId && (
          <Form.Group className="mb-3" controlId="premiumAmount">
            <Form.Label>Premium amount (Rs.) *</Form.Label>
            <Form.Control
              type="number"
              min="0"
              value={draft.premium_amount || ""}
              onChange={(e) => {
                setTouched((prev) => ({ ...prev, premium_amount: true }));
                setField("premium_amount", e.target.value);
              }}
              isInvalid={soldError}
            />
            <Form.Control.Feedback type="invalid">
              Enter a premium amount greater than zero.
            </Form.Control.Feedback>
          </Form.Group>
        )}

        {(draft.customerMode === "new" || draft.status_id === pendingStatusId) && (
          <Form.Group className="mb-3" controlId="nextFollowUp">
            <Form.Label>
              Next follow-up date{draft.status_id === pendingStatusId ? " *" : " (optional)"}
            </Form.Label>
            <Form.Control
              type="date"
              min={todayIso()}
              value={draft.next_follow_up_date || ""}
              onChange={(e) => {
                setTouched((prev) => ({ ...prev, next_follow_up_date: true }));
                setField("next_follow_up_date", e.target.value);
              }}
              isInvalid={pendingError}
            />
            {pendingError && (
              <Form.Control.Feedback type="invalid">
                Select a follow-up date.
              </Form.Control.Feedback>
            )}
          </Form.Group>
        )}

        <Row>
          <Col xs={12} sm={6}>
            <Form.Group className="mb-3" controlId="duration">
              <Form.Label>Duration (minutes)</Form.Label>
              <Form.Control
                type="number"
                min="0"
                value={draft.duration_minutes || ""}
                onChange={(e) => setField("duration_minutes", e.target.value)}
              />
            </Form.Group>
          </Col>
        </Row>

        <Form.Group className="mb-3" controlId="remarks">
          <Form.Label>Remarks</Form.Label>
          <Form.Control
            as="textarea"
            rows={3}
            value={draft.remarks || ""}
            onChange={(e) => setField("remarks", e.target.value)}
          />
        </Form.Group>
      </Form>
    </div>
  );
}
