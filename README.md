# tfasoft-backend

Core API, data models, and consensus logic for the TFASOFT platform.

This backend powers the Android, iOS, and web interfaces for the TFASOFT election transparency system.

---

## ✅ Features Implemented

- 🔐 **Authentication & Role Management**
    - User registration and login
    - Role-based access control (`ORDINARY`, `FAIR`, `ADMIN_LEVEL_1`, `ADMIN_LEVEL_2`, `AUDITOR`)
    - JWT-based token auth

- 🗳️ **Result Capture**
    - Submit one result per user with per-candidate vote counts
    - FAIR users restricted to their assigned polling station
    - Automatic consensus update

- 🖼️ **Media Upload & Background Sync**
    - Upload up to 10 photos/videos with result submission
    - AWS S3 integration + local metadata caching
    - Background upload retry with `localMediaId`

- 🗺️ **Polling Structure Navigation**
    - Region → Division → Subdivision → Center → Polling Station drill-down

- 📊 **Consensus Aggregation**
    - Top 3 candidates + other group summary per zone
    - Real-time percentage agreement by candidate
    - Submission deltas from consensus

- 🚨 **Suspicion Reporting & Fraud Review**
    - Any logged-in user can flag suspicious submissions with reason
    - `ADMIN_LEVEL_1` can mark as rejection candidate
    - `ADMIN_LEVEL_2` can vote on removability
    - Submissions are excluded from consensus if >50% of level 2 admins agree

- 📜 **Audit Logging**
    - Full log of all submissions (user, IP, device, snapshot)
    - Admin/auditor-only access to logs

---

## 📁 Key Directories

- `grails-app/domain/` — Domain classes (users, results, audits, reviews)
- `grails-app/services/` — Core business logic
- `grails-app/controllers/` — REST API endpoints
- `grails-app/conf/UrlMappings.groovy` — Versioned API routing (`/api/v1/...`)
- `docs/` — OpenAPI spec and usage guide

---

## 📦 API Documentation

The OpenAPI spec is defined in [`docs/api/openapi.yaml`](docs/api/openapi.yaml). You can preview it using:

- Swagger UI: https://tfa-elections.github.io/tfasoft-docs
- Swagger Editor: https://editor.swagger.io

---

## 🚀 Running Locally

Ensure you have Java 11+ and Grails 5.2.3 installed. Then:

```bash
./grailsw run-app
```

Environment config is set in `application.yml`. PostgreSQL and AWS S3 keys should be injected via environment variables or secure config.

---

## 📬 Contact

For development or contribution inquiries:
- GitHub: https://github.com/tfa-elections
- Email: [contact@tfacam.org](mailto:contact@tfacam.org)
