package com.marcosfg.meconomy.gui;

import com.marcosfg.meconomy.Main;
import com.marcosfg.meconomy.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MoneyMenu implements Listener {

    private static final String MENU_TITLE = ColorUtils.color("&2&lmEconomy &8| &fMenu Principal");
    private static final String PAY_TITLE = ColorUtils.color("&2&lmEconomy &8| &fEnviar Dinheiro");
    private static final String TOP_TITLE = ColorUtils.color("&2&lmEconomy &8| &fTop Magnatas");
    private static final int MENU_SIZE = 45;

    private final Main plugin;
    private final Map<UUID, PayStep> paySteps = new HashMap<>();
    private final Map<UUID, String> payTargets = new HashMap<>();

    public MoneyMenu(Main plugin) {
        this.plugin = plugin;
    }

    public void openMain(Player player) {
        Inventory inventory = Bukkit.createInventory(null, MENU_SIZE, MENU_TITLE);

        fill(inventory);
        inventory.setItem(4, item(Material.EMERALD, "&a&lSua Carteira", lore(
                "&7Veja suas informações financeiras.",
                "",
                "&fSaldo atual: &2" + currencySymbol() + " " + format(plugin.getEconomyManager().getBalance(player)),
                "",
                "&eClique para atualizar"
        )));
        inventory.setItem(20, item(Material.DOUBLE_PLANT, "&e&lEnviar Dinheiro", lore(
                "&7Envie dinheiro para outro jogador",
                "&7com um fluxo guiado e seguro.",
                "",
                "&aClique para iniciar"
        )));
        inventory.setItem(22, item(Material.GOLD_INGOT, "&6&lTop Magnatas", lore(
                "&7Veja os jogadores mais ricos",
                "&7registrados na economia.",
                "",
                "&aClique para abrir"
        )));
        inventory.setItem(24, item(Material.BOOK, "&b&lAjuda", lore(
                "&7Veja os comandos principais",
                "&7e permissões do plugin.",
                "",
                "&aClique para visualizar"
        )));

        if (player.hasPermission("meconomy.admin")) {
            inventory.setItem(40, item(Material.REDSTONE_COMPARATOR, "&c&lPainel Admin", lore(
                    "&7Recarregue as configurações",
                    "&7diretamente pelo menu.",
                    "",
                    "&cClique para recarregar"
            )));
        } else {
            inventory.setItem(40, item(Material.BARRIER, "&c&lFechar", lore(
                    "&7Fecha este menu.",
                    "",
                    "&cClique para sair"
            )));
        }

        player.openInventory(inventory);
        play(player, Sound.CLICK);
    }

    private void openPay(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, PAY_TITLE);

        fill(inventory);
        inventory.setItem(11, item(Material.NAME_TAG, "&a&lInformar Jogador", lore(
                "&7Digite no chat o nome do jogador",
                "&7que receberá o pagamento.",
                "",
                "&aClique para continuar"
        )));
        inventory.setItem(15, item(Material.ARROW, "&c&lVoltar", lore(
                "&7Retorna ao menu principal.",
                "",
                "&cClique para voltar"
        )));

        player.openInventory(inventory);
        play(player, Sound.CLICK);
    }

    private void openTop(Player player) {
        int limit = Math.max(1, Math.min(plugin.getConfig().getInt("top-balance.limit", 10), 28));
        Map<String, Double> top = plugin.getEconomyManager().getTopBalance(limit);
        Inventory inventory = Bukkit.createInventory(null, 54, TOP_TITLE);

        fill(inventory);

        if (top == null || top.isEmpty()) {
            inventory.setItem(22, item(Material.BARRIER, "&c&lRanking vazio", lore(
                    "&7Nenhum jogador encontrado",
                    "&7no ranking de economia."
            )));
        } else {
            int slot = 10;
            int position = 1;
            for (Map.Entry<String, Double> entry : top.entrySet()) {
                if (slot >= 44) {
                    break;
                }

                Material material = position == 1 ? Material.DIAMOND : position == 2 ? Material.GOLD_INGOT : position == 3 ? Material.IRON_INGOT : Material.EMERALD;
                inventory.setItem(slot, item(material, positionColor(position) + "#" + position + " &f" + entry.getKey(), lore(
                        "&7Saldo acumulado:",
                        "&2" + currencySymbol() + " " + format(entry.getValue()),
                        "",
                        "&8Ranking atualizado em tempo real."
                )));

                position++;
                slot++;
                if (slot == 17 || slot == 26 || slot == 35) {
                    slot += 2;
                }
            }
        }

        inventory.setItem(49, item(Material.ARROW, "&c&lVoltar", lore(
                "&7Retorna ao menu principal.",
                "",
                "&cClique para voltar"
        )));

        player.openInventory(inventory);
        play(player, Sound.CLICK);
    }

    private void sendHelp(Player player) {
        player.closeInventory();
        player.sendMessage(ColorUtils.color("&8&m--------------------------------"));
        player.sendMessage(ColorUtils.color("&2&lmEconomy &8| &fAjuda"));
        player.sendMessage(ColorUtils.color("&a/money &7- Abre o menu de economia"));
        player.sendMessage(ColorUtils.color("&a/money <jogador> &7- Ver saldo de outro jogador"));
        player.sendMessage(ColorUtils.color("&a/money pay <jogador> <quantia> &7- Enviar dinheiro"));
        player.sendMessage(ColorUtils.color("&a/magnata &7- Ver ranking dos mais ricos"));
        if (player.hasPermission("meconomy.admin")) {
            player.sendMessage(ColorUtils.color("&c/money set <jogador> <quantia> &7- Definir saldo"));
            player.sendMessage(ColorUtils.color("&c/money give <jogador> <quantia> &7- Adicionar saldo"));
            player.sendMessage(ColorUtils.color("&c/money take <jogador> <quantia> &7- Remover saldo"));
            player.sendMessage(ColorUtils.color("&c/money reload &7- Recarregar configuração"));
        }
        player.sendMessage(ColorUtils.color("&8&m--------------------------------"));
        play(player, Sound.ORB_PICKUP);
    }

    private void startPayTargetInput(Player player) {
        player.closeInventory();
        paySteps.put(player.getUniqueId(), PayStep.TARGET);
        player.sendMessage(ColorUtils.color("&2&lmEconomy &8| &aDigite o nome do jogador que receberá o pagamento."));
        player.sendMessage(ColorUtils.color("&7Digite &ccancelar &7para sair."));
        play(player, Sound.CLICK);
    }

    private void handleTargetInput(Player player, String message) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(message);

        if ((!target.isOnline() && !target.hasPlayedBefore()) || target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ColorUtils.color("&cJogador inválido ou não encontrado."));
            player.sendMessage(ColorUtils.color("&7Digite outro nome ou &ccancelar&7."));
            play(player, Sound.NOTE_BASS);
            return;
        }

        payTargets.put(player.getUniqueId(), message);
        paySteps.put(player.getUniqueId(), PayStep.AMOUNT);
        player.sendMessage(ColorUtils.color("&2&lmEconomy &8| &aAgora digite a quantia que deseja enviar para &f" + safeName(target) + "&a."));
        player.sendMessage(ColorUtils.color("&7Digite &ccancelar &7para sair."));
        play(player, Sound.CLICK);
    }

    private void handleAmountInput(Player player, String message) {
        Double amount = parsePositiveAmount(message);

        if (amount == null) {
            player.sendMessage(ColorUtils.color("&cValor inválido. Digite uma quantia maior que zero."));
            play(player, Sound.NOTE_BASS);
            return;
        }

        String targetName = payTargets.get(player.getUniqueId());
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if ((!target.isOnline() && !target.hasPlayedBefore()) || target.getUniqueId().equals(player.getUniqueId())) {
            clearPay(player);
            player.sendMessage(colorMessage("messages.player-not-found"));
            play(player, Sound.NOTE_BASS);
            return;
        }

        if (!plugin.getEconomyManager().has(player, amount)) {
            player.sendMessage(colorMessage("messages.insufficient-funds"));
            play(player, Sound.NOTE_BASS);
            return;
        }

        plugin.getEconomyManager().withdraw(player, amount);
        plugin.getEconomyManager().deposit(target, amount);
        clearPay(player);

        player.sendMessage(colorMessage("messages.paid")
                .replace("%player%", safeName(target))
                .replace("%symbol%", currencySymbol())
                .replace("%amount%", format(amount)));

        if (target.isOnline() && target.getPlayer() != null) {
            target.getPlayer().sendMessage(colorMessage("messages.received")
                    .replace("%player%", player.getName())
                    .replace("%symbol%", currencySymbol())
                    .replace("%amount%", format(amount)));
        }

        play(player, Sound.LEVEL_UP);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        String title = event.getInventory().getTitle();
        if (!isMenu(title)) {
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();

        if (title.equals(MENU_TITLE)) {
            if (slot == 4) {
                openMain(player);
                return;
            }
            if (slot == 20) {
                openPay(player);
                return;
            }
            if (slot == 22) {
                openTop(player);
                return;
            }
            if (slot == 24) {
                sendHelp(player);
                return;
            }
            if (slot == 40) {
                if (player.hasPermission("meconomy.admin")) {
                    plugin.reloadConfig();
                    player.sendMessage(ColorUtils.color("&aConfiguração recarregada com sucesso!"));
                    play(player, Sound.LEVEL_UP);
                    openMain(player);
                } else {
                    player.closeInventory();
                }
            }
            return;
        }

        if (title.equals(PAY_TITLE)) {
            if (slot == 11) {
                startPayTargetInput(player);
                return;
            }
            if (slot == 15) {
                openMain(player);
            }
            return;
        }

        if (title.equals(TOP_TITLE) && slot == 49) {
            openMain(player);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // Mantido intencionalmente vazio para permitir fluxos por chat após fechar o menu.
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PayStep step = paySteps.get(player.getUniqueId());

        if (step == null) {
            return;
        }

        event.setCancelled(true);
        String message = event.getMessage().trim();

        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (message.equalsIgnoreCase("cancelar") || message.equalsIgnoreCase("cancel") || message.equalsIgnoreCase("sair")) {
                    clearPay(player);
                    player.sendMessage(ColorUtils.color("&cPagamento cancelado."));
                    play(player, Sound.NOTE_BASS);
                    return;
                }

                if (step == PayStep.TARGET) {
                    handleTargetInput(player, message);
                    return;
                }

                if (step == PayStep.AMOUNT) {
                    handleAmountInput(player, message);
                }
            }
        });
    }

    private boolean isMenu(String title) {
        return title.equals(MENU_TITLE) || title.equals(PAY_TITLE) || title.equals(TOP_TITLE);
    }

    private void fill(Inventory inventory) {
        ItemStack glass = item(Material.STAINED_GLASS_PANE, "&8", new ArrayList<String>(), (short) 7);
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, glass);
        }
    }

    private ItemStack item(Material material, String name, List<String> lore) {
        return item(material, name, lore, (short) 0);
    }

    private ItemStack item(Material material, String name, List<String> lore, short data) {
        ItemStack stack = new ItemStack(material, 1, data);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ColorUtils.color(name));

        List<String> coloredLore = new ArrayList<>();
        for (String line : lore) {
            coloredLore.add(ColorUtils.color(line));
        }
        meta.setLore(coloredLore);
        stack.setItemMeta(meta);
        return stack;
    }

    private List<String> lore(String... lines) {
        List<String> lore = new ArrayList<>();
        for (String line : lines) {
            lore.add(line);
        }
        return lore;
    }

    private String colorMessage(String path) {
        return ColorUtils.color(plugin.getConfig().getString(path, ""));
    }

    private String currencySymbol() {
        return plugin.getConfig().getString("settings.currency-symbol", "$");
    }

    private String format(double value) {
        return plugin.getEconomyManager().format(value);
    }

    private String safeName(OfflinePlayer player) {
        return player.getName() != null ? player.getName() : "Desconhecido";
    }

    private String positionColor(int position) {
        if (position == 1) {
            return ChatColor.GOLD.toString() + ChatColor.BOLD;
        }
        if (position == 2) {
            return ChatColor.GRAY.toString() + ChatColor.BOLD;
        }
        if (position == 3) {
            return ChatColor.YELLOW.toString() + ChatColor.BOLD;
        }
        return ChatColor.GREEN.toString();
    }

    private Double parsePositiveAmount(String raw) {
        try {
            double value = Double.parseDouble(raw.replace(",", "."));
            return value > 0 ? value : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private void clearPay(Player player) {
        paySteps.remove(player.getUniqueId());
        payTargets.remove(player.getUniqueId());
    }

    private void play(Player player, Sound sound) {
        try {
            player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
        } catch (Exception ignored) {
        }
    }

    private enum PayStep {
        TARGET,
        AMOUNT
    }
}
