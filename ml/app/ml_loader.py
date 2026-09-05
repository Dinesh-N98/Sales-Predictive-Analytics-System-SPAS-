from pathlib import Path
from typing import Any

import joblib


MODEL1: dict[str, Any] | None = None
MODEL2: dict[str, Any] | None = None


def load_models() -> None:
    """Load the model artifacts once for the lifetime of the process."""
    global MODEL1, MODEL2

    models_dir = Path(__file__).resolve().parent / "models"
    MODEL1 = joblib.load(models_dir / "model1_activity_outcome.joblib")
    MODEL2 = joblib.load(models_dir / "model2_se_target_forecast.joblib")


def get_model1() -> dict[str, Any]:
    if MODEL1 is None:
        raise RuntimeError("Model 1 has not been loaded")
    return MODEL1


def get_model2() -> dict[str, Any]:
    if MODEL2 is None:
        raise RuntimeError("Model 2 has not been loaded")
    return MODEL2