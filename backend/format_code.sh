#!/bin/bash
set -e

echo "Running flake8 to verify code style..."
flake8 src tests --max-line-length=120 --exclude=__init__.py

echo "Removing unused imports with autoflake..."
autoflake --in-place --remove-all-unused-imports --remove-unused-variables --recursive src tests

echo "Organizing imports with isort..."
isort src tests

echo "Formatting code with black..."
black src tests

echo "Running flake8 to verify code style..."
flake8 src tests --max-line-length=120 --exclude=__init__.py

echo "Code formatting and style checks complete."
