import { isPasswordValid, getPasswordValidationMessage } from "@/utils/password";

describe("isPasswordValid", () => {
  it("deve retornar true para senha válida com todos os requisitos", () => {
    expect(isPasswordValid("Senha@123!")).toBe(true);
  });

  it("deve retornar false para senha sem letra maiúscula", () => {
    expect(isPasswordValid("senha@123!")).toBe(false);
  });

  it("deve retornar false para senha sem letra minúscula", () => {
    expect(isPasswordValid("SENHA@123!")).toBe(false);
  });

  it("deve retornar false para senha sem número", () => {
    expect(isPasswordValid("Senha@abc!")).toBe(false);
  });

  it("deve retornar false para senha sem caractere especial", () => {
    expect(isPasswordValid("Senha1234")).toBe(false);
  });

  it("deve retornar false para senha com 8 caracteres (precisa ser maior)", () => {
    expect(isPasswordValid("Senh@12")).toBe(false);
  });

  it("deve retornar false para senha vazia", () => {
    expect(isPasswordValid("")).toBe(false);
  });
});

describe("getPasswordValidationMessage", () => {
  it("deve retornar mensagem de obrigatória para senha vazia", () => {
    expect(getPasswordValidationMessage("")).toBe("Senha é obrigatória");
  });

  it("deve retornar string vazia para senha válida", () => {
    expect(getPasswordValidationMessage("SenhaValida@123")).toBe("");
  });

  it("deve listar erros para senha sem maiúscula e sem número", () => {
    const msg = getPasswordValidationMessage("senha@abc!");
    expect(msg).toContain("uma letra maiúscula");
    expect(msg).toContain("um número");
  });
});
