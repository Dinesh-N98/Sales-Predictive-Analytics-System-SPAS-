import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { SE_LEVELS, findById } from "../../data/mockData";

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
  const navigate = useNavigate();

  if (!currentSe) return null;

  const level = findById(SE_LEVELS, currentSe.se_level_id);

  function handleLogout() {
    logout();
    navigate("/login");
  }

  return (
    <div className="portal-navbar navbar-portal justify-content-between">
      <div className="brand-mark">
        <span className="brand-chip">C</span>
        <span>
          Logsheet
          <span className="brand-sub">Ceylinco Sales Portal</span>
        </span>
      </div>

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
            {level?.level_name}
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
