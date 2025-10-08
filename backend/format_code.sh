#!/bin/bash
set -e

echo "Running flake8 to verify code style..."
flake8 src --max-line-length=120 --exclude=__init__.py

echo "Organizing imports with isort..."
isort src

echo "Formatting code with black..."
black src

echo "Running flake8 to verify code style..."
flake8 src --max-line-length=120 --exclude=__init__.py

echo "Code formatting and style checks complete."
