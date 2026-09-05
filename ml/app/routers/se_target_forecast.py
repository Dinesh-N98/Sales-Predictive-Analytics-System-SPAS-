import pandas as pd
from fastapi import APIRouter

from app.ml_loader import get_model2
from app.schemas import SeTargetForecastRequest, SeTargetForecastResponse


router = APIRouter()


@router.post("/predict/se-target-forecast", response_model=SeTargetForecastResponse)
def predict_se_target_forecast(
    request: SeTargetForecastRequest,
) -> SeTargetForecastResponse:
    if not request.items:
        return SeTargetForecastResponse(results=[])

    artifact = get_model2()
    input_df = pd.DataFrame([item.model_dump() for item in request.items])
    input_df = input_df[artifact["feature_columns"]]
    raw_features = pd.get_dummies(
        input_df,
        columns=artifact["categorical_columns"],
        drop_first=True,
    )
    raw_features = raw_features.reindex(
        columns=artifact["dummy_columns"],
        fill_value=0,
    )

    scaled_features = artifact["scaler"].transform(raw_features)
    probabilities = artifact["model"].predict_proba(scaled_features)[:, 1]
    predictions = (probabilities >= 0.5).astype(int)

    return SeTargetForecastResponse(
        results=[
            {
                "id": item.id,
                "prediction": int(prediction),
                "probability_hit_target": float(probability),
            }
            for item, prediction, probability in zip(
                request.items, predictions, probabilities
            )
        ]
    )