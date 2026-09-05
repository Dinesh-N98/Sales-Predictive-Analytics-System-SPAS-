from contextlib import asynccontextmanager

from fastapi import FastAPI

from app import ml_loader
from app.routers import activity_outcome, se_target_forecast


@asynccontextmanager
async def lifespan(app: FastAPI):
	ml_loader.load_models()
	yield


app = FastAPI(title="SPAS ML Serving API", lifespan=lifespan)

# TODO: Add service authentication and network restrictions before real deployment.
app.include_router(activity_outcome.router)
app.include_router(se_target_forecast.router)


@app.get("/health")
def health() -> dict[str, str]:
	return {"status": "ok"}
