import { useState } from "react";
import { Form } from "react-bootstrap";
import { Navigate, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const DEMO_PASSWORD = "cgi123"; // Backend seeded password
const DEMO_PHONE = "0708013267"; // Backend seeded phone number

export default function LoginPage() {
  const { login, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [identifier, setIdentifier] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [touched, setTouched] = useState({ identifier: false, password: false });
  const [isLoading, setIsLoading] = useState(false);

  if (isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    const trimmedIdentifier = identifier.trim();

    if (!trimmedIdentifier || !password) {
      setTouched({ identifier: true, password: true });
      setError("Enter your phone number and password.");
      return;
    }

    setIsLoading(true);
    const result = await login(trimmedIdentifier, password);
    setIsLoading(false);

    if (!result.ok) {
      setError(result.error);
      return;
    }
    navigate("/");
  }

  function fillDemo() {
    // Use phone number (backend requirement) instead of email
    setIdentifier(DEMO_PHONE);
    setPassword(DEMO_PASSWORD);
    setError("");
    setTouched({ identifier: false, password: false });
  }

  const identifierError = touched.identifier && !identifier.trim();
  const passwordError = touched.password && !password;
  const isFormValid = Boolean(identifier.trim() && password);

  return (
    <div className="login-shell">
      <div className="login-card">
        <div className="login-mark">C</div>
        <h4 className="mb-1">Sign in to Logsheet</h4>
        <p className="text-secondary small mb-4">Ceylinco Sales Executive Portal</p>

        <Form onSubmit={handleSubmit}>
          <Form.Group className="mb-3" controlId="identifier">
            <Form.Label>Phone number</Form.Label>
            <Form.Control
              type="text"
              value={identifier}
              onChange={(e) => {
                setIdentifier(e.target.value);
                setError("");
              }}
              onBlur={() => setTouched((prev) => ({ ...prev, identifier: true }))}
              autoFocus
              isInvalid={identifierError}
            />
            <Form.Control.Feedback type="invalid">
              Enter your phone number.
            </Form.Control.Feedback>
          </Form.Group>

          <Form.Group className="mb-3" controlId="password">
            <Form.Label>Password</Form.Label>
            <Form.Control
              type="password"
              value={password}
              onChange={(e) => {
                setPassword(e.target.value);
                setError("");
              }}
              onBlur={() => setTouched((prev) => ({ ...prev, password: true }))}
              isInvalid={passwordError}
            />
            <Form.Control.Feedback type="invalid">
              Enter your password.
            </Form.Control.Feedback>
          </Form.Group>

          {error && <div className="alert alert-danger small mb-3">{error}</div>}

          <button 
            type="submit" 
            className="btn btn-gold w-100 mb-3" 
            disabled={!isFormValid || isLoading}
          >
            {isLoading ? "Signing in..." : "Log in"}
          </button>
        </Form>

        <div className="demo-hint">
          Use the seeded account below to sign in: {" "}
          <code>{DEMO_PHONE}</code> / <code>{DEMO_PASSWORD}</code>, or{" "}
          <button type="button" className="btn btn-link btn-sm p-0 align-baseline" onClick={fillDemo}>
            autofill it
          </button>
          .
        </div>
      </div>
    </div>
  );
}
