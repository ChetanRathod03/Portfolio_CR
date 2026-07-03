# Portfolio Backend

Spring Boot API that powers the contact form and the recruiter feedback portal on
Chetan Rathod's portfolio site. Stores submissions in PostgreSQL and emails a
notification (plus an auto-reply to the sender) on every submission.

## Stack
Java 21 &middot; Spring Boot 3.3 &middot; Spring Data JPA &middot; Spring Security &middot; Spring Mail &middot; PostgreSQL &middot; Docker

## What's included
- `POST /api/contact` — general contact form
- `POST /api/recruiter-feedback` — recruiter Q&A / feedback portal
- Bean validation on every field, clean JSON error responses
- Async email notifications (won't slow down the API response)
- Honeypot spam field (`website`) — silently drops bot submissions
- CORS locked to configured frontend origin(s)
- Dockerfile + docker-compose (Postgres + backend + Nginx) for one-command local/EC2 deployment

## What's intentionally NOT included yet
This is a working MVP, not the full spec (admin dashboard, reCAPTCHA, rate
limiting, JWT-authenticated admin login, CSV export). Those are real projects
in their own right — happy to build any of them next if you want them.

## Local setup (without Docker)

1. Install Java 21 and PostgreSQL locally, or run just the `db` service from
   `docker-compose.yml`: `docker compose up db`
2. Create the database:
   ```sql
   CREATE DATABASE portfolio_db;
   CREATE USER portfolio_user WITH PASSWORD 'portfolio_pass';
   GRANT ALL PRIVILEGES ON DATABASE portfolio_db TO portfolio_user;
   ```
3. Copy `.env.example` to `.env` and fill in real values (especially `MAIL_USERNAME`
   / `MAIL_PASSWORD` — see the Gmail note in that file).
4. Export the env vars (or use an IDE run config / `direnv`) and run:
   ```bash
   mvn spring-boot:run
   ```
5. API is live at `http://localhost:8080`.

## Run everything with Docker Compose

```bash
cp .env.example .env      # fill in real DB + mail values first
docker compose up --build
```

This brings up Postgres, the Spring Boot API, and Nginx (serving `./frontend-dist`
and reverse-proxying `/api/*` to the backend) — see **Connecting the frontend** below.

## Connecting the frontend

Build your portfolio frontend to static files and drop the output in
`./frontend-dist` before `docker compose up` (Nginx serves that folder as `/`
and proxies `/api/*` straight to the backend, so the frontend just calls
relative paths like `fetch('/api/contact', ...)` — no CORS to worry about in
production). For local dev without Docker, point the frontend at
`http://localhost:8080` directly and make sure that origin is in
`CORS_ALLOWED_ORIGINS`.

## API contract

### POST /api/contact
```json
{
  "name": "Jane Recruiter",
  "email": "jane@company.com",
  "company": "Acme Corp",
  "designation": "Engineering Manager",
  "phone": "+1 555 123 4567",
  "subject": "Backend role",
  "message": "Would love to chat about an opening.",
  "resumeRequested": true,
  "scheduleInterviewRequested": false,
  "priority": "HIGH"
}
```
`name`, `email`, and `message` are required. Response: `201 Created` with
`{ "success": true, "message": "...", "data": null }`, or `400` with field-level
validation errors.

### POST /api/recruiter-feedback
```json
{
  "name": "Jane Recruiter",
  "company": "Acme Corp",
  "email": "jane@company.com",
  "linkedin": "https://linkedin.com/in/janerecruiter",
  "questionType": "TECHNICAL_QUESTION",
  "message": "How did you handle concurrency in VaultCore?",
  "rating": 5,
  "anonymous": false
}
```
`questionType` must be one of: `GENERAL_QUESTION`, `TECHNICAL_QUESTION`,
`PROJECT_CLARIFICATION`, `INTERVIEW_INVITATION`, `JOB_OPPORTUNITY`, `FEEDBACK`,
`SUGGESTION`, `OTHER`. `email` and `message` are required even when
`anonymous: true` (the email is stored for spam control but never emailed to
you if anonymous, and the name is stored as "Anonymous").

## Database schema
Tables (`contacts`, `recruiter_feedback`) are created automatically by
Hibernate (`ddl-auto: update`) on first run — no manual migration needed for
this MVP. Columns match the entity fields in `entity/Contact.java` and
`entity/RecruiterFeedback.java`, including `status` (`UNREAD`/`READ`/
`RESPONDED`/`ARCHIVED`) for a future admin dashboard, and `ip_address` for
basic spam/abuse tracing.

## Deploying to AWS EC2 (basic path)

1. Launch an EC2 instance (Ubuntu 22.04, t3.small or larger), open ports 80
   and 443 in the security group.
2. Install Docker + Docker Compose plugin on the instance.
3. Copy this project (and your built `frontend-dist`) to the instance
   (`scp` or `git clone`).
4. Create `.env` on the instance with production values — real DB password,
   real Gmail app password, and `CORS_ALLOWED_ORIGINS` set to your real domain.
5. `docker compose up -d --build`
6. Point your domain's DNS A record at the instance's public IP.
7. Put a TLS certificate in front (easiest: run Certbot for Let's Encrypt
   against the Nginx container, or put an AWS Application Load Balancer with
   an ACM certificate in front of the instance).

## Environment variables
See `.env.example` for the full list with descriptions.
