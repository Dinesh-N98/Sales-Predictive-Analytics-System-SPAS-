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

function getStepValidity(stepKey, draft) {
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
      return isFollowUpStepValid(draft);
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
  const { addClient, updateClient, addActivityLog, addSale } = useDataStore();
  const { lookupsLoading, lookupsError } = useLookups();

  const prefillClient = location.state?.prefillClient || null;

  const [draft, setDraft] = useState(() => buildInitialDraft(prefillClient));
  const [stepIndex, setStepIndex] = useState(0);
  const [isDone, setIsDone] = useState(false);
  const [savedSummary, setSavedSummary] = useState(null);

  const sequence = getStepSequence(draft);
  const stepKey = sequence[stepIndex];
  const isLastStep = stepIndex === sequence.length - 1;
  const isValid = getStepValidity(stepKey, draft);

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
  }

  function handleSave() {
    let clientId;

    if (draft.customerMode === "new") {
      const created = addClient({ ...draft.client, last_policy_id: draft.policyId });
      clientId = created.id;
    } else {
      clientId = draft.selectedClientId;
      updateClient(clientId, {
        last_policy_id: draft.policyId,
        rejection_reason_id: draft.status_id === 4 ? draft.rejection_reason_id : draft.client.rejection_reason_id,
      });
    }

    addActivityLog({
      se_id: currentSe.id,
      client_id: clientId,
      activity_type_id: draft.activityTypeId,
      status_id: draft.status_id,
      activity_date: new Date().toISOString(),
      policy_id: draft.policyId,
      next_follow_up_date: draft.next_follow_up_date || null,
      remarks: draft.remarks || "",
      duration_minutes: draft.duration_minutes ? Number(draft.duration_minutes) : null,
    });

    if (draft.status_id === 3) {
      addSale({
        client_id: clientId,
        policy_id: draft.policyId,
        se_id: currentSe.id,
        issue_date: new Date().toISOString().slice(0, 10),
        renewal_date: null,
        premium_amount: Number(draft.premium_amount),
      });
    }

    setSavedSummary({ clientName: draft.client?.full_name, statusId: draft.status_id });
    setIsDone(true);
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
              disabled={!isValid}
              onClick={isLastStep ? handleSave : goNext}
            >
              {isLastStep ? "Save activity" : "Continue"}
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
