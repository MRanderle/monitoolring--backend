# Justificativa Técnica do Padrão de Rotas

Este documento registra o **motivo** das decisões tomadas em
[`api-nomenclatura-recursos.md`](./api-nomenclatura-recursos.md) e
[`api-convencoes-http.md`](./api-convencoes-http.md), não apenas o "como".

## 1. Por que REST orientado a recursos, e não RPC/verbos na URL

A stack do projeto (`spring-boot-starter-webmvc`) é construída em torno do
modelo de controllers mapeando rotas para recursos, o que casa
naturalmente com REST orientado a substantivos. Alternativas baseadas em
RPC (`POST /confirmarRetirada`, `POST /getFerramentas`) foram descartadas
porque:

- **Previsibilidade para o consumidor.** Um frontend (ou qualquer cliente
  da API) consegue inferir a rota de uma nova funcionalidade a partir do
  padrão existente, sem precisar de um dicionário de nomes de ação por
  endpoint.
- **Aderência a boas práticas de mercado.** O modelo adotado segue o
  espírito de guias amplamente usados na indústria — *Microsoft REST API
  Guidelines*, *Google API Design Guide* e *Zalando RESTful API
  Guidelines* — adaptado ao vocabulário do domínio do projeto.
- **Menor acoplamento entre rota e implementação.** Rotas RPC tendem a
  vazar detalhes de implementação/fluxo na própria URL; rotas orientadas a
  recurso descrevem *o que* existe no sistema, não *como* uma tela
  específica opera sobre ele.

## 2. Por que nomes de resource em português

O `docs/prd.md` já fixa um vocabulário de domínio preciso e não-ambíguo
(Membro, Ferramenta, Empréstimo, Retirada, Devolução, Checklist de
Conservação, Bloqueio por Inadimplência, Perda). Traduzir esses termos
para inglês na API introduziria um segundo dicionário
(`Ferramenta`→`Tool`, `Empréstimo`→`Loan`, `Retirada`→`Checkout`,
`Devolução`→`Return`) que precisaria ser mantido em sincronia com o PRD
para sempre, sem ganho real:

- **Reduz ambiguidade de tradução.** Termos como "Retirada" e "Devolução"
  não têm uma tradução única e óbvia para inglês no contexto de
  empréstimo de ferramentas (`checkout`/`pickup`/`withdrawal` são todos
  candidatos plausíveis) — manter o português elimina essa escolha
  arbitrária.
- **Facilita comunicação entre código e stakeholders não técnicos.** Como
  este é um projeto com rigor de produto real, mas de domínio e equipe
  majoritariamente falantes de português, alinhar rota e requisito (FR-9
  fala em "Retirada", a rota é sobre "Retirada") reduz o custo de
  revisar/validar comportamento contra o PRD.
- **Custo de manter em inglês seria maior que o benefício.** Não há
  consumidores externos da API fora do próprio ecossistema do projeto que
  justifiquem inglês como padrão de interoperabilidade.

## 3. Por que sub-resources de ação em vez de verbo ou flag de status

A alternativa mais simples seria expor a transição de estado de um
Empréstimo como `PATCH /emprestimos/{id}` com um campo `status` no corpo.
Essa abordagem foi descartada em favor de sub-resources de ação
(`POST /emprestimos/{id}/devolucao`, `POST /emprestimos/{id}/perda`)
porque:

- **RNF de integridade e auditoria (PRD §5) exige imutabilidade.** Todo
  registro de Retirada, Devolução, Perda e Checklist deve ser imutável uma
  vez confirmado; correções exigem um novo registro, nunca edição
  silenciosa. Um `PATCH` de status sugere que o registro anterior foi
  editado; um `POST` de sub-resource deixa explícito que um novo evento,
  com seu próprio timestamp e responsável, foi criado.
- **Rastreabilidade por rota.** Com sub-resources de ação, é possível
  auditar por rota qual operação de negócio ocorreu (quem chamou
  `POST /emprestimos/108/perda` e quando) sem precisar inspecionar o corpo
  da requisição para descobrir qual transição foi solicitada.
- **Alinhamento com a regra crítica FR-12.** A tentativa de Retirada
  precisa ser rejeitada de forma explícita (com `409` e motivo) quando o
  Membro está bloqueado — um endpoint de ação dedicado deixa esse ponto de
  decisão isolado e fácil de testar, em vez de misturado na lógica genérica
  de um `PATCH` de status.

## 4. Por que não há `DELETE` físico

A mesma RNF de integridade e auditoria (§5) exige que o histórico de
Ferramentas, Membros, Empréstimos e Checklists seja preservado
indefinidamente. Por isso nenhuma rota `DELETE` é definida sobre esses
recursos — a "remoção" de uma Ferramenta é sempre uma mudança de estado
(`inativa`), reversível e auditável, nunca uma exclusão física de
registro.

## 5. Benefícios de consistência, legibilidade e escalabilidade

- **Consistência:** todo novo endpoint segue as mesmas três regras
  (substantivo plural em português, verbo HTTP mapeado por semântica,
  sub-resource de ação para transições de estado), reduzindo decisões
  ad-hoc a cada nova funcionalidade.
- **Legibilidade:** a URL descreve o recurso e, quando aplicável, o evento
  de negócio (`emprestimos/{id}/devolucao`), sem exigir consulta a
  documentação externa para entender o que uma rota faz.
- **Escalabilidade do padrão:** funcionalidades hoje fora do escopo do MVP
  mas já mapeadas no PRD (§7.1) — Treinamento Prévio (N2) e Multas
  automatizadas (N3) — encaixam-se no mesmo padrão sem exigir uma nova
  convenção: por exemplo, `POST /emprestimos/{id}/treinamento` ou
  `GET /emprestimos/{id}/multa` seguiriam a mesma lógica de sub-resource já
  estabelecida aqui.
