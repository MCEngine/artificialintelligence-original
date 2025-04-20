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
 * OpenRouter implementation of {@link IMCEngineArtificialIntelligenceApi}.
 * This class communicates with the OpenRouter API to fetch AI-generated responses.
 */
public class MCEngineArtificialIntelligenceApiOpenRouter implements IMCEngineArtificialIntelligenceApi {
    private final String token;
    private final String aiModel;
    private final MCEngineArtificialIntelligenceApiUtil logger;

    public MCEngineArtificialIntelligenceApiOpenRouter(Plugin plugin) {
        this.token = plugin.getConfig().getString("ai.openrouter.token", null);
        this.aiModel = plugin.getConfig().getString("ai.openrouter.model", "mistralai/mistral-7b-instruct");
        this.logger = new MCEngineArtificialIntelligenceApiUtil(plugin.getLogger());
        logger.info("Using OpenRouter model: " + aiModel);
    }

    @Override
    public String getResponse(String message) {
        try {
            URI uri = URI.create("https://openrouter.ai/api/v1/chat/completions");
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("HTTP-Referer", "https://github.com/mcengine");
            conn.setRequestProperty("X-Title", "MCEngine AI");
            conn.setDoOutput(true);

            JSONObject payload = new JSONObject();
            payload.put("model", aiModel);
            payload.put("temperature", 0.7);

            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            messages.put(userMessage);
            payload.put("messages", messages);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payload.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int statusCode = conn.getResponseCode();
            if (statusCode != 200) {
                logger.warn("OpenRouter API returned status: " + statusCode);
                return "Error: Unable to get response from OpenRouter API";
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                responseBuilder.append(line);
            }
            in.close();

            JSONObject responseJson = new JSONObject(responseBuilder.toString());
            JSONArray choices = responseJson.getJSONArray("choices");

            if (choices.length() > 0) {
                JSONObject messageObj = choices.getJSONObject(0).getJSONObject("message");
                return messageObj.getString("content").trim();
            }

            return "No response from OpenRouter AI.";
        } catch (Exception e) {
            logger.error("OpenRouter API error: " + e.getMessage());
            return "Exception: " + e.getMessage();
        }
    }
}
