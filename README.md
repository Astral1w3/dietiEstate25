# Dieti Estates 2025

This README is the English translation of `README_IMPORTANTISSIMO.pdf`.

To run the application you have 2 options:

- test the live website
- run backend (Docker) + frontend locally

## Option 1: Live Website

A live version of the application is available at:

- URL: https://gentle-cliff-05689dc03.3.azurestaticapps.net/

### Important Note: Cold Start Behavior

The backend is hosted on a service that automatically goes to sleep after inactivity.

What this means:

- your first request after inactivity (for example property search or login) may fail, hang, or take up to 30 seconds
- this is the time needed for the backend to wake up

Solution:

- wait a few moments and refresh the page
- the second request should work normally

### Test Login Credentials

Use the following test accounts to explore roles and permissions:

| Role | Email | Password |
| --- | --- | --- |
| Standard User | `lello@gmail.com` | `lello@gmail.com` |
| Real Estate Agent | `fabiananaplesdream@gmail.com` | `fabiananaplesdream@gmail.com` |
| Manager | `giovanninaplesdreams@gmail.com` | `giovanninaplesdreams@gmail.com` |
| Administrator | `angelomarcone4@gmail.com` | `angelomarcone4@gmail.com` |

### New Account Registration

You can also create a new account:

1. Standard registration (email + password)
2. Fast login with Google (Google Sign-In)

Special role creation rules:

- Administrator: this account has the highest privileges (including account management). It cannot be created through the public UI, only through direct DB access.
- Agent and Manager: these roles cannot be created by a standard user. They are created/managed by an Administrator from the app dashboard (an Agent can also be created by a Manager).
- Standard User: any visitor can create this role directly.

## Option 2: Run Locally

### Backend (Docker)

Recommended approach: run backend with Docker.

Prerequisite:

- Docker Desktop installed and running.

1. Load Docker image

Make sure you have `dieti-estates-1.0.tar` on your machine, then run:

```bash
docker load -i dieti-estates-1.0.tar
```

2. Run backend container

```bash
docker run -d -p 8080:8080 \
  --name dieti-backend \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://dietiestates2025v3.postgres.database.azure.com:5432/postgres?sslmode=require" \
  -e SPRING_DATASOURCE_USERNAME="<your_db_user>" \
  -e SPRING_DATASOURCE_PASSWORD="<your_db_password>" \
  -e APPLICATION_SECURITY_JWT_SECRET_KEY="yourGeneratedBase64Key-oJb8V8Yzuw8bFpQ8g7F2aK3eP1cV6sD0lB7jN9hT5yE=" \
  -e SPRING_SERVLET_MULTIPART_LOCATION="/app/uploads" \
  dieti-estates:1.0
```

For debugging:

```bash
docker logs dieti-backend
```

Note:

- some properties may appear without images in the Docker environment because not all sample images are bundled into the Docker image

### Frontend

1. Install Node.js (version >= 20.0.0)
2. Open a terminal in the frontend folder: `dietiestates2025-front`
3. Create a `.env` file with:

```bash
REACT_APP_GEOAPIFY_API_KEY=
REACT_APP_GOOGLE=
```

4. Install dependencies:

```bash
npm install
```

5. Start development server:

```bash
npm start
```

6. Open `http://localhost:3000` in your browser.

By default, frontend uses Azure backend.  
To use local backend, edit `dietiestates2025-front/src/services/api.js`:

- uncomment `//baseURL: 'http://localhost:8080/api'`
- comment the Azure `baseURL`

## Available Scripts (Frontend)

- `npm start`: starts app in development mode
- `npm run build`: creates production build

## Technologies Used

- React 19.1.0
- React Router DOM 7.5.0
- Axios
- Leaflet (maps)
- Chart.js (charts)

## Repository Structure

- `dietiestates2025-back`: Spring Boot backend
- `dietiestates2025-front`: React frontend
- `Database`: DB dump and notes

## Deployment Workflows

- Backend: `.github/workflows/backend-azure-deploy.yml`
- Frontend: `.github/workflows/frontend-azure-static-web-app.yml`

Both workflows are configured for branch `main`.
