import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { useLookups } from "../../context/DataStoreContext";

function initialsFor(name) {
  return name
    .split(" ")
    .map((part) => part[0])
    .slice(0, 2)
    .join("")
    .toUpperCase();
}

export default function AppNavbar() {
  const { currentSe, logout } = useAuth();
  const { seLevels } = useLookups();
  const navigate = useNavigate();

  if (!currentSe) return null;

  const level = seLevels.find((item) => String(item.id) === String(currentSe.se_level_id));

  function handleLogout() {
    logout();
    navigate("/login");
  }

  return (
    <div className="portal-navbar navbar-portal justify-content-between">
      <button type="button" className="brand-mark brand-home-button" onClick={() => navigate("/")} aria-label="Go to dashboard">
        <span className="brand-chip">C</span>
        <span>
          Logsheet
          <span className="brand-sub">Ceylinco Sales Portal</span>
        </span>
      </button>

      <div className="d-flex align-items-center gap-2">
        <button
          type="button"
          className="btn btn-sm btn-outline-portal"
          onClick={() => navigate("/history")}
          aria-label="Activity history"
          title="Activity history"
        >
          <i className="bi bi-clock-history" />
        </button>
        <div className="text-end d-none d-sm-block">
          <div className="fw-semibold small lh-1">{currentSe.full_name}</div>
          <div className="text-secondary lh-1" style={{ fontSize: "0.7rem" }}>
            {level?.level_name || currentSe.se_level_name || ""}
          </div>
        </div>
        <div className="se-avatar" title={currentSe.full_name}>
          {initialsFor(currentSe.full_name)}
        </div>
        <button
          type="button"
          className="btn btn-sm btn-outline-portal"
          onClick={handleLogout}
          aria-label="Log out"
        >
          <i className="bi bi-box-arrow-right" />
        </button>
      </div>
    </div>
  );
}
