package io.github.mcengine.api.artificialintelligence.model;

import io.github.mcengine.api.artificialintelligence.IMCEngineArtificialIntelligenceApi;
import io.github.mcengine.api.artificialintelligence.util.MCEngineArtificialIntelligenceApiUtil;
import org.bukkit.plugin.Plugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * DeepSeek implementation of {@link IMCEngineArtificialIntelligenceApi}.
 * This class communicates with the DeepSeek API to fetch AI-generated responses based on user prompts.
 */
public class MCEngineArtificialIntelligenceApiDeepSeek implements IMCEngineArtificialIntelligenceApi {

    /** The Bukkit plugin instance using this AI model. */
    private final Plugin plugin;

    /** The API token used for authentication with DeepSeek. */
    private final String token;

    /** The AI model ID configured in the plugin config. */
    private final String aiModel;

    /** Logger utility to log information, warnings, and errors. */
    private final MCEngineArtificialIntelligenceApiUtil logger;

    /**
     * Constructs a DeepSeek API handler using the plugin's configuration.
     *
     * @param plugin The plugin that provides configuration and context.
     */
    public MCEngineArtificialIntelligenceApiDeepSeek(Plugin plugin) {
        this.plugin = plugin;
        this.token = plugin.getConfig().getString("deepseek.token", null);
        this.aiModel = plugin.getConfig().getString("deepseek.model", "deepseek-chat");
        this.logger = new MCEngineArtificialIntelligenceApiUtil(plugin.getLogger());
        logger.info("Using model: " + aiModel);
    }

    /**
     * Sends a user message to the DeepSeek API and retrieves the AI's response.
     *
     * @param message The user message or prompt to send.
     * @return The AI's response as a string, or an error message if the request fails.
     */
    @Override
    public String getResponse(String message) {
        try {
            URI uri = URI.create("https://api.deepseek.com/v1/chat/completions");
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Construct JSON payload
            JSONObject payload = new JSONObject();
            payload.put("model", aiModel);
            payload.put("temperature", 0.7);

            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            messages.put(userMessage);
            payload.put("messages", messages);

            // Send request body
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payload.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int statusCode = conn.getResponseCode();
            if (statusCode != 200) {
                logger.warn("DeepSeek API returned status: " + statusCode);
                return "Error: Unable to get response from DeepSeek API";
            }

            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                responseBuilder.append(line);
            }
            in.close();

            // Parse response
            JSONObject responseJson = new JSONObject(responseBuilder.toString());
            JSONArray choices = responseJson.getJSONArray("choices");

            if (choices.length() > 0) {
                JSONObject messageObj = choices.getJSONObject(0).getJSONObject("message");
                return messageObj.getString("content").trim();
            }

            return "No response from DeepSeek AI.";
        } catch (Exception e) {
            logger.error("DeepSeek API error: " + e.getMessage());
            return "Exception: " + e.getMessage();
        }
    }

    /**
     * Logs an informational message using the internal logger.
     *
     * @param message The message to log.
     */
    public void info(String message) {
        logger.info(message);
    }

    /**
     * Logs a warning message using the internal logger.
     *
     * @param message The message to log.
     */
    public void warn(String message) {
        logger.warn(message);
    }

    /**
     * Logs an error message using the internal logger.
     *
     * @param message The message to log.
     */
    public void error(String message) {
        logger.error(message);
    }
}
