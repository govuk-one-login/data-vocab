# syntax=docker/dockerfile:1

ARG PYTHON_VERSION=3.11.5
FROM python:${PYTHON_VERSION}-slim

# Prevents Python from writing pyc files.
ENV PYTHONDONTWRITEBYTECODE=1

# Keeps Python from buffering stdout and stderr to avoid situations where
# the application crashes without emitting any logs due to buffering.
ENV PYTHONUNBUFFERED=1

RUN pip install poetry

# Install dependencies
RUN apt-get update && \
    apt-get install -y \
    gcc \
    git \
    nodejs

WORKDIR /app

ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --uid "${UID}" \
    vocabuser

RUN chown vocabuser:vocabuser /app

# Switch to the non-privileged user to run the application.
USER vocabuser

# Copy the source code into the container.
COPY --chown=vocabuser:vocabuser . /app

# Install dependencies
RUN poetry install

ENV ROOT_DIR=/app
