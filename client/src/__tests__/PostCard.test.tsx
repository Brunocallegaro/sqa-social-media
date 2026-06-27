import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import PostCard from "@/components/PostCard";
import { Post } from "@/service/types";

const mockPost: Post = {
  id: 1,
  title: "Post de teste",
  body: "Corpo do post de teste",
  liked: false,
  reactions: {
    likes: 42,
    dislikes: 7,
  },
};

describe("PostCard - Exibição de reactions (likes/dislikes)", () => {
  it("deve exibir o número de likes corretamente", () => {
    render(
      <PostCard
        post={mockPost}
        isAuthenticated={false}
        onLike={jest.fn()}
      />
    );
    expect(screen.getByTestId("likes-count")).toHaveTextContent("42");
  });

  it("deve exibir o número de dislikes corretamente", () => {
    render(
      <PostCard
        post={mockPost}
        isAuthenticated={false}
        onLike={jest.fn()}
      />
    );
    expect(screen.getByTestId("dislikes-count")).toHaveTextContent("7");
  });

  it("deve renderizar o contêiner de reactions", () => {
    render(
      <PostCard
        post={mockPost}
        isAuthenticated={false}
        onLike={jest.fn()}
      />
    );
    expect(screen.getByTestId("reactions")).toBeInTheDocument();
  });

  it("deve exibir 0 likes quando reactions é undefined", () => {
    const postSemReactions: Post = {
      ...mockPost,
      reactions: { likes: 0, dislikes: 0 },
    };
    render(
      <PostCard
        post={postSemReactions}
        isAuthenticated={false}
        onLike={jest.fn()}
      />
    );
    expect(screen.getByTestId("likes-count")).toHaveTextContent("0");
    expect(screen.getByTestId("dislikes-count")).toHaveTextContent("0");
  });
});

describe("PostCard - Comportamento do botão de curtir", () => {
  it("deve exibir 'Curtir' quando post não está curtido", () => {
    render(
      <PostCard
        post={mockPost}
        isAuthenticated={true}
        onLike={jest.fn()}
      />
    );
    expect(screen.getByRole("button")).toHaveTextContent("Curtir");
  });

  it("deve exibir 'Curtido' quando post já está curtido", () => {
    const likedPost = { ...mockPost, liked: true };
    render(
      <PostCard
        post={likedPost}
        isAuthenticated={true}
        onLike={jest.fn()}
      />
    );
    expect(screen.getByRole("button")).toHaveTextContent("Curtido");
  });

  it("deve chamar onLike com o id correto ao clicar", async () => {
    const mockOnLike = jest.fn().mockResolvedValue(undefined);
    render(
      <PostCard
        post={mockPost}
        isAuthenticated={true}
        onLike={mockOnLike}
      />
    );
    fireEvent.click(screen.getByRole("button"));
    await waitFor(() => {
      expect(mockOnLike).toHaveBeenCalledWith(1);
    });
  });

  it("deve mostrar alerta se usuário não autenticado tentar curtir", () => {
    const alertMock = jest.spyOn(window, "alert").mockImplementation(() => {});
    render(
      <PostCard
        post={mockPost}
        isAuthenticated={false}
        onLike={jest.fn()}
      />
    );
    fireEvent.click(screen.getByRole("button"));
    expect(alertMock).toHaveBeenCalledWith(
      "Você precisa estar autenticado para curtir posts!"
    );
    alertMock.mockRestore();
  });

  it("deve reverter o like se a chamada à API falhar", async () => {
    const mockOnLike = jest.fn().mockRejectedValue(new Error("Erro de rede"));
    const alertMock = jest.spyOn(window, "alert").mockImplementation(() => {});

    render(
      <PostCard
        post={mockPost}
        isAuthenticated={true}
        onLike={mockOnLike}
      />
    );

    fireEvent.click(screen.getByRole("button"));

    await waitFor(() => {
      expect(screen.getByRole("button")).toHaveTextContent("Curtir");
    });

    alertMock.mockRestore();
  });
});

describe("PostCard - Renderização geral", () => {
  it("deve renderizar o título do post", () => {
    render(
      <PostCard post={mockPost} isAuthenticated={false} onLike={jest.fn()} />
    );
    expect(screen.getByText("Post de teste")).toBeInTheDocument();
  });

  it("deve renderizar o corpo do post", () => {
    render(
      <PostCard post={mockPost} isAuthenticated={false} onLike={jest.fn()} />
    );
    expect(screen.getByText("Corpo do post de teste")).toBeInTheDocument();
  });
});
