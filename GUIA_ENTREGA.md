# Guia de Entrega - Atividade CI/CD + Feature Likes/Dislikes

## O que foi implementado

### 1. ✅ Estrutura de CI (`.github/workflows/ci.yml`)
Pipeline com 3 jobs paralelos/sequenciais:
- **Job 1** – Testes unitários Java (Maven + H2 in-memory)
- **Job 2** – Testes unitários do client (Jest/React)
- **Job 3** – Testes E2E + API (Playwright + Newman), roda após os dois primeiros

**Trigger:** Dispara em todo PR aberto ou atualizado com destino à `main`.

---

### 2. ✅ Feature: Likes e Dislikes dos Posts

**Backend (`api/`):**
- `PostService.java` — método `extractReactions()` lê `reactions.likes` e `reactions.dislikes` direto da DummyJSON API e inclui no payload de cada post.

**Frontend (`client/`):**
- `types/index.ts` — adicionado `PostReactions` e campo `reactions` na interface `Post`
- `PostCard.tsx` — exibe contadores com `data-testid="likes-count"` e `data-testid="dislikes-count"`

---

### 3. ✅ Testes de Caixa-Branca (Unitários)

**Client (Jest):**
- `__tests__/email.test.ts` — testa `isEmailValid` e `getEmailValidationMessage`
- `__tests__/password.test.ts` — testa `isPasswordValid` e `getPasswordValidationMessage`
- `__tests__/PostCard.test.tsx` — testa rendering do componente, reactions, comportamento do like

**API (JUnit 5 + Mockito + MockMvc):**
- `UserServiceTest.java` — testa `isEmailValid`, `isPasswordValid`, `createUser`, `findByEmail`
- `PostServiceTest.java` — testa `toggleLike` (criar/remover), `getLikedPosts`
- `AuthControllerTest.java` — testa os endpoints `/auth/signup`, `/auth/signin`, `/auth/reset-password`

---

### 4. ✅ Testes de Caixa-Preta (E2E + API)

**E2E Playwright (`tests/e2e/home.spec.ts`):**
- Carregamento da home e feed
- Exibição de likes e dislikes em cada post
- Botão curtir, carregar mais
- Alerta para usuário não autenticado

**API Newman (`tests/api/collection.json`):**
- Signup (válido, duplicado, email inválido, senha fraca)
- Signin (válido, senha errada)
- GET /posts com validação do campo `reactions`
- POST /posts/:id/like
- GET /posts/liked
- Reset password (existente, inexistente)

---

## Como subir para o GitHub

```bash
# 1. Fork o repositório original (via interface do GitHub)
# https://github.com/PepeTonin/sqa-social-media → botão "Fork"

# 2. Clone o seu fork
git clone https://github.com/SEU_USUARIO/sqa-social-media.git
cd sqa-social-media

# 3. Copie todos os arquivos deste ZIP para dentro do projeto clonado
# (substitua os arquivos existentes)

# 4. Configure o application.properties com seus dados de banco
# api/src/main/resources/application.properties

# 5. Commit na main (unificação)
git add .
git commit -m "feat: unificação de testes e configuração CI"
git push origin main

# 6. Crie a branch da feature
git checkout -b feature/likes-dislikes

# Já está tudo pronto! Apenas faça o commit nessa branch
git add .
git commit -m "feat: exibir likes e dislikes dos posts via DummyJSON reactions"
git push origin feature/likes-dislikes

# 7. Abra o Pull Request no GitHub
# feature/likes-dislikes → main (do seu fork)
# A pipeline irá rodar automaticamente!
```

## Estrutura de arquivos novos/modificados

```
sqa-social-media/
├── .github/
│   └── workflows/
│       └── ci.yml                          ← NOVO: Pipeline CI
├── api/
│   ├── pom.xml                             ← MODIFICADO: +actuator
│   └── src/
│       ├── main/java/.../service/
│       │   └── PostService.java            ← MODIFICADO: +reactions
│       └── test/java/.../
│           ├── controller/
│           │   └── AuthControllerTest.java ← NOVO
│           └── service/
│               ├── UserServiceTest.java    ← NOVO
│               └── PostServiceTest.java    ← NOVO
├── client/
│   ├── next.config.ts                      ← MODIFICADO: +env
│   └── src/
│       ├── __tests__/
│       │   ├── email.test.ts               ← NOVO
│       │   ├── password.test.ts            ← NOVO
│       │   └── PostCard.test.tsx           ← NOVO
│       ├── components/
│       │   └── PostCard.tsx                ← MODIFICADO: +reactions UI
│       └── service/
│           ├── api.ts                      ← MODIFICADO: +NEXT_PUBLIC_API_URL
│           └── types/index.ts              ← MODIFICADO: +PostReactions
└── tests/                                  ← NOVO: pasta de testes E2E
    ├── package.json
    ├── playwright.config.ts
    ├── e2e/
    │   └── home.spec.ts
    └── api/
        └── collection.json
```
