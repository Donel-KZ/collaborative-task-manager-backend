# Task Manager API

Spring Boot backend for a task manager/to-do app that supports individual projects, group projects, deliverables, and smaller task items.

## Domain

- A `UserAccount` represents someone who owns or joins projects.
- A `Project` can be `INDIVIDUAL` or `GROUP`.
- A group project can have members with `OWNER` or `MEMBER` roles.
- A `Deliverable` belongs to a project and represents a larger piece of work the user creates.
- A `TaskItem` belongs to a deliverable and breaks it into smaller work.
- Projects, deliverables, and tasks use `PENDING` or `FINISHED`.
- `pastDue` is returned by the API when an unfinished item has a due date before today.

## Main endpoints

```text
POST   /api/auth/register
POST   /api/auth/login

GET    /api/users

POST   /api/projects
GET    /api/projects
GET    /api/projects/{id}
PUT    /api/projects/{id}
PATCH  /api/projects/{id}/finish

GET    /api/projects/{id}/members
POST   /api/projects/{id}/members

GET    /api/projects/{projectId}/deliverables
POST   /api/projects/{projectId}/deliverables
PUT    /api/deliverables/{id}
PATCH  /api/deliverables/{id}/finish

GET    /api/deliverables/{deliverableId}/tasks
POST   /api/deliverables/{deliverableId}/tasks
PUT    /api/tasks/{id}
PATCH  /api/tasks/{id}/finish
```

## Example flow

Register a user:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "displayName": "Donel",
    "email": "donel@example.com",
    "password": "password123"
  }'
```

Log in:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"donel@example.com","password":"password123"}'
```

Both endpoints return an `accessToken`. Send it to protected endpoints with:

```text
Authorization: Bearer YOUR_ACCESS_TOKEN
```

Create a group project:

```bash
curl -X POST http://localhost:8080/api/projects \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "name": "Mobile App Launch",
    "description": "Plan and deliver the first version",
    "type": "GROUP",
    "dueDate": "2026-09-30",
    "ownerId": 1
  }'
```

Create a deliverable:

```bash
curl -X POST http://localhost:8080/api/projects/1/deliverables \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "title": "Authentication module",
    "description": "Login, registration, password reset",
    "dueDate": "2026-08-15"
  }'
```

Break that deliverable into tasks:

```bash
curl -X POST http://localhost:8080/api/deliverables/1/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{"title":"Create login endpoint","dueDate":"2026-07-01"}'
```

Finish work:

```bash
curl -X PATCH http://localhost:8080/api/deliverables/1/finish \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## Running

This project uses Maven. If Maven is installed:

```bash
mvn spring-boot:run
```

By default, the app uses an in-memory H2 database for quick development:

```text
JDBC URL: jdbc:h2:mem:taskmanager
User: sa
Password:
Console: http://localhost:8080/h2-console
```

## Running with PostgreSQL

Start PostgreSQL with Docker:

```bash
docker compose up -d postgres
```

Then run the app with the `postgres` profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

The default local PostgreSQL settings are:

```text
Database: task_manager
Username: task_manager
Password: task_manager_password
URL: jdbc:postgresql://localhost:5432/task_manager
```

You can override them with environment variables:

```bash
DB_URL=jdbc:postgresql://localhost:5432/task_manager \
DB_USERNAME=task_manager \
DB_PASSWORD=task_manager_password \
JWT_SECRET=replace-this-with-a-long-random-secret \
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

## Next backend features to add

- Project invitations instead of directly adding members.
- Assigned users for deliverables and tasks.
- Comments, attachments, and activity history.
- OpenAPI/Swagger documentation for the mobile, web, and desktop clients.
# collaborative-task-manager-backend
# collaborative-task-manager-backend
