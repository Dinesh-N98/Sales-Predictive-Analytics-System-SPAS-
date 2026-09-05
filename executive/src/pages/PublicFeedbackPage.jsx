import { useEffect, useState } from "react";
import { Form } from "react-bootstrap";
import { useParams } from "react-router-dom";

const API_BASE = "http://localhost:24742/backend/api";

const RATINGS = [
  { value: 1, label: "Very poor" },
  { value: 2, label: "Poor" },
  { value: 3, label: "Okay" },
  { value: 4, label: "Good" },
  { value: 5, label: "Excellent" },
];

export default function PublicFeedbackPage() {
  const { token } = useParams();
  const [rating, setRating] = useState(null);
  const [strengths, setStrengths] = useState([]);
  const [improvements, setImprovements] = useState([]);
  const [strengthId, setStrengthId] = useState("");
  const [improvementId, setImprovementId] = useState("");
  const [lookupError, setLookupError] = useState("");
  const [comments, setComments] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [isSubmitted, setIsSubmitted] = useState(false);

  useEffect(() => {
    let isActive = true;

    async function fetchFeedbackLookups() {
      try {
        const [strengthsResponse, improvementsResponse] = await Promise.all([
          fetch(`${API_BASE}/feedback-strengths`),
          fetch(`${API_BASE}/feedback-improvements`),
        ]);
        if (!strengthsResponse.ok || !improvementsResponse.ok) {
          throw new Error("Feedback options are temporarily unavailable.");
        }
        const [strengthsData, improvementsData] = await Promise.all([
          strengthsResponse.json(),
          improvementsResponse.json(),
        ]);
        if (isActive) {
          setStrengths(strengthsData);
          setImprovements(improvementsData);
        }
      } catch (lookupFetchError) {
        if (isActive) setLookupError(lookupFetchError.message);
      }
    }

    fetchFeedbackLookups();
    return () => {
      isActive = false;
    };
  }, []);

  async function handleSubmit(event) {
    event.preventDefault();
    if (!rating || !token) {
      setError(!token ? "This feedback link is incomplete." : "Please choose a rating.");
      return;
    }

    setIsSubmitting(true);
    setError("");

    try {
      const response = await fetch(`${API_BASE}/public/client-feedbacks/${encodeURIComponent(token)}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          rating,
          strengthId: strengthId ? Number(strengthId) : null,
          improvementId: improvementId ? Number(improvementId) : null,
          comments: comments.trim() || null,
        }),
      });

      let responseBody = null;
      try {
        responseBody = await response.json();
      } catch {
        // Some backend errors intentionally have no response body.
      }

      if (!response.ok) {
        if (response.status === 404) {
          throw new Error("This feedback link is invalid or no longer available.");
        }
        throw new Error(responseBody?.message || "Feedback could not be submitted. Please try again.");
      }

      setIsSubmitted(true);
    } catch (submitError) {
      setError(submitError.message || "Feedback could not be submitted. Please try again.");
    } finally {
      setIsSubmitting(false);
    }
  }

  if (isSubmitted) {
    return (
      <main className="public-feedback-shell">
        <section className="public-feedback-card feedback-complete" aria-live="polite">
          <div className="public-feedback-mark" aria-hidden="true">
            <i className="bi bi-check-lg" />
          </div>
          <p className="public-feedback-kicker">Ceylinco Insurance</p>
          <h1>Thank you for your feedback</h1>
          <p className="public-feedback-copy">Your response has been recorded and helps us improve the service we provide.</p>
        </section>
      </main>
    );
  }

  return (
    <main className="public-feedback-shell">
      <section className="public-feedback-card">
        <div className="public-feedback-mark" aria-hidden="true">C</div>
        <p className="public-feedback-kicker">Ceylinco Insurance</p>
        <h1>How was your experience?</h1>
        <p className="public-feedback-copy">Please take a moment to tell us about your recent interaction.</p>
        <aside className="public-feedback-privacy" aria-label="Privacy notice">
          <i className="bi bi-info-circle" aria-hidden="true" />
          <p>
            <strong>Your privacy matters to us.</strong>{" "}
            Ceylinco Insurance may use anonymized details from your interactions including financial profile category to improve service quality and sales processes. Your feedback and personal data are processed in line with Sri Lanka's Personal Data Protection Act, No. 9 of 2022. Data is never shared with third parties for marketing purposes.
          </p>
        </aside>

        <Form onSubmit={handleSubmit}>
          <fieldset className="rating-fieldset">
            <legend>How would you rate your experience?</legend>
            <div className="rating-grid">
              {RATINGS.map((option) => (
                <label className={`rating-option ${rating === option.value ? "is-selected" : ""}`} key={option.value}>
                  <input
                    type="radio"
                    name="rating"
                    value={option.value}
                    checked={rating === option.value}
                    onChange={() => {
                      setRating(option.value);
                      setError("");
                    }}
                  />
                  <span className="rating-number">{option.value}</span>
                  <span className="rating-label">{option.label}</span>
                </label>
              ))}
            </div>
          </fieldset>

          <div className="public-feedback-selects">
            <Form.Group controlId="feedback-strength" className="mb-3">
              <Form.Label>What went well? <span className="text-secondary">Optional</span></Form.Label>
              <Form.Select value={strengthId} onChange={(event) => setStrengthId(event.target.value)}>
                <option value="">Select an option</option>
                {strengths.map((strength) => (
                  <option value={strength.id} key={strength.id}>{strength.strengthName}</option>
                ))}
              </Form.Select>
            </Form.Group>

            <Form.Group controlId="feedback-improvement" className="mb-4">
              <Form.Label>What could be improved? <span className="text-secondary">Optional</span></Form.Label>
              <Form.Select value={improvementId} onChange={(event) => setImprovementId(event.target.value)}>
                <option value="">Select an option</option>
                {improvements.map((improvement) => (
                  <option value={improvement.id} key={improvement.id}>{improvement.improvementName}</option>
                ))}
              </Form.Select>
            </Form.Group>
          </div>

          <Form.Group className="mb-4" controlId="feedback-comments">
            <Form.Label>Anything else you would like us to know? <span className="text-secondary">Optional</span></Form.Label>
            <Form.Control
              as="textarea"
              rows={4}
              maxLength={10000}
              value={comments}
              onChange={(event) => setComments(event.target.value)}
              placeholder="Share a comment about your experience"
            />
          </Form.Group>

          {lookupError && <div className="form-text mb-3">{lookupError} You can still submit a rating and comment.</div>}

          {error && <div className="alert alert-danger small" role="alert">{error}</div>}

          <button type="submit" className="btn btn-gold w-100" disabled={isSubmitting || !token}>
            {isSubmitting ? "Submitting..." : "Submit feedback"}
          </button>
        </Form>
      </section>
    </main>
  );
}
