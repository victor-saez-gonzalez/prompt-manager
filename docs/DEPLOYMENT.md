# Deployment Guide for Prompt Manager Backend

This document explains how to build, push, and deploy the backend service to Google Cloud Run.

---

## 1. Build the Docker Image

Build the Docker image locally:

```bash
docker build -t europe-west1-docker.pkg.dev/prompt-manager-456522/prompt-manager-repo/prompt-manager-backend:latest .
```

---

## 2. Push the Docker Image to Artifact Registry

Push the image to Artifact Registry:

```bash
docker push europe-west1-docker.pkg.dev/prompt-manager-456522/prompt-manager-repo/prompt-manager-backend:latest
```

---

## 3. Deploy the Image to Cloud Run

Deploy the image to Cloud Run:

```bash
gcloud run deploy prompt-manager-backend \
  --image=europe-west1-docker.pkg.dev/prompt-manager-456522/prompt-manager-repo/prompt-manager-backend:latest \
  --platform=managed \
  --region=europe-west1 \
  --allow-unauthenticated \
  --set-env-vars=SPRING_PROFILES_ACTIVE=docker,DB_USER=your_user,DB_PASSWORD=your_password,DB_NAME=your_db
```

---

## 4. Environment Variables Description

| Variable               | Description                         |
|-------------------------|-------------------------------------|
| `SPRING_PROFILES_ACTIVE` | Active Spring Boot profile (e.g., docker) |
| `DB_USER`               | PostgreSQL database username       |
| `DB_PASSWORD`           | PostgreSQL database password       |
| `DB_NAME`               | PostgreSQL database name           |

---

## 5. Prerequisites

- Docker must be installed and configured.
- Google Cloud SDK (`gcloud`) must be installed and authenticated.
- The project must have an Artifact Registry and Cloud Run enabled.
- The Cloud Run service must have permission to pull images from Artifact Registry.

---
