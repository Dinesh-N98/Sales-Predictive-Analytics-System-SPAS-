# SPAS ML Serving Layer

FastAPI microservice for the two pre-trained SPAS scikit-learn models. It is
intended for calls from the Jakarta EE backend, not direct frontend access.

## Python environment

Use Python 3.12. The model artifacts require `scikit-learn==1.6.1`, which is
not available as a prebuilt wheel for Python 3.14.

From `ml/` in PowerShell:

```powershell
py -3.12 -m venv venv
.\venv\Scripts\Activate.ps1
python -m pip install --upgrade pip
pip install -r requirements.txt
```

## Run

```powershell
uvicorn app.main:app --reload
```

The service exposes `GET /health`, `POST /predict/activity-outcome`, and
`POST /predict/se-target-forecast`.