import { isEmailValid, getEmailValidationMessage } from "@/utils/email";

describe("isEmailValid", () => {
  it("deve retornar true para email válido", () => {
    expect(isEmailValid("usuario@exemplo.com")).toBe(true);
  });

  it("deve retornar true para email com subdomínio", () => {
    expect(isEmailValid("user@mail.example.org")).toBe(true);
  });

  it("deve retornar false para email sem @", () => {
    expect(isEmailValid("usuarioexemplo.com")).toBe(false);
  });

  it("deve retornar false para email sem domínio", () => {
    expect(isEmailValid("usuario@")).toBe(false);
  });

  it("deve retornar false para string vazia", () => {
    expect(isEmailValid("")).toBe(false);
  });

  it("deve retornar false para email apenas com espaços", () => {
    expect(isEmailValid("   ")).toBe(false);
  });
});

describe("getEmailValidationMessage", () => {
  it("deve retornar mensagem de obrigatório para email vazio", () => {
    expect(getEmailValidationMessage("")).toBe("Email é obrigatório");
  });

  it("deve retornar mensagem de inválido para email sem @", () => {
    expect(getEmailValidationMessage("emailsemarroba.com")).toBe(
      "Email inválido"
    );
  });

  it("deve retornar string vazia para email válido", () => {
    expect(getEmailValidationMessage("valido@email.com")).toBe("");
  });
});
