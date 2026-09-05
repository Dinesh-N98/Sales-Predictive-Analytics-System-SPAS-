import { useState } from "react";
import { Form, Row, Col } from "react-bootstrap";
import { useLookups } from "../../context/DataStoreContext";

const emptyClient = {
  full_name: "",
  address: "",
  contact_number: "",
  nic: "",
  email: "",
  client_type_id: "",
  financial_level_id: "",
  lead_source_id: "",
};

export function isNewCustomerFormValid(client) {
  if (!client) return false;
  return Boolean(
    client.full_name?.trim() &&
      client.contact_number?.trim() &&
      client.client_type_id &&
      client.financial_level_id &&
      client.lead_source_id
  );
}

export default function StepNewCustomerForm({ draft, updateDraft }) {
  const { clientTypes, financialLevels, leadSources } = useLookups();
  const client = { ...emptyClient, ...draft.client };
  const [touched, setTouched] = useState({});

  function setField(field, value) {
    updateDraft({ client: { ...client, [field]: value } });
  }

  function sanitizeName(value) {
    return value.replace(/[^a-zA-Z\s'-]/g, "");
  }

  function sanitizePhone(value) {
    return value.replace(/[^0-9]/g, "");
  }

  function markTouched(field) {
    setTouched((prev) => ({ ...prev, [field]: true }));
  }

  const fullNameError = touched.full_name && !client.full_name.trim();
  const contactNumberError = touched.contact_number && !client.contact_number.trim();
  const clientTypeError = touched.client_type_id && !client.client_type_id;
  const financialLevelError = touched.financial_level_id && !client.financial_level_id;
  const leadSourceError = touched.lead_source_id && !client.lead_source_id;

  return (
    <div className="wizard-fade">
      <h5 className="step-heading">New customer details</h5>
      <p className="step-subheading">Only takes a minute — we'll remember this for next time.</p>

      <Form>
        <Form.Group className="mb-3" controlId="fullName">
          <Form.Label>Full name *</Form.Label>
          <Form.Control
            type="text"
            value={client.full_name}
            onChange={(e) => setField("full_name", sanitizeName(e.target.value))}
            onBlur={() => markTouched("full_name")}
            autoFocus
            isInvalid={fullNameError}
          />
          <Form.Control.Feedback type="invalid">
            Enter the customer's full name.
          </Form.Control.Feedback>
        </Form.Group>

        <Row>
          <Col xs={12} sm={6}>
            <Form.Group className="mb-3" controlId="contactNumber">
              <Form.Label>Contact number *</Form.Label>
              <Form.Control
                type="tel"
                value={client.contact_number}
                onChange={(e) => setField("contact_number", sanitizePhone(e.target.value))}
                onBlur={() => markTouched("contact_number")}
                isInvalid={contactNumberError}
              />
              <Form.Control.Feedback type="invalid">
                Enter a contact number.
              </Form.Control.Feedback>
            </Form.Group>
          </Col>
          <Col xs={12} sm={6}>
            <Form.Group className="mb-3" controlId="nic">
              <Form.Label>NIC</Form.Label>
              <Form.Control
                type="text"
                value={client.nic}
                onChange={(e) => setField("nic", e.target.value)}
              />
            </Form.Group>
          </Col>
        </Row>

        <Form.Group className="mb-3" controlId="email">
          <Form.Label>Email</Form.Label>
          <Form.Control
            type="email"
            value={client.email}
            onChange={(e) => setField("email", e.target.value)}
          />
        </Form.Group>

        <Form.Group className="mb-3" controlId="address">
          <Form.Label>Address</Form.Label>
          <Form.Control
            as="textarea"
            rows={2}
            value={client.address}
            onChange={(e) => setField("address", e.target.value)}
          />
        </Form.Group>

        <Form.Group className="mb-3" controlId="clientType">
          <Form.Label>Client type *</Form.Label>
          <Form.Select
            value={client.client_type_id}
            onChange={(e) => setField("client_type_id", Number(e.target.value) || "")}
            onBlur={() => markTouched("client_type_id")}
            isInvalid={clientTypeError}
          >
            <option value="">Select client type</option>
            {clientTypes.map((t) => (
              <option key={t.id} value={t.id}>
                {t.type_name}
              </option>
            ))}
          </Form.Select>
          <Form.Control.Feedback type="invalid">
            Pick a client type.
          </Form.Control.Feedback>
        </Form.Group>

        <Row>
          <Col xs={12} sm={6}>
            <Form.Group className="mb-3" controlId="financialLevel">
              <Form.Label>Financial level *</Form.Label>
              <Form.Select
                value={client.financial_level_id}
                onChange={(e) => setField("financial_level_id", Number(e.target.value) || "")}
                onBlur={() => markTouched("financial_level_id")}
                isInvalid={financialLevelError}
              >
                <option value="">Select level</option>
                {financialLevels.map((f) => (
                  <option key={f.id} value={f.id}>
                    {f.level_name}
                  </option>
                ))}
              </Form.Select>
              <Form.Control.Feedback type="invalid">
                Choose a financial level.
              </Form.Control.Feedback>
            </Form.Group>
          </Col>
          <Col xs={12} sm={6}>
            <Form.Group className="mb-3" controlId="leadSource">
              <Form.Label>Lead source *</Form.Label>
              <Form.Select
                value={client.lead_source_id}
                onChange={(e) => setField("lead_source_id", Number(e.target.value) || "")}
                onBlur={() => markTouched("lead_source_id")}
                isInvalid={leadSourceError}
              >
                <option value="">Select source</option>
                {leadSources.map((s) => (
                  <option key={s.id} value={s.id}>
                    {s.source_name}
                  </option>
                ))}
              </Form.Select>
              <Form.Control.Feedback type="invalid">
                Pick a lead source.
              </Form.Control.Feedback>
            </Form.Group>
          </Col>
        </Row>
      </Form>
    </div>
  );
}
