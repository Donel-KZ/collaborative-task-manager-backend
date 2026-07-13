# Task Manager API Documentation

## Overview

Task Manager API is a Spring Boot backend for individual and group project task management. It supports:

- User authentication and registration
- Password reset flow
- Projects with owner/member roles
- Deliverables grouped under projects
- Task items grouped under deliverables
- JWT-based authentication
- In-memory H2 database by default
- Optional PostgreSQL profile via `postgres`

## Domain Model

- `UserAccount`: application user with `displayName`, `email`, and optional profile picture.
- `Project`: belongs to one owner, can be `INDIVIDUAL` or `GROUP`, has `PENDING` or `FINISHED` status.
- `ProjectMembership`: links users to group projects with roles `OWNER` or `MEMBER`.
- `Deliverable`: belongs to a project, has title, description, due date, and status.
- `TaskItem`: belongs to a deliverable, has title, status, optional due date, and past-due indicator.

## Authentication

The API uses JWT authentication.

- unauthenticated endpoints: `POST /api/auth/**`, `GET /h2-console/**`, and static profile pictures under `/uploads/profile-pictures/**`
- all other endpoints require a valid `Authorization: Bearer <token>` header.

### Auth endpoints

#### `POST /api/auth/register`

Request body:

```json
{
  "displayName": "string",
  "email": "user@example.com",
  "password": "string (min 8 chars)"
}
```

Response body:

```json
{
  "tokenType": "Bearer",
  "accessToken": "string",
  "user": {
    "id": 1,
    "displayName": "string",
    "email": "user@example.com",
    "profilePictureUrl": null
  }
}
```

#### `POST /api/auth/login`

Request body:

```json
{
  "email": "user@example.com",
  "password": "string"
}
```

Response body:

```json
{
  "tokenType": "Bearer",
  "accessToken": "string",
  "user": {
    "id": 1,
    "displayName": "string",
    "email": "user@example.com",
    "profilePictureUrl": "string or null"
  }
}
```

#### `POST /api/auth/forgot-password`

Request body:

```json
{
  "email": "user@example.com"
}
```

Response body:

```json
{
  "message": "If this email exists, a reset link has been sent."
}
```

#### `POST /api/auth/reset-password`

Request body:

```json
{
  "token": "string",
  "password": "string (min 8 chars)"
}
```

Response body:

```json
{
  "message": "Password has been reset."
}
```

## User endpoints

#### `GET /api/users`

Returns all users.

#### `GET /api/users/me`

Returns the currently authenticated user's details.

#### `PATCH /api/users/me/profile-picture`

Consumes `multipart/form-data`.

Form field:

- `file`: profile image file

Response body: `UserResponse`

## Project endpoints

#### `GET /api/projects`

Returns all projects.

#### `GET /api/projects/{id}`

Returns a single project.

#### `POST /api/projects`

Request body:

```json
{
  "name": "Project name",
  "description": "Optional description",
  "type": "INDIVIDUAL|GROUP",
  "dueDate": "YYYY-MM-DD",
  "ownerId": 1
}
```

Response body: `ProjectResponse`

#### `PUT /api/projects/{id}`

Request body:

```json
{
  "name": "Project name",
  "description": "Optional description",
  "type": "INDIVIDUAL|GROUP",
  "status": "PENDING|FINISHED",
  "dueDate": "YYYY-MM-DD"
}
```

Response body: `ProjectResponse`

#### `PATCH /api/projects/{id}/finish`

Marks the project as finished.

Response body: `ProjectResponse`

#### `GET /api/projects/{id}/members`

Returns the members of a project.

#### `POST /api/projects/{id}/members`

Request body:

```json
{
  "userId": 2,
  "role": "OWNER|MEMBER"
}
```

Response body: `ProjectMemberResponse`

## Deliverable endpoints

#### `GET /api/projects/{projectId}/deliverables`

Returns deliverables for the specified project.

#### `POST /api/projects/{projectId}/deliverables`

Request body:

```json
{
  "title": "Deliverable title",
  "description": "Optional description",
  "dueDate": "YYYY-MM-DD"
}
```

Response body: `DeliverableResponse`

#### `PUT /api/deliverables/{id}`

Request body:

```json
{
  "title": "Deliverable title",
  "description": "Optional description",
  "status": "PENDING|FINISHED",
  "dueDate": "YYYY-MM-DD"
}
```

Response body: `DeliverableResponse`

#### `PATCH /api/deliverables/{id}/finish`

Marks the deliverable as finished.

Response body: `DeliverableResponse`

## Task endpoints

#### `GET /api/deliverables/{deliverableId}/tasks`

Returns task items for a deliverable.

#### `POST /api/deliverables/{deliverableId}/tasks`

Request body:

```json
{
  "title": "Task title",
  "dueDate": "YYYY-MM-DD"
}
```

Response body: `TaskItemResponse`

#### `PUT /api/tasks/{id}`

Request body:

```json
{
  "title": "Task title",
  "status": "PENDING|FINISHED",
  "dueDate": "YYYY-MM-DD"
}
```

Response body: `TaskItemResponse`

#### `PATCH /api/tasks/{id}/finish`

Marks the task item as finished.

Response body: `TaskItemResponse`

## Response object schemas

### `UserResponse`

```json
{
  "id": 1,
  "displayName": "string",
  "email": "user@example.com",
  "profilePictureUrl": "string or null"
}
```

### `ProjectResponse`

```json
{
  "id": 1,
  "name": "string",
  "description": "string",
  "type": "INDIVIDUAL|GROUP",
  "status": "PENDING|FINISHED",
  "dueDate": "YYYY-MM-DD",
  "pastDue": false,
  "owner": { ...UserResponse }
}
```

### `DeliverableResponse`

```json
{
  "id": 1,
  "projectId": 1,
  "title": "string",
  "description": "string",
  "status": "PENDING|FINISHED",
  "dueDate": "YYYY-MM-DD",
  "pastDue": false
}
```

### `TaskItemResponse`

```json
{
  "id": 1,
  "deliverableId": 1,
  "title": "string",
  "status": "PENDING|FINISHED",
  "dueDate": "YYYY-MM-DD or null",
  "pastDue": false
}
```

### `ProjectMemberResponse`

```json
{
  "id": 1,
  "user": { ...UserResponse },
  "role": "OWNER|MEMBER"
}
```

## Enums

- `ProjectType`: `INDIVIDUAL`, `GROUP`
- `WorkStatus`: `PENDING`, `FINISHED`
- `ProjectRole`: `OWNER`, `MEMBER`

## Configuration

### Default development profile

The default config uses H2 in-memory database.

`src/main/resources/application.properties` sets:

- `spring.datasource.url=jdbc:h2:mem:taskmanager`
- `spring.datasource.username=sa`
- `spring.datasource.password=`
- `spring.jpa.hibernate.ddl-auto=update`
- `spring.h2.console.enabled=true`
- `app.security.jwt.secret=change-this-development-secret-to-a-long-random-production-secret`
- `app.frontend.password-reset-url=taskmanager://reset-password`
- `app.profile-pictures.upload-dir=uploads/profile-pictures`

### PostgreSQL profile

`src/main/resources/application-postgres.properties` supports environment overrides:

- `DB_URL` (default `jdbc:postgresql://localhost:5432/task_manager`)
- `DB_USERNAME` (default `task_manager`)
- `DB_PASSWORD` (default `task_manager_password`)
- `JWT_SECRET`
- `PASSWORD_RESET_URL`
- `PROFILE_PICTURE_UPLOAD_DIR`
- mail settings: `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`

### Docker Compose

The repository includes `docker-compose.yml` for PostgreSQL:

- database: `task_manager`
- username: `task_manager`
- password: `task_manager_password`

## Run locally

Default H2 mode:

```bash
mvn spring-boot:run
```

PostgreSQL mode:

```bash
docker compose up -d postgres
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

Override settings via environment:

```bash
DB_URL=jdbc:postgresql://localhost:5432/task_manager \
DB_USERNAME=task_manager \
DB_PASSWORD=task_manager_password \
JWT_SECRET=replace-this-with-a-long-random-secret \
MAIL_HOST=smtp.example.com \
MAIL_USERNAME=user \
MAIL_PASSWORD=secret \
PROFILE_PICTURE_UPLOAD_DIR=uploads/profile-pictures \
  mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

## Notes

- The application enforces validation on required fields and dates.
- `pastDue` is true when an unfinished item has a due date before the current date.
- Reset links are logged locally when SMTP is not configured.
- Profile picture upload is available via `PATCH /api/users/me/profile-picture`.
