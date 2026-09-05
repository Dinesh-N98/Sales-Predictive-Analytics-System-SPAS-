import pandas as pd
from fastapi import APIRouter

from app.ml_loader import get_model1
from app.schemas import ActivityOutcomeRequest, ActivityOutcomeResponse


router = APIRouter()


@router.post("/predict/activity-outcome", response_model=ActivityOutcomeResponse)
def predict_activity_outcome(request: ActivityOutcomeRequest) -> ActivityOutcomeResponse:
    if not request.items:
        return ActivityOutcomeResponse(results=[])

    artifact = get_model1()
    input_df = pd.DataFrame([item.model_dump() for item in request.items])
    feature_columns = artifact["feature_columns"]
    input_df = input_df[feature_columns].copy()

    categorical_columns = artifact["categorical_columns"]
    numeric_columns = artifact["numeric_columns"]
    input_df[categorical_columns] = input_df[categorical_columns].fillna("Missing")
    if "rating" in numeric_columns:
        input_df["rating"] = input_df["rating"].fillna(0)

    pipeline = artifact["pipeline"]
    probabilities = pipeline.predict_proba(input_df)
    classes = list(pipeline.named_steps["clf"].classes_)
    sold_idx = classes.index("Sold")
    pending_idx = classes.index("Pending")
    rejected_idx = classes.index("Rejected")
    sold_threshold = artifact["sold_threshold"]

    results = []
    for item, row in zip(request.items, probabilities):
        prediction = (
            "Sold"
            if row[sold_idx] >= sold_threshold
            else "Pending"
            if row[pending_idx] >= row[rejected_idx]
            else "Rejected"
        )
        results.append(
            {
                "id": item.id,
                "prediction": prediction,
                "probabilities": {
                    "Pending": float(row[pending_idx]),
                    "Rejected": float(row[rejected_idx]),
                    "Sold": float(row[sold_idx]),
                },
            }
        )

    return ActivityOutcomeResponse(results=results)