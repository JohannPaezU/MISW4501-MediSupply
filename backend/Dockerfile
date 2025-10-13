FROM python:3.12-slim

WORKDIR /app

COPY requirements.txt /app/
RUN pip3 install --upgrade pip
RUN pip3 install -r requirements.txt

COPY . /app/

CMD ["sh", "-c", "uvicorn src.main:app --host 0.0.0.0 --port $APP_PORT"]