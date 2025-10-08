Write-Host "Running flake8 to verify code style..."
flake8 src --max-line-length=120 --exclude=__init__.py

Write-Host "Organizing imports with isort..."
isort src

Write-Host "Formatting code with black..."
black src

Write-Host "Running flake8 to verify code style..."
flake8 src --max-line-length=120 --exclude=__init__.py

Write-Host "Code formatting and style checks complete."
