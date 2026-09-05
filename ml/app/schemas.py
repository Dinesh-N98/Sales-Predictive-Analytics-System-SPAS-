from typing import Literal

from pydantic import BaseModel, Field


class ActivityOutcomeItem(BaseModel):
    id: int
    se_level_name: str
    activity_name: str
    duration_minutes: float
    followup_count: int
    client_type_name: str
    financial_level_name: str
    lead_source_name: str
    policy_name: str
    has_feedback: int = Field(ge=0, le=1)
    rating: float | None = None
    strength_name: str | None = None
    improvement_name: str | None = None
    running_achieved: float
    has_achieved_target: int = Field(ge=0, le=1)
    day_of_week: str


class ActivityOutcomeRequest(BaseModel):
    items: list[ActivityOutcomeItem]


class ActivityOutcomeResult(BaseModel):
    id: int
    prediction: Literal["Sold", "Pending", "Rejected"]
    probabilities: dict[Literal["Pending", "Rejected", "Sold"], float]


class ActivityOutcomeResponse(BaseModel):
    results: list[ActivityOutcomeResult]


class SeTargetForecastItem(BaseModel):
    id: int | str
    avg_activity_per_day: float
    avg_followup_count: float
    avg_duration_minutes: float
    sold_rate: float
    se_level_name: str


class SeTargetForecastRequest(BaseModel):
    items: list[SeTargetForecastItem]


class SeTargetForecastResult(BaseModel):
    id: int | str
    prediction: Literal[0, 1]
    probability_hit_target: float


class SeTargetForecastResponse(BaseModel):
    results: list[SeTargetForecastResult]