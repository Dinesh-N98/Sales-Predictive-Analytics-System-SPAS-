import { useLookups } from "../../context/DataStoreContext";

export default function StepActivityType({ draft, updateDraft, goNext }) {
  const { activityTypes } = useLookups();

  function choose(activityTypeId) {
    updateDraft({ activityTypeId });
    goNext();
  }

  return (
    <div className="wizard-fade">
      <h5 className="step-heading">What are you logging?</h5>
      <p className="step-subheading">Pick the kind of activity you just did.</p>

      <div className="activity-type-grid">
        {activityTypes.map((type) => {
          const selected = draft.activityTypeId === type.id;
          return (
            <button
              type="button"
              key={type.id}
              className={`activity-type-card ${selected ? "is-selected" : ""}`}
              onClick={() => choose(type.id)}
            >
              <span className="option-icon">
                <i className={`bi ${type.icon}`} />
              </span>
              <span className="option-title">{type.activity_name}</span>
            </button>
          );
        })}
      </div>
    </div>
  );
}
