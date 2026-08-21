# Padrão de Nomenclatura de Resources da API

Este documento define como os *resources* (recursos) expostos pela API REST
do Monitoolring devem ser nomeados. Aplica-se a toda rota criada no
projeto, presente ou futura.

## 1. Regras gerais

1. **Substantivos, nunca verbos.** O nome do resource representa uma
   entidade ou coleção do domínio (`ferramentas`, `emprestimos`), nunca uma
   ação (`criarFerramenta`, `retirarFerramenta`). Ações que não são CRUD
   puro são tratadas como sub-resources — ver
   [`api-convencoes-http.md`](./api-convencoes-http.md).
2. **Plural para coleções.** `GET /ferramentas` retorna a coleção;
   `GET /ferramentas/{id}` retorna um item dela. Não se usa singular para
   coleções (`ferramenta`) nem se mistura singular/plural entre rotas
   irmãs.
3. **Minúsculas.** Nenhum resource usa camelCase, PascalCase ou
   `snake_case` no caminho da URL.
4. **kebab-case para nomes compostos.** Quando o nome do recurso tem mais
   de uma palavra, as palavras são separadas por hífen:
   `prazo-devolucao`, não `prazoDevolucao` nem `prazo_devolucao`.
5. **Português, com o vocabulário do domínio já fixado no PRD.** Os nomes
   de resource seguem os termos usados em `docs/prd.md`
   (Ferramenta, Membro, Empréstimo, Checklist, etc.) — não se traduz o
   domínio para inglês. Ver justificativa completa em
   [`api-justificativa-tecnica.md`](./api-justificativa-tecnica.md).
6. **Sem acentuação ou cedilha nas URLs.** Caracteres especiais do
   português são removidos por segurança/compatibilidade de URL:
   - `ç` → `c` (ex.: `configuracoes`, não `configurações`)
   - `ã`, `á`, `â` → `a` (ex.: `emprestimos`, não `empréstimos`)
   - `é`, `ê` → `e`
   Isso se aplica apenas ao *caminho da URL*; nos corpos JSON de
   requisição/resposta (nomes de campos, mensagens de erro), a acentuação
   correta é preservada normalmente.

## 2. Tabela de tradução: termo de negócio → resource

| Entidade (PRD) | Resource (URL) | Observações |
|---|---|---|
| Membro | `membros` | Cadastro self-service (FR-1) |
| Responsável da Oficina / Staff | `staff` | Termo já usado no próprio PRD; só é criado por outro Staff (FR-2) |
| Ferramenta | `ferramentas` | Possui 5 estados: disponível, emprestada, em reparo/inspeção, perdida, inativa |
| Empréstimo | `emprestimos` | Liga um Membro a uma Ferramenta; criado no ato da Retirada |
| Checklist de Conservação | `checklists` | Sempre um sub-resource de `emprestimos`; nunca existe isolado |
| Devolução | *(sub-resource de ação em `emprestimos`)* | Não é uma coleção própria — ver seção 3 |
| Perda | *(sub-resource de ação em `emprestimos`)* | Não é uma coleção própria — ver seção 3 |
| Prazo padrão de devolução | `configuracoes` | Resource de configuração, com um recurso singleton `prazo-devolucao` |
| Bloqueio por Inadimplência | *(não é resource próprio)* | Campo derivado, exposto dentro de `membros/{id}` — ver seção 4 |

## 3. Sub-resources: quando aninhar

Um resource é aninhado sob outro (`/pai/{id}/filho`) quando a relação é de
posse forte e o filho só existe no contexto do pai:

- `emprestimos/{id}/checklists` — os checklists de retirada e devolução só
  fazem sentido dentro de um Empréstimo específico.
- `emprestimos/{id}/devolucao` e `emprestimos/{id}/perda` — eventos que só
  ocorrem no contexto de um Empréstimo específico (ver
  `api-convencoes-http.md` para o detalhamento desses sub-resources de
  ação).

Um resource permanece no nível raiz, filtrado por *query string*, quando a
listagem também precisa ser consultada de forma independente do pai:

- `GET /emprestimos?membroId={id}` — Staff consultando o histórico de um
  Membro específico (FR-6), mas também precisa listar
  `GET /emprestimos?status=em-atraso` sem filtrar por Membro.
- Alternativa equivalente e igualmente válida: `GET /membros/{id}/emprestimos`
  para o caso específico de um Membro consultando o próprio histórico
  (FR-7) — usada quando o contexto é sempre "meus empréstimos" e não há
  necessidade de outros filtros cruzados.

Regra prática: se a rota mais natural é "todos os X de um Y específico" e
não existe caso de uso para "todos os X do sistema, filtrando por Y entre
outros critérios", use aninhamento. Caso contrário, use recurso raiz +
filtro.

## 4. Estado derivado não é um resource

Nem todo conceito do domínio vira uma rota própria. O **Bloqueio por
Inadimplência** é um estado computado em tempo real (FR-17/FR-18), não uma
entidade armazenada — por isso não existe `GET /bloqueios`. Ele é exposto
como campo dentro da representação de `membros/{id}`:

```json
GET /membros/42

{
  "id": 42,
  "nome": "Ana Souza",
  "bloqueado": true,
  "emprestimosEmAtraso": [
    { "emprestimoId": 108, "ferramentaId": 7, "atrasoDesde": "2026-08-10" }
  ]
}
```

## 5. Exemplos de aplicação (visão geral)

| Recurso | Rota raiz | Sub-resources |
|---|---|---|
| Membro | `/membros` | `/membros/{id}/emprestimos` |
| Staff | `/staff` | — |
| Ferramenta | `/ferramentas` | — |
| Empréstimo | `/emprestimos` | `/emprestimos/{id}/checklists`, `/emprestimos/{id}/devolucao`, `/emprestimos/{id}/perda` |
| Configuração | `/configuracoes/prazo-devolucao` | — (recurso singleton) |
| Autenticação | `/auth/registro`, `/auth/login`, `/auth/logout` | Não segue o padrão substantivo-coleção pois representa ações de sessão, não um resource armazenável — exceção documentada e justificada em [`api-justificativa-tecnica.md`](./api-justificativa-tecnica.md) |

O detalhamento de método HTTP + rota + comportamento para cada uma dessas
entradas está em [`api-convencoes-http.md`](./api-convencoes-http.md).
