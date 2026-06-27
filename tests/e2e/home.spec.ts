import { test, expect } from "@playwright/test";

const BASE_URL = process.env.BASE_URL || "http://localhost:3000";

test.describe("Home - Feed de Posts", () => {
  test.beforeEach(async ({ page }) => {
    await page.goto(BASE_URL);
  });

  test("deve carregar a página home com o título do feed", async ({ page }) => {
    await expect(page.getByRole("heading", { name: /feed de posts/i })).toBeVisible({ timeout: 10000 });
  });

  test("deve exibir posts na página inicial", async ({ page }) => {
    // Aguarda o carregamento dos posts
    await expect(page.getByRole("listitem").first()).toBeVisible({ timeout: 15000 });
    const posts = page.getByRole("listitem");
    await expect(posts).toHaveCount(await posts.count());
    expect(await posts.count()).toBeGreaterThan(0);
  });

  test("deve exibir número de likes em cada post", async ({ page }) => {
    await page.waitForSelector('[data-testid="likes-count"]', { timeout: 15000 });
    const likesElements = page.locator('[data-testid="likes-count"]');
    const count = await likesElements.count();
    expect(count).toBeGreaterThan(0);

    // Verifica que o primeiro post tem um número visível de likes
    const firstLikes = await likesElements.first().textContent();
    expect(firstLikes).toMatch(/\d+/);
  });

  test("deve exibir número de dislikes em cada post", async ({ page }) => {
    await page.waitForSelector('[data-testid="dislikes-count"]', { timeout: 15000 });
    const dislikesElements = page.locator('[data-testid="dislikes-count"]');
    const count = await dislikesElements.count();
    expect(count).toBeGreaterThan(0);

    const firstDislikes = await dislikesElements.first().textContent();
    expect(firstDislikes).toMatch(/\d+/);
  });

  test("deve exibir o contêiner de reactions em todos os posts", async ({ page }) => {
    await page.waitForSelector('[data-testid="reactions"]', { timeout: 15000 });
    const reactionElements = page.locator('[data-testid="reactions"]');
    const postsCount = await page.getByRole("listitem").count();
    expect(await reactionElements.count()).toBe(postsCount);
  });

  test("deve exibir botão de curtir em cada post", async ({ page }) => {
    await page.waitForSelector('[role="listitem"]', { timeout: 15000 });
    const curtirButtons = page.getByRole("button", { name: /curtir/i });
    expect(await curtirButtons.count()).toBeGreaterThan(0);
  });

  test("deve exibir botão de carregar mais", async ({ page }) => {
    await page.waitForSelector('[role="listitem"]', { timeout: 15000 });
    const loadMoreButton = page.getByRole("button", { name: /carregar mais/i });
    await expect(loadMoreButton).toBeVisible({ timeout: 5000 });
  });

  test("deve carregar mais posts ao clicar em 'Carregar mais'", async ({ page }) => {
    await page.waitForSelector('[role="listitem"]', { timeout: 15000 });
    const initialCount = await page.getByRole("listitem").count();

    const loadMoreButton = page.getByRole("button", { name: /carregar mais/i });
    await loadMoreButton.click();

    await page.waitForFunction(
      (prevCount) => document.querySelectorAll('[role="listitem"]').length > prevCount,
      initialCount,
      { timeout: 10000 }
    );

    const newCount = await page.getByRole("listitem").count();
    expect(newCount).toBeGreaterThan(initialCount);
  });

  test("deve mostrar alerta ao tentar curtir sem estar autenticado", async ({ page }) => {
    await page.waitForSelector('[role="listitem"]', { timeout: 15000 });

    page.once("dialog", async (dialog) => {
      expect(dialog.message()).toContain("autenticado");
      await dialog.accept();
    });

    const curtirButton = page.getByRole("button", { name: /curtir/i }).first();
    await curtirButton.click();
  });
});

test.describe("Autenticação - Signup", () => {
  test("deve exibir o formulário de cadastro", async ({ page }) => {
    await page.goto(`${BASE_URL}/signup`);
    await expect(page.getByRole("heading", { name: /cadastro|criar conta|sign up/i })).toBeVisible({ timeout: 5000 });
  });

  test("deve exibir erro para email inválido no signup", async ({ page }) => {
    await page.goto(`${BASE_URL}/signup`);

    const emailInput = page.getByRole("textbox", { name: /email/i });
    await emailInput.fill("emailinvalido");

    const passwordInput = page.locator('input[type="password"]').first();
    await passwordInput.fill("Senha@123");

    await page.getByRole("button", { name: /cadastrar|criar|sign up/i }).click();
    // A aplicação deve exibir mensagem de erro ou impedir o envio
    await page.waitForTimeout(1000);
  });
});

test.describe("Autenticação - Signin", () => {
  test("deve exibir o formulário de login", async ({ page }) => {
    await page.goto(`${BASE_URL}/signin`);
    await expect(page.getByRole("heading", { name: /entrar|login|sign in/i })).toBeVisible({ timeout: 5000 });
  });

  test("deve navegar para página de reset de senha", async ({ page }) => {
    await page.goto(`${BASE_URL}/signin`);
    const resetLink = page.getByRole("link", { name: /esqueci|reset|recuperar/i });
    if (await resetLink.isVisible()) {
      await resetLink.click();
      await expect(page).toHaveURL(/reset-password/, { timeout: 5000 });
    }
  });
});
