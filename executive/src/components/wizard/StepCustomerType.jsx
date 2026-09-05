export default function StepCustomerType({ draft, updateDraft, goNext }) {
  function choose(mode) {
    updateDraft({ customerMode: mode });
    goNext();
  }

  return (
    <div className="wizard-fade">
      <h5 className="step-heading">Who's this activity with?</h5>
      <p className="step-subheading">New customer, or someone already in the system.</p>

      <div className="d-flex flex-column gap-2">
        <button
          type="button"
          className={`option-card ${draft.customerMode === "new" ? "is-selected" : ""}`}
          onClick={() => choose("new")}
        >
          <span className="option-icon">
            <i className="bi bi-person-plus-fill" />
          </span>
          <span>
            <span className="option-title d-block">New customer</span>
            <span className="option-sub">First time we're logging their details</span>
          </span>
          <span className="option-check">
            <i className="bi bi-chevron-right" />
          </span>
        </button>

        <button
          type="button"
          className={`option-card ${draft.customerMode === "existing" ? "is-selected" : ""}`}
          onClick={() => choose("existing")}
        >
          <span className="option-icon">
            <i className="bi bi-person-check-fill" />
          </span>
          <span>
            <span className="option-title d-block">Existing customer</span>
            <span className="option-sub">Search and load their record</span>
          </span>
          <span className="option-check">
            <i className="bi bi-chevron-right" />
          </span>
        </button>
      </div>
    </div>
  );
}
