import { Navigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import AppNavbar from "./AppNavbar";

export default function ProtectedRoute({ children }) {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return (
    <div className="app-shell">
      <AppNavbar />
      <main className="app-main">{children}</main>
    </div>
  );
}
