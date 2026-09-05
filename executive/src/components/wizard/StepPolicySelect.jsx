import { useEffect, useState } from "react";
import { findById } from "../../utils/findById";
import { useLookups } from "../../context/DataStoreContext";

export function isPolicyStepValid(draft) {
  return Boolean(draft.policyId);
}

export default function StepPolicySelect({ draft, updateDraft }) {
  const { policies, policyCategories } = useLookups();
  const selectedPolicy = findById(policies, draft.policyId);
  const initiallyOpen = selectedPolicy ? selectedPolicy.policy_category_id : (policyCategories[0]?.id || null);
  const [openCategoryId, setOpenCategoryId] = useState(initiallyOpen);
  const [touched, setTouched] = useState(false);

  useEffect(() => {
    if (selectedPolicy) setOpenCategoryId(selectedPolicy.policy_category_id);
  }, [selectedPolicy]);

  function choosePolicy(policyId) {
    setTouched(true);
    updateDraft({ policyId });
  }

  function handleCategoryToggle(categoryId) {
    setTouched(true);
    setOpenCategoryId((current) => (current === categoryId ? null : categoryId));
  }

  return (
    <div className="wizard-fade">
      <h5 className="step-heading">Which policy?</h5>
      <p className="step-subheading">
        {draft.client?.full_name
          ? `What are you offering ${draft.client.full_name.split(" ")[0]}?`
          : "Pick the policy for this conversation."}
      </p>

      {policyCategories.map((category) => {
        const policiesInCategory = policies.filter((p) => p.policy_category_id === category.id);
        const isOpen = openCategoryId === category.id;
        const hasSelection = policiesInCategory.some((p) => p.id === draft.policyId);

        return (
          <div className="policy-category" key={category.id}>
            <button
              type="button"
              className="policy-category-header"
              onClick={() => handleCategoryToggle(category.id)}
              aria-expanded={isOpen}
            >
              <span>
                {category.category_name}
                {hasSelection && !isOpen && (
                  <i className="bi bi-check-circle-fill text-success ms-2" />
                )}
              </span>
              <i className={`bi ${isOpen ? "bi-chevron-up" : "bi-chevron-down"}`} />
            </button>

            {isOpen && (
              <div className="policy-category-body">
                {policiesInCategory.map((policy) => {
                  const selected = draft.policyId === policy.id;
                  return (
                    <button
                      type="button"
                      key={policy.id}
                      className={`option-card ${selected ? "is-selected" : ""}`}
                      onClick={() => choosePolicy(policy.id)}
                    >
                      <span>
                        <span className="option-title d-block">{policy.policy_name}</span>
                        <span className="option-sub">{policy.policy_details}</span>
                      </span>
                      {selected && (
                        <span className="option-check">
                          <i className="bi bi-check-circle-fill" />
                        </span>
                      )}
                    </button>
                  );
                })}
              </div>
            )}
          </div>
        );
      })}

      {!draft.policyId && touched && (
        <div className="text-danger small mt-3">Please select a policy before continuing.</div>
      )}
    </div>
  );
}
