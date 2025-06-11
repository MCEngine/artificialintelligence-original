package io.github.mcengine.common.artificialintelligence;

import io.github.mcengine.common.artificialintelligence.functions.calling.FunctionRule;
import io.github.mcengine.common.artificialintelligence.functions.calling.IFunctionCallingLoader;
import io.github.mcengine.common.artificialintelligence.functions.calling.json.FunctionCallingJson;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class FunctionCallingLoader {

    private final List<FunctionRule> mergedRules = new ArrayList<>();

    public FunctionCallingLoader(Plugin plugin) {
        this(plugin, false);
    }

    public FunctionCallingLoader(Plugin plugin, boolean silent) {
        String dbType = plugin.getConfig().getString("dbType", "json").toLowerCase();
        if (!silent) plugin.getLogger().info("Using DB type: " + dbType);

        File jsonFolder = null;
        if (dbType.equals("json")) {
            String path = plugin.getConfig().getString("db.json.path", "json");
            jsonFolder = new File(plugin.getDataFolder(), path);
            if (!silent) plugin.getLogger().info("Loading function rules from: " + jsonFolder.getPath());
        }

        IFunctionCallingLoader orm = switch (dbType) {
            case "json" -> new FunctionCallingJson(jsonFolder);
            default -> new FunctionCallingJson(jsonFolder);
        };

        List<FunctionRule> rules = orm.loadFunctionRules();
        if (rules != null) {
            mergedRules.addAll(rules);
            if (!silent) {
                plugin.getLogger().info("Loaded " + rules.size() + " function rules.");
                plugin.getLogger().info("This is all merge rule" + mergedRules);
            }
        } else {
            if (!silent) plugin.getLogger().warning("No function rules loaded.");
        }
    }

    public List<String> match(Player player, String message) {
        List<String> results = new ArrayList<>();
        String lowerMessage = message.toLowerCase().trim();

        for (FunctionRule rule : mergedRules) {
            for (String pattern : rule.match) {
                String lowerPattern = pattern.toLowerCase();
                if (lowerMessage.contains(lowerPattern) || lowerPattern.contains(lowerMessage)) {
                    String resolved = applyPlaceholders(rule.response, player);
                    results.add(resolved);
                    break;
                }
            }
        }

        return results;
    }

    private String applyPlaceholders(String response, Player player) {
        response = response
                .replace("{player_name}", player.getName())
                .replace("{player_uuid}", player.getUniqueId().toString())
                .replace("{time_server}", getFormattedTime(TimeZone.getDefault()))
                .replace("{time_utc}", getFormattedTime(TimeZone.getTimeZone("UTC")))
                .replace("{time_gmt}", getFormattedTime(TimeZone.getTimeZone("GMT")));

        Map<String, String> namedZones = Map.ofEntries(
                Map.entry("{time_new_york}", getFormattedTime("America/New_York")),
                Map.entry("{time_london}", getFormattedTime("Europe/London")),
                Map.entry("{time_tokyo}", getFormattedTime("Asia/Tokyo")),
                Map.entry("{time_bangkok}", getFormattedTime("Asia/Bangkok")),
                Map.entry("{time_sydney}", getFormattedTime("Australia/Sydney")),
                Map.entry("{time_paris}", getFormattedTime("Europe/Paris")),
                Map.entry("{time_berlin}", getFormattedTime("Europe/Berlin")),
                Map.entry("{time_singapore}", getFormattedTime("Asia/Singapore")),
                Map.entry("{time_los_angeles}", getFormattedTime("America/Los_Angeles")),
                Map.entry("{time_toronto}", getFormattedTime("America/Toronto"))
        );
        for (Map.Entry<String, String> entry : namedZones.entrySet()) {
            response = response.replace(entry.getKey(), entry.getValue());
        }

        for (int hour = -12; hour <= 14; hour++) {
            for (int min : new int[]{0, 30, 45}) {
                String utcLabel = getZoneLabel("utc", hour, min);
                String gmtLabel = getZoneLabel("gmt", hour, min);
                TimeZone tz = TimeZone.getTimeZone(String.format("GMT%+03d:%02d", hour, min));
                String time = getFormattedTime(tz);
                response = response.replace(utcLabel, time);
                response = response.replace(gmtLabel, time);
            }
        }

        return response;
    }

    private String getFormattedTime(TimeZone timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(timeZone);
        return sdf.format(new Date());
    }

    private String getFormattedTime(String zoneId) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(zoneId));
        return sdf.format(new Date());
    }

    private String getZoneLabel(String prefix, int hour, int minute) {
        String sign = hour >= 0 ? "plus" : "minus";
        int absHour = Math.abs(hour);
        return String.format("{time_%s_%s_%02d_%02d}", prefix, sign, absHour, minute);
    }
}
