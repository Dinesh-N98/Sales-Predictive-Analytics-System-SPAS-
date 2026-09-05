import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { useDataStore, useLookups } from "../../context/DataStoreContext";
import StepIndicator from "./StepIndicator";
import StepActivityType from "./StepActivityType";
import StepCustomerType from "./StepCustomerType";
import StepNewCustomerForm, { isNewCustomerFormValid } from "./StepNewCustomerForm";
import StepExistingCustomerSearch, { isExistingCustomerStepValid } from "./StepExistingCustomerSearch";
import StepPolicySelect, { isPolicyStepValid } from "./StepPolicySelect";
import StepFollowUp, { isFollowUpStepValid } from "./StepFollowUp";
import StepConfirm from "./StepConfirm";

const AUTO_ADVANCE_STEPS = new Set(["activityType", "customerType"]);

const initialDraft = {
  activityTypeId: null,
  customerMode: null,
  client: null,
  selectedClientId: null,
  policyId: null,
  status_id: null,
  next_follow_up_date: null,
  premium_amount: "",
  rejection_reason_id: "",
  remarks: "",
  duration_minutes: "",
  skipCustomerSteps: false,
};

function buildInitialDraft(prefillClient) {
  if (!prefillClient) return initialDraft;
  return {
    ...initialDraft,
    customerMode: "existing",
    client: prefillClient,
    selectedClientId: prefillClient.id,
    policyId: prefillClient.last_policy_id || null,
    skipCustomerSteps: true,
  };
}

function getStepSequence(draft) {
  if (draft.skipCustomerSteps) {
    return ["activityType", "policy", "followup", "confirm"];
  }
  return ["activityType", "customerType", "customer", "policy", "followup", "confirm"];
}

function getStatusId(leadStatuses, name) {
  return leadStatuses.find((status) => status.status_name.toLowerCase() === name.toLowerCase())?.id ?? null;
}

function getStepValidity(stepKey, draft, leadStatuses) {
  switch (stepKey) {
    case "activityType":
      return Boolean(draft.activityTypeId);
    case "customerType":
      return Boolean(draft.customerMode);
    case "customer":
      return draft.customerMode === "new"
        ? isNewCustomerFormValid(draft.client)
        : isExistingCustomerStepValid(draft);
    case "policy":
      return isPolicyStepValid(draft);
    case "followup":
      return isFollowUpStepValid(draft, leadStatuses);
    case "confirm":
      return true;
    default:
      return false;
  }
}

export default function ActivityWizard() {
  const navigate = useNavigate();
  const location = useLocation();
  const { currentSe } = useAuth();
  const { addClient, updateClient, addActivityLog, updateActivityLog, addSale } = useDataStore();
  const { lookupsLoading, lookupsError, leadStatuses } = useLookups();

  const prefillClient = location.state?.prefillClient || null;

  const [draft, setDraft] = useState(() => buildInitialDraft(prefillClient));
  const [stepIndex, setStepIndex] = useState(0);
  const [isDone, setIsDone] = useState(false);
  const [savedSummary, setSavedSummary] = useState(null);
  const [saveError, setSaveError] = useState(null);
  const [isSaving, setIsSaving] = useState(false);
  const [isLinkCopied, setIsLinkCopied] = useState(false);

  const sequence = getStepSequence(draft);
  const stepKey = sequence[stepIndex];
  const isLastStep = stepIndex === sequence.length - 1;
  const isValid = getStepValidity(stepKey, draft, leadStatuses);
  const pendingStatusId = getStatusId(leadStatuses, "Pending");
  const soldStatusId = getStatusId(leadStatuses, "Sold");
  const rejectedStatusId = getStatusId(leadStatuses, "Rejected");

  function updateDraft(patch) {
    setDraft((prev) => ({ ...prev, ...patch }));
  }

  function goNext() {
    setStepIndex((i) => Math.min(i + 1, sequence.length - 1));
  }

  function goBack() {
    if (stepIndex === 0) {
      navigate("/");
      return;
    }
    setStepIndex((i) => Math.max(i - 1, 0));
  }

  function resetWizard() {
    setDraft(buildInitialDraft(null));
    setStepIndex(0);
    setIsDone(false);
    setSavedSummary(null);
    setSaveError(null);
    setIsSaving(false);
    setIsLinkCopied(false);
  }

  async function handleSave() {
    setSaveError(null);
    setIsSaving(true);
    let clientId;

    try {
      if (draft.customerMode === "new") {
        const created = await addClient(draft.client);
        clientId = created.id;
      } else {
        clientId = draft.selectedClientId;
        updateClient(clientId, {
          last_policy_id: draft.policyId,
          rejection_reason_id: draft.status_id === rejectedStatusId ? draft.rejection_reason_id : draft.client.rejection_reason_id,
        });
      }

      const activityDate = new Date().toISOString().slice(0, 10);
      const activityPayload = {
        clientId,
        activityTypeId: draft.activityTypeId,
        statusId: draft.status_id === soldStatusId ? pendingStatusId : draft.status_id,
        premiumAmount: null,
        activityDate,
        clientPolicyId: draft.policyId || null,
        nextFollowUpDate: draft.next_follow_up_date || null,
        remarks: draft.remarks || "",
        durationMinutes: draft.duration_minutes ? Number(draft.duration_minutes) : 0,
      };
      const savedActivity = await addActivityLog(activityPayload);

      if (draft.status_id === soldStatusId) {
        try {
          await updateActivityLog(savedActivity.id, {
            ...activityPayload,
            statusId: soldStatusId,
            premiumAmount: Number(draft.premium_amount),
          });
        } catch (error) {
          const conflictPrefix = error.status === 409 ? "Sold conflict" : "Sold update failed";
          throw new Error(`${conflictPrefix}: activity was logged, but it was not marked Sold. ${error.message}`);
        }
      }

      if (draft.status_id === soldStatusId) {
        addSale({
          client_id: clientId,
          policy_id: draft.policyId,
          se_id: currentSe.id,
          issue_date: new Date().toISOString().slice(0, 10),
          renewal_date: null,
          premium_amount: Number(draft.premium_amount),
        });
      }

      setSavedSummary({
        clientName: draft.client?.full_name,
        statusId: draft.status_id,
        feedbackToken: savedActivity.feedback_token,
      });
      setIsDone(true);
    } catch (error) {
      setSaveError(error.message || "Activity could not be saved.");
    } finally {
      setIsSaving(false);
    }
  }

  const feedbackUrl = savedSummary?.feedbackToken
    ? `${window.location.origin}/feedback/${encodeURIComponent(savedSummary.feedbackToken)}`
    : null;

  async function copyFeedbackLink() {
    if (!feedbackUrl) return;
    await navigator.clipboard.writeText(feedbackUrl);
    setIsLinkCopied(true);
  }

  function renderStep() {
    switch (stepKey) {
      case "activityType":
        return <StepActivityType draft={draft} updateDraft={updateDraft} goNext={goNext} />;
      case "customerType":
        return <StepCustomerType draft={draft} updateDraft={updateDraft} goNext={goNext} />;
      case "customer":
        return draft.customerMode === "new" ? (
          <StepNewCustomerForm draft={draft} updateDraft={updateDraft} />
        ) : (
          <StepExistingCustomerSearch draft={draft} updateDraft={updateDraft} />
        );
      case "policy":
        return <StepPolicySelect draft={draft} updateDraft={updateDraft} />;
      case "followup":
        return <StepFollowUp draft={draft} updateDraft={updateDraft} />;
      case "confirm":
        return <StepConfirm draft={draft} />;
      default:
        return null;
    }
  }

  if (isDone) {
    return (
      <div className="success-shell wizard-fade">
        <div className="success-check">
          <i className="bi bi-check-lg" />
        </div>
        <h4 className="mb-2">Activity logged</h4>
        <p className="text-secondary mb-4">
          {savedSummary?.clientName ? `Saved for ${savedSummary.clientName}.` : "Saved successfully."}
        </p>
        {feedbackUrl && (
          <div className="feedback-share-panel mx-auto mb-4">
            <div className="feedback-share-label">Client feedback link</div>
            <div className="feedback-link-row">
              <input className="form-control" value={feedbackUrl} readOnly aria-label="Client feedback link" />
              <button type="button" className="btn btn-gold feedback-copy-button" onClick={copyFeedbackLink}>
                <i className={`bi ${isLinkCopied ? "bi-check-lg" : "bi-copy"}`} aria-hidden="true" />
                <span>{isLinkCopied ? "Copied" : "Copy link"}</span>
              </button>
            </div>
            <div className="feedback-share-actions">
              <a
                className="btn btn-outline-portal"
                href={`https://wa.me/?text=${encodeURIComponent(`Please share your feedback: ${feedbackUrl}`)}`}
                target="_blank"
                rel="noreferrer"
              >
                <i className="bi bi-whatsapp" aria-hidden="true" /> WhatsApp
              </a>
              <a
                className="btn btn-outline-portal"
                href={`mailto:?subject=${encodeURIComponent("Share your feedback")}&body=${encodeURIComponent(`Please share your feedback: ${feedbackUrl}`)}`}
              >
                <i className="bi bi-envelope" aria-hidden="true" /> Email
              </a>
            </div>
            <small className="text-secondary d-block mt-2">The public feedback form still needs to be added before this link can receive responses.</small>
          </div>
        )}
        <div className="d-flex flex-column gap-2 mx-auto" style={{ maxWidth: 320 }}>
          <button type="button" className="btn btn-gold" onClick={resetWizard}>
            Log another activity
          </button>
          <button type="button" className="btn btn-outline-portal" onClick={() => navigate("/")}>
            Back to dashboard
          </button>
        </div>
      </div>
    );
  }

  if (lookupsLoading) {
    return (
      <div className="app-main--with-action-bar d-flex align-items-center justify-content-center" style={{ minHeight: "400px" }}>
        <div className="text-center">
          <div className="spinner-border text-warning mb-3" role="status">
            <span className="visually-hidden">Loading lookups...</span>
          </div>
          <p className="text-secondary">Loading lookup tables...</p>
        </div>
      </div>
    );
  }

  if (lookupsError) {
    return (
      <div className="app-main--with-action-bar d-flex align-items-center justify-content-center" style={{ minHeight: "400px" }}>
        <div className="text-center">
          <div className="alert alert-danger" role="alert" style={{ maxWidth: 400 }}>
            <h5 className="alert-heading">Error loading lookup data</h5>
            <p className="mb-0">{lookupsError}</p>
            <button type="button" className="btn btn-sm btn-outline-danger mt-3" onClick={() => window.location.reload()}>
              Retry
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="app-main--with-action-bar">
      <StepIndicator totalSteps={sequence.length} currentStep={stepIndex + 1} />

      {saveError && (
        <div className="alert alert-danger mx-3" role="alert">
          <strong>Could not save activity.</strong> {saveError}
        </div>
      )}

      {renderStep()}

      <div className="wizard-action-bar">
        <div className="wizard-action-bar-inner">
          <button type="button" className="btn btn-outline-portal" onClick={goBack} style={{ flex: "0 0 auto" }}>
            {stepIndex === 0 ? "Cancel" : "Back"}
          </button>

          {!AUTO_ADVANCE_STEPS.has(stepKey) && (
            <button
              type="button"
              className="btn btn-gold flex-grow-1"
              disabled={!isValid || isSaving}
              onClick={isLastStep ? handleSave : goNext}
            >
              {isSaving ? "Saving..." : isLastStep ? "Save activity" : "Continue"}
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
