export default function StepIndicator({ totalSteps, currentStep }) {
  const steps = Array.from({ length: totalSteps }, (_, i) => i + 1);

  return (
    <div className="step-indicator" aria-label={`Step ${currentStep} of ${totalSteps}`}>
      {steps.map((step, idx) => {
        const isDone = step < currentStep;
        const isCurrent = step === currentStep;
        return (
          <div className="step-dot-wrap" key={step}>
            <div
              className={`step-dot ${isDone ? "is-done" : ""} ${isCurrent ? "is-current" : ""}`}
            >
              {isDone ? <i className="bi bi-check-lg" /> : step}
            </div>
            {idx < steps.length - 1 && <div className={`step-dot-line ${isDone ? "is-done" : ""}`} />}
          </div>
        );
      })}
    </div>
  );
}
