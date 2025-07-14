# TFASOFT API Documentation

This directory contains the OpenAPI specification for the TFASOFT public REST API.

## 📄 File
- `openapi.yaml` — Swagger-compliant API definition for authentication, result submission, media uploads, and consensus services.

---

## 🔍 View the API Locally (Swagger UI)

To visualize and explore the API, you can use [Swagger UI](https://swagger.io/tools/swagger-ui/) locally:

### Option 1: Run with Docker
```bash
docker run -p 8081:8080 \
  -e SWAGGER_JSON=/mnt/openapi.yaml \
  -v "$PWD/openapi.yaml:/mnt/openapi.yaml" \
  swaggerapi/swagger-ui
```
Then visit:
```
http://localhost:8081
```

### Option 2: Use Swagger Editor (Web)
- Go to: [https://editor.swagger.io](https://editor.swagger.io)
- Copy-paste or upload `openapi.yaml` to preview the API

---

## 📘 Use with Redoc (Optional)

You can also render the documentation using [Redoc](https://redocly.com/openapi/):

### Option 1: Redoc CLI
```bash
npx redoc-cli preview openapi.yaml
```
Then open:
```
http://localhost:8080
```

### Option 2: Embed in HTML
```html
<!DOCTYPE html>
<html>
  <head><title>TFASOFT API</title></head>
  <body>
    <redoc spec-url="openapi.yaml"></redoc>
    <script src="https://cdn.redoc.ly/redoc/latest/bundles/redoc.standalone.js"></script>
  </body>
</html>
```

---

## 📌 Maintenance
- Keep this file in sync with `tfasoft-backend` endpoints.
- Update parameters, responses, and security definitions as needed.

For help maintaining or expanding the spec, see the [OpenAPI 3.0 documentation](https://swagger.io/specification/).
