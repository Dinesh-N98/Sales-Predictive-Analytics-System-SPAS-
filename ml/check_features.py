import joblib
a1 = joblib.load(r"D:\SPAS\ml\app\models\model1_activity_outcome.joblib")
print(a1["feature_columns"])
print(a1["categorical_columns"])
print(a1["numeric_columns"])

a2 = joblib.load(r"D:\SPAS\ml\app\models\model2_se_target_forecast.joblib")
print(a2["feature_columns"])
print(a2["categorical_columns"])
print(a2["dummy_columns"])