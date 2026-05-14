<div align="center">

# 💰 mEconomy

### Economia moderna, leve e configurável para servidores Minecraft

**mEconomy** é um plugin de economia desenvolvido para servidores que precisam de uma solução simples de instalar, bonita para os jogadores, eficiente para a equipe e compatível com o ecossistema clássico do Spigot por meio do Vault.

![Java](https://img.shields.io/badge/Java-8%2B-ed8b00?style=for-the-badge&logo=java&logoColor=white)
![Spigot](https://img.shields.io/badge/Spigot-1.8.8%2B-orange?style=for-the-badge&logo=minecraft&logoColor=white)
![Vault](https://img.shields.io/badge/Vault-Compatible-blue?style=for-the-badge)
![Storage](https://img.shields.io/badge/Storage-YAML%20%7C%20MySQL-2ea44f?style=for-the-badge)
![Build](https://img.shields.io/badge/Build-Maven-red?style=for-the-badge&logo=apachemaven)

[![Baixar Plugin](https://img.shields.io/badge/BAIXAR%20PLUGIN-mEconomy--jar-2ea44f?style=for-the-badge&logo=github)](https://github.com/MarcosFG-Dev/mEconomy/actions/workflows/maven-build.yml)
[![Ver Código](https://img.shields.io/badge/VER%20CÓDIGO-GitHub-181717?style=for-the-badge&logo=github)](https://github.com/MarcosFG-Dev/mEconomy)

</div>

---

## 🚀 Por que usar o mEconomy?

Muitos plugins de economia são antigos, pesados, confusos de configurar ou dependem de sistemas desnecessariamente complexos. O **mEconomy** foi criado com foco em uma experiência direta: instalar, configurar e usar.

Ele entrega o que um servidor precisa no dia a dia: saldo, pagamento entre jogadores, comandos administrativos, ranking dos mais ricos, integração com Vault e armazenamento flexível em YAML ou MySQL.

### Ideal para

- Servidores **Survival**, **RankUP**, **SkyBlock**, **Factions**, **PvP**, **Semi-Anárquico** e redes personalizadas.
- Donos de servidor que querem uma economia limpa, objetiva e fácil de manter.
- Desenvolvedores que precisam de compatibilidade com plugins que usam **Vault**.
- Equipes que querem um sistema de economia pronto para produção sem complicação.

---

## ✨ Destaques competitivos

| Recurso | Benefício |
|---|---|
| **Integração com Vault** | Compatível com lojas, jobs, ranks, recompensas e plugins que usam economia via Vault. |
| **YAML e MySQL** | Use arquivos locais para servidores pequenos ou MySQL para ambientes maiores. |
| **Menu GUI profissional** | Interface visual para jogadores acessarem saldo, ranking, ajuda e pagamento guiado. |
| **Ranking de magnatas** | Liste os jogadores mais ricos com `/magnata` e limite configurável. |
| **Mensagens configuráveis** | Personalize textos, prefixos, cores e formatos no `config.yml`. |
| **Formatação monetária** | Valores grandes ficam legíveis com notação compacta, como `1.5k`, `2M` e `5B`. |
| **Comandos administrativos** | Controle total sobre saldos com `set`, `give`, `take` e `reload`. |
| **Java 8+** | Compatível com ambientes clássicos e servidores legados. |

---

## 🖥️ Menu GUI

O mEconomy conta com uma interface visual pensada para deixar a experiência mais fluida para o jogador.

### Ações disponíveis no menu

- Ver saldo atual
- Abrir ranking de magnatas
- Enviar dinheiro com fluxo guiado pelo chat
- Visualizar ajuda rápida
- Recarregar configuração, para administradores
- Fechar menu com segurança

### Comandos para abrir

```text
/money
/money menu
/money gui
/money painel
```

O pagamento pelo menu segue um fluxo simples:

```text
Abrir menu → Enviar Dinheiro → Digitar jogador → Digitar quantia → Confirmação automática
```

---

## 🔥 Funcionalidades

### Jogadores

- Consultar saldo próprio
- Consultar saldo de outro jogador
- Enviar dinheiro para outros jogadores
- Usar menu GUI de economia
- Ver ranking dos jogadores mais ricos
- Receber notificações de pagamento

### Administração

- Definir saldo de jogadores
- Adicionar dinheiro a uma conta
- Remover dinheiro de uma conta
- Recarregar configurações
- Controlar permissões
- Personalizar mensagens e formatos

### Armazenamento

- **YAML** para instalação rápida e servidores menores
- **MySQL** para servidores com maior volume ou redes que precisam centralizar dados

---

## 📦 Download e instalação

### Download

Clique no botão abaixo e baixe o artifact gerado pelo GitHub Actions:

[![Baixar mEconomy](https://img.shields.io/badge/BAIXAR%20PLUGIN-mEconomy--jar-2ea44f?style=for-the-badge&logo=github)](https://github.com/MarcosFG-Dev/mEconomy/actions/workflows/maven-build.yml)

> Abra a execução mais recente do workflow **Maven Build** e baixe o artifact **mEconomy-jar**.

### Instalação

1. Baixe o `.jar` do plugin.
2. Coloque o arquivo na pasta `plugins/` do servidor.
3. Instale o **Vault**.
4. Reinicie o servidor.
5. Configure o `config.yml` gerado.
6. Use `/money` para abrir o menu.

---

## ✅ Requisitos

| Requisito | Versão |
|---|---|
| Java | 8 ou superior |
| Servidor | Spigot 1.8.8+ |
| Dependência | Vault |
| Build | Maven |

---

## 📜 Comandos

### Jogadores

| Comando | Alias | Descrição | Permissão |
|---|---|---|---|
| `/money` | `/bal`, `/balance` | Abre o menu principal de economia. | `meconomy.use` |
| `/money saldo` | `/money balance` | Mostra seu saldo no chat. | `meconomy.use` |
| `/money <jogador>` | - | Consulta o saldo de outro jogador. | `meconomy.use` |
| `/money pay <jogador> <quantia>` | `/money pagar` | Envia dinheiro para outro jogador. | `meconomy.use` |
| `/money menu` | `/money gui`, `/money painel` | Abre o menu GUI. | `meconomy.use` |
| `/money help` | `/money ajuda` | Exibe ajuda dos comandos. | `meconomy.use` |
| `/magnata` | `/balancetop`, `/baltop` | Exibe o ranking dos mais ricos. | Configurável |

### Administração

| Comando | Descrição | Permissão |
|---|---|---|
| `/money set <jogador> <quantia>` | Define o saldo exato de um jogador. | `meconomy.admin` |
| `/money give <jogador> <quantia>` | Adiciona dinheiro ao saldo de um jogador. | `meconomy.admin` |
| `/money take <jogador> <quantia>` | Remove dinheiro do saldo de um jogador. | `meconomy.admin` |
| `/money reload` | Recarrega a configuração do plugin. | `meconomy.admin` |

---

## 🔐 Permissões

| Permissão | Descrição |
|---|---|
| `meconomy.use` | Permite usar os comandos principais de economia. |
| `meconomy.admin` | Permite usar comandos administrativos. |
| Permissão do `/magnata` | Pode ser configurada em `top-balance.permission`. |

---

## ⚙️ Configuração

O plugin foi pensado para ser simples de configurar, mas flexível o suficiente para servidores em produção.

<details>
<summary>Ver exemplo de config.yml</summary>

```yaml
# Configuração mEconomy
# Autor: MarcosFG

storage:
  type: "YAML" # Use MYSQL para banco de dados
  mysql:
    host: "localhost"
    port: 3306
    user: "root"
    password: ""
    database: "mecon"
    table: "users_money"

settings:
  currency-symbol: "RT$"
  currency-singular: "Real"
  currency-plural: "Reais"
  start-balance: 1000.0
  format:
    use-k-notation: true
    decimals: 2

messages:
  prefix: "&2[&amEconomy&2] &f"
  no-permission: "&cVocê não tem permissão."
  balance: "&aSeu saldo: &2%symbol% %amount%"
  balance-other: "&aSaldo de &e%player%&a: &2%symbol% %amount%"
  paid: "&aVocê enviou &2%symbol% %amount% &apara &e%player%&a."
  received: "&aVocê recebeu &2%symbol% %amount% &ade &e%player%&a."
  admin-give: "&aVocê deu &2%symbol% %amount% &apara &e%player%&a."
  admin-take: "&aVocê removeu &2%symbol% %amount% &ade &e%player%&a."
  admin-set: "&aNovo saldo de &e%player%&a definido para &2%symbol% %amount%&a."
  insufficient-funds: "&cSaldo insuficiente!"
  player-not-found: "&cJogador não encontrado."
  invalid-args: "&cUse: /money <pay/top/help>"
  invalid-amount: "&cValor inválido."
  magnata-broadcast: "&6&l[MAGNATA] &eO novo magnata é &f%player% &ecom &2%symbol% %amount%&e!"

top-balance:
  update-interval: 300
  limit: 10
  max-limit: 100
  permission: ""
  header: "&2&lTOP %limit% &aMais Ricos:"
  format: "&7%pos%º &f%player% &7- &2%balance%"
  empty: "&cNenhum jogador encontrado no ranking."
  invalid-limit: "&cUse /%label% [quantidade] com um número entre %min% e %max%."
  no-permission: "&cVocê não tem permissão para usar este comando."
```

</details>

---

## 🧩 Integração com Vault

O mEconomy registra automaticamente sua economia no `ServicesManager` do Bukkit quando o Vault está instalado.

Isso permite que outros plugins usem o mEconomy como provedor de economia sem integração customizada.

### Exemplo para desenvolvedores

```java
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

private Economy economy;

private boolean setupEconomy() {
    if (getServer().getPluginManager().getPlugin("Vault") == null) {
        return false;
    }

    RegisteredServiceProvider<Economy> provider = getServer()
            .getServicesManager()
            .getRegistration(Economy.class);

    if (provider == null) {
        return false;
    }

    economy = provider.getProvider();
    return economy != null;
}
```

---

## 🏆 Ranking de magnatas

O comando `/magnata` exibe o ranking dos jogadores mais ricos do servidor.

Recursos do ranking:

- Quantidade padrão configurável
- Limite máximo configurável
- Permissão opcional
- Mensagens customizáveis
- Placeholders para posição, jogador, saldo e limite

Exemplo:

```text
/magnata
/magnata 5
/magnata 20
```

---

## 🛠️ Build local

Para compilar o plugin localmente:

```bash
mvn clean package
```

O arquivo final será gerado em:

```text
target/
```

---

## 📌 Roadmap sugerido

- Página de configuração do menu GUI
- Logs de transações
- Histórico de pagamentos
- Placeholders para PlaceholderAPI
- Sistema de top cacheado por intervalo
- Migração automática entre YAML e MySQL

---

## 🤝 Suporte e contribuição

Encontrou um problema, tem uma sugestão ou quer melhorar o plugin?

Abra uma issue no GitHub ou envie melhorias por pull request.

---

<div align="center">

### mEconomy

**Economia simples para instalar, poderosa para administrar e agradável para jogar.**

Desenvolvido por **MarcosFG**.

</div>
