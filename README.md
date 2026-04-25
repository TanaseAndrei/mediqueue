# MediQueue

Aplicație SaaS de programări pentru cabinete medicale private mici din România.

## Structura proiectului

```
mediqueue/
├── backend/    # Spring Boot 3.3 (Java 21)
└── frontend/   # Next.js 15 (TypeScript)
```

## Branches

| Branch | Scop |
|--------|------|
| `main` | Producție |
| `staging` | Testare pre-producție |
| `develop` | Dezvoltare activă |

## Stack

- **Backend:** Java 21, Spring Boot 3.3, PostgreSQL, Flyway, JPA/Hibernate
- **Frontend:** Next.js 15, TypeScript, Tailwind CSS, shadcn/ui
- **Notificări:** Resend (email), SMS.ro (SMS)
