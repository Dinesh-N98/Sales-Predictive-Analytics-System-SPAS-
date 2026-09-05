import { useEffect, useState } from "react";
import { Form } from "react-bootstrap";
import { useDataStore, useLookups } from "../../context/DataStoreContext";
import { findById } from "../../data/mockData";
import EmptyState from "../common/EmptyState";

export function isExistingCustomerStepValid(draft) {
  return Boolean(draft.selectedClientId);
}

export default function StepExistingCustomerSearch({ draft, updateDraft }) {
  const { searchClients, getActivitiesForClient } = useDataStore();
  const { clientTypes, financialLevels, policies } = useLookups();
  const [query, setQuery] = useState("");
  const [results, setResults] = useState([]);
  const [searchLoading, setSearchLoading] = useState(false);
  const [searchError, setSearchError] = useState(null);

  const selectedClient = draft.client;

  useEffect(() => {
    const normalizedQuery = query.trim();
    if (normalizedQuery.length < 2) {
      setResults([]);
      setSearchError(null);
      setSearchLoading(false);
      return undefined;
    }

    let cancelled = false;
    setSearchLoading(true);
    setSearchError(null);

    const timeoutId = window.setTimeout(async () => {
      try {
        const matches = await searchClients(normalizedQuery);
        if (!cancelled) setResults(matches);
      } catch (err) {
        if (!cancelled) {
          setResults([]);
          setSearchError(err.message);
        }
      } finally {
        if (!cancelled) setSearchLoading(false);
      }
    }, 250);

    return () => {
      cancelled = true;
      window.clearTimeout(timeoutId);
    };
  }, [query, searchClients]);

  function selectClient(client) {
    updateDraft({ client, selectedClientId: client.id, policyId: client.last_policy_id || null });
    setQuery("");
  }

  function clearSelection() {
    updateDraft({ client: null, selectedClientId: null });
  }

  if (selectedClient) {
    const clientType = findById(clientTypes, selectedClient.client_type_id);
    const financialLevel = findById(financialLevels, selectedClient.financial_level_id);
    const lastPolicy = findById(policies, selectedClient.last_policy_id);
    const priorActivityCount = getActivitiesForClient(selectedClient.id).length;

    return (
      <div className="wizard-fade">
        <h5 className="step-heading">Customer loaded</h5>
        <p className="step-subheading">Their details came in automatically — nothing to retype.</p>

        <div className="client-summary-card mb-3">
          <div className="d-flex justify-content-between align-items-start mb-2">
            <div>
              <div className="fw-bold">{selectedClient.full_name}</div>
              <div className="text-secondary small">{selectedClient.contact_number} · {selectedClient.nic}</div>
            </div>
            <button type="button" className="btn btn-sm btn-outline-portal" onClick={clearSelection}>
              Change
            </button>
          </div>
          <div className="d-flex flex-wrap gap-2 mb-2">
            {clientType && <span className="badge-segment">{clientType.type_name}</span>}
            {financialLevel && <span className="badge-segment">{financialLevel.level_name} value</span>}
          </div>
          {lastPolicy && (
            <div className="small text-secondary">
              Last discussed: <strong className="text-dark">{lastPolicy.policy_name}</strong>
            </div>
          )}
          <div className="small text-secondary">
            {priorActivityCount} previous {priorActivityCount === 1 ? "activity" : "activities"} logged
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="wizard-fade">
      <h5 className="step-heading">Find the customer</h5>
      <p className="step-subheading">Search by name, phone number, or NIC.</p>

      <div className="search-input-wrap mb-3">
        <i className="bi bi-search search-icon" />
        <Form.Control
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          autoFocus
        />
      </div>

      {searchLoading && <p className="text-secondary small">Searching customers...</p>}

      {searchError && <div className="alert alert-danger small">{searchError}</div>}

      {!searchLoading && !searchError && query.trim().length >= 2 && results.length === 0 && (
        <EmptyState
          icon="bi-person-x"
          title="No matches"
          copy="Double-check the spelling, or add them as a new customer instead."
        />
      )}

      {results.length > 0 && (
        <div className="list-card">
          {results.map((client) => (
            <button type="button" key={client.id} className="list-row" onClick={() => selectClient(client)}>
              <span className="list-row-icon">
                <i className="bi bi-person-fill" />
              </span>
              <span className="list-row-body">
                <span className="list-row-title">{client.full_name}</span>
                <span className="list-row-sub">{client.contact_number} · {client.nic}</span>
              </span>
              <span className="list-row-meta">
                <i className="bi bi-chevron-right" />
              </span>
            </button>
          ))}
        </div>
      )}

      {query.trim().length < 2 && (
        <EmptyState
          icon="bi-search"
          title="Search to get started"
          copy="Type at least 2 characters of their name, phone, or NIC."
        />
      )}
    </div>
  );
}
