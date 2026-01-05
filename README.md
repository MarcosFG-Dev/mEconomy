# 💰 mEconomy - Sistema de Economia Avançado

![Java](https://img.shields.io/badge/Java-8%2B-ed8b00?style=for-the-badge&logo=java&logoColor=white)
![Spigot](https://img.shields.io/badge/Spigot-1.8.8%2B-orange?style=for-the-badge&logo=minecraft&logoColor=white)
![Vault](https://img.shields.io/badge/Vault-Ready-blue?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

O **mEconomy** é um plugin de economia robusto, eficiente e altamente configurável para servidores de Minecraft. Projetado para suportar desde pequenos servidores survival até grandes redes com milhares de jogadores, oferecendo performance de ponta e integração total com o ecossistema Spigot via Vault.

## 🚀 Funcionalidades Principais

*   **⚡ Alta Performance**: Otimizado para evitar lag, com sistemas de cache inteligente e operações assíncronas.
*   **💾 Armazenamento Híbrido**:
    *   **YAML**: Simples e eficaz para servidores locais e testes.
    *   **MySQL**: Robusto para redes e servidores em produção, permitindo sincronização de dados.
*   **🔗 Integração Vault**: Drop-in replacement para qualquer plugin de economia. Se um plugin suporta Vault, ele suporta mEconomy.
*   **💲 Formatação Monetária**: Suporte nativo a sufixos simplificados (Ex: `1.5k`, `10M`, `5B`) para facilitar a leitura.
*   **🏆 Sistema de Magnata**: Comando `/magnata` integrado para listar os jogadores mais ricos do servidor.
*   **🛠️ Totalmente Configurável**: Mensagens, formatos, intervalos de atualização e símbolos monetários personalizáveis via `config.yml`.

---

## 📜 Comandos e Permissões

### 👤 Comandos de Jogador

| Comando | Alias | Descrição | Permissão |
| :--- | :--- | :--- | :--- |
| `/money` | `/bal`, `/balance` | Visualiza seu saldo atual. | `meconomy.use` |
| `/money pay <player> <quantia>` | - | Envia dinheiro para outro jogador. | `meconomy.use` |
| `/money help` | - | Exibe a lista de ajuda. | `meconomy.use` |
| `/magnata` | `/balancetop`, `/baltop` | Exibe o ranking dos jogadores mais ricos. | `meconomy.use` |

### 🛡️ Comandos Administrativos

| Comando | Descrição | Permissão |
| :--- | :--- | :--- |
| `/money set <player> <quantia>` | Define o saldo exato de um jogador. | `meconomy.admin` |
| `/money give <player> <quantia>` | Adiciona dinheiro ao saldo de um jogador. | `meconomy.admin` |
| `/money take <player> <quantia>` | Remove dinheiro do saldo de um jogador. | `meconomy.admin` |
| `/money reload` | Recarrega as configurações do plugin. | `meconomy.admin` |

---

## ⚙️ Configuração Padrão (`config.yml`)

<details>
<summary>Clique para ver o arquivo config.yml</summary>

```yaml
# Configuração mEconomy
# Autor: MarcosFG

# Tipo de armazenamento: YAML ou MYSQL
storage:
  type: "YAML" # Altere para MYSQL para usar banco de dados
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
    use-k-notation: true # Ex: 1.5k, 2M, 5B
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
  update-interval: 300 # Segundos (Ainda não implementado o auto-update, apenas comando)
  limit: 10
  format: "&7%pos%º &f%player% &7- &2%balance%"
```
</details>

---

## 💻 API para Desenvolvedores

O mEconomy registra-se automaticamente no **ServicesManager** do Bukkit. Para manipular a economia em seu plugin, basta utilizar a API padrão do Vault.

### Exemplo de uso:

```java
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class SeuPlugin extends JavaPlugin {

    private Economy econ = null;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe("Vault não encontrado! Desativando plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    public void darDinheiro(Player p, double quantia) {
        econ.depositPlayer(p, quantia);
    }
}
```

---

## 📥 Instalação

1.  Baixe a versão mais recente na aba **Releases**.
2.  Arraste o arquivo `.jar` para a pasta `plugins/` do seu servidor.
3.  Instale o **Vault** (Obrigatório).
4.  Reinicie o servidor.
5.  Configure o `config.yml` conforme sua necessidade.

---

**Desenvolvido com ❤️ por MarcosFG**
